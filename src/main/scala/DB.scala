import doobie._
import doobie.implicits._
import cats.effect.IO

object DB {


  def getSystems(): ConnectionIO[List[String]] =
    sql"""select "solarSystemName" from "mapSolarSystems"""".query[String].list

}
