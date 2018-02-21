import com.monovore.decline._
import cats.implicits._

object CLI {

  case class Options(token: String,
                     db: String,
                     username: String,
                     password: String)

  val discordToken = Opts.argument[String]("token")
  val databaseUrl = Opts.argument[String]("db")
  val username = Opts.argument[String]("username")
  val password = Opts.argument[String]("password")

  val cli = Command(
    "bot",
    "my bot"
  )(
    (discordToken, databaseUrl, username, password).mapN(Options.apply)
  )

}
