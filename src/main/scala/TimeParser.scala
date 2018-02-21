object TimeParser {
  import fastparse.all._
  import scala.concurrent.duration._

  val digits = CharIn('0' to '9')
  val times = Map(
    "d" -> {i: Int => i.days},
    "h" -> {i: Int => i.hours},
    "m" -> {i: Int => i.minutes},
    "s" -> {i: Int => i.seconds}
  )
  val time: Parser[Duration] = P(digits.rep(max=3).! ~ CharIn("dhms").! ~ " ".?).map { case (digits, unit) =>
    times(unit)(digits.toInt)
  }
  val parser: Parser[Seq[Duration]] = time.rep

}
