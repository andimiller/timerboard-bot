import fs2.StreamApp
import fs2._
import doobie._
import doobie.implicits._
import doobie.hikari._
import doobie.hikari.implicits._
import cats._
import cats.effect._
import cats.implicits._

object Main extends StreamApp[IO] {

  override def stream(
      args: List[String],
      requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    for {
      parsed <- Stream.eval(IO { CLI.cli.parse(args) })
      res <- Stream.eval(
        parsed.fold(
          h => {
            IO {
              println(h)
              StreamApp.ExitCode.Error
            }
          },
          c => {
            for {
              xa <- HikariTransactor.newHikariTransactor[IO](
                "org.postgresql.Driver",
                c.db,
                c.username,
                c.password
              )
              systems <- DB.getSystems().transact(xa) guarantee xa.shutdown
              _ <- IO {
                println(systems)
                val ocr = new OCR()
                val result = ocr.doOCR(ocr.exampleurl, systems)
                println(result)
              }
              exit <- IO.pure(StreamApp.ExitCode.Success)
            } yield exit
          }
        )
      )
    } yield res
  }
}
