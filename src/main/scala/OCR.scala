import java.awt.Color
import java.awt.image.{BufferedImage, RenderedImage}
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

import net.sourceforge.tess4j.Tesseract
import net.sourceforge.tess4j.util.{ImageHelper, LoadLibs}

import scala.math.{max, min}
import org.apache.lucene.search.spell.{
  JaroWinklerDistance,
  LuceneLevenshteinDistance
}

class OCR {

  val distance = new LuceneLevenshteinDistance //new JaroWinklerDistance()

  val chars = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') ++ ":[]-=()"

  val t = new Tesseract();
  t.setLanguage("evesansneue2")
  t.setTessVariable("load_system_dawg", "false")
  t.setTessVariable("load_freq_dawg", "false")
  t.setTessVariable("tessedit_char_whitelist", chars.mkString)
  t.setDatapath("c:\\Users\\Andi\\Documents\\GitHub\\ocrdemo\\tessdata")

  val exampleurl = new URL("https://i.imgur.com/sKOPmAA.png")

  def colourGate(i: BufferedImage): BufferedImage = {
    for {
      x <- 0 until i.getData.getWidth
      y <- 0 until i.getData.getHeight
    } yield {
      val color = i.getRGB(x, y)
      val red = (color >>> 16) & 0xFF
      val green = (color >>> 8) & 0xFF
      val blue = (color >>> 0) & 0xFF
      val lumens = (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255
      if (lumens > 0.6) {
        ()
      } else {
        i.setRGB(x, y, new Color(0, 0, 0).getRGB)
      }
    }
    i
  }

  def scale(i: BufferedImage): BufferedImage =
    ImageHelper.getScaledInstance(i, i.getWidth * 10, i.getHeight * 10)

  def extractStructureName(str: String) = {
     str
      .split('\n')
      .find(_.contains(" - "))
      .flatMap {
        _.split(" - ").lastOption
      }
  }

  val replacements = {
    val rs = List(
      ('0', 'O'),
      ('S', '5'),
      ('Y', 'W'),
      ('V', 'Y'),
      ('Y', 'W'),
      ('I', '1')
    )
    (rs ++ rs.map(x => (x._2, x._1))).toMap
  }

  def ocrpermutations(str: String) = {
    val indexes = str.zipWithIndex.collect {
      case (c, i) if replacements.contains(c) => i
    }
    if (indexes.length == 0) {
      List(str)
    } else {
      val gene = List(true, false)
      val genes = List.fill(indexes.length)(gene)
      CartesianProduct(genes).map { gs =>
        gs.zip(indexes).foldLeft(str) {
          case (s, (b, i)) =>
            if (b) {
              s.toList.zipWithIndex.collect {
                case (c, ix) if ix == i => replacements(c)
                case (c, ix) => c
              }.mkString
            } else {
              s
            }
        }
      }
    }
  }

  def extractTimer(str: String) = {
    str
      .split('\n')
      .filter(_.contains(": "))
      .map(_.split(": ")(1).toUpperCase)
      .flatMap { time =>
        ocrpermutations(time)
          .map(_.toLowerCase)
          .map(TimeParser.parser.parse(_))
      }
      .sortBy(0 - _.index)
      .headOption
      .map { _.get.value }
  }

  def extractSolarSystem(str: String,
                         systems: List[String]): Option[(String, String)] = {
    str
      .split('\n')
      .find(_.contains(" - "))
      .flatMap {
        _.split(" - ").headOption.map(_.split(" ").last)
      }
      .map { system =>
        for {
          possibilities <- List(ocrpermutations(system))
          s <- systems
          p <- possibilities
        } yield {
          (p, s, distance.getDistance(p, s))
        }
      }
      .flatMap(_.sortBy(0 - _._3).map(r => (r._1, r._2)).headOption)
  }

  def doOCR(url: URL, systems: List[String]) = {
    val img = ImageIO.read(url) // read the image from the URL
    val gated = colourGate(img) // colour gate it
    val scaled = scale(gated) // scale it up
    val text = t.doOCR(scaled) // read the text
    val system = extractSolarSystem(text, systems)
    val structureName = extractStructureName(text)
    val timer = extractTimer(text)

    println(
      s"""I parsed this raw text out of it:
        |=====
        |$text
        |=====
        |I believe the system can be corrected to ${system.map(_._1)} which means it's actual system is ${system.map(_._2)}
        |The timer is ${timer}
        |The structure's name is: ${structureName}
      """.stripMargin
    )
    (system, structureName, timer)
  }
}
