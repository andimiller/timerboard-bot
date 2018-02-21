import org.scalatest.{FlatSpec, MustMatchers}

class OCRSpec extends FlatSpec with MustMatchers {


  "ExtractSolarSystem" must "extract solar systems" in {
    val input =""": :: s
                 |0sW-OP - Fortizar [REDGU]4-DNA-AcFortizarZI [1] 523 km o
                 |- Reinforced: 6d 17h 46m 8s [LOW POWER]""".stripMargin
    val o = new OCR()
    val (corrected, target) = o.extractSolarSystem(input, List("OSW-0P", "im gay", "Amarr", "Game Over", "Jita")).get
    corrected must equal ("OsW-0P")
    target must equal ("OSW-0P")
  }

  "ExtractSolarSystem" must "also extract solar systems" in {
    val input = """(shipZI
                  |a 2H-ZHM - Bus-stop Perrigenstreet Ioship nu
                  |piIOt:
                  |[2] 12 km
                  |Reinforced: 1d 22h 49m 4s
                  |""".stripMargin
    val o = new OCR()
    val (corrected, target) = o.extractSolarSystem(input, List("OSW-0P", "im gay", "Amarr", "Game Over", "Jita", "2V-ZHM")).get
  }

  "crossproduct" must "do what I think" in {
    val gene = List(true, false)
    val genes = List.fill(10)(gene)
    CartesianProduct(genes).length must equal(1024)
  }

  "ocrpermutations" must "do permutations for OCR mistakes" in {
    val o = new OCR()
    o.ocrpermutations("0SHIT") must equal(List("O5H1T", "O5HIT", "OSH1T", "OSHIT", "05H1T", "05HIT", "0SH1T", "0SHIT"))
  }

  import scala.concurrent.duration._
  "ExtractTimer" must "extract the timer" in {
    val input =""": :: s
                 |0sW-OP - Fortizar [REDGU]4-DNA-AcFortizarZI [1] 523 km o
                 |- Reinforced: 6d 17h 46m 8s [LOW POWER]""".stripMargin
    val o = new OCR()
    val r = o.extractTimer(input)
    println(r)
  }

}
