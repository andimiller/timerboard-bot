import cats._
import cats.implicits._
import cats.syntax._
import cats.effect._
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.events.{EventSubscriber, IListener}
import sx.blah.discord.handle.impl.events.{InviteReceivedEvent, ReadyEvent}
import scala.collection.JavaConverters._

object Bot {

  def create(token: String) = new ClientBuilder().withToken(token).build()

  @EventSubscriber
  def onReadyEvent(event: ReadyEvent): Unit = {
    println("ready")
  }

  @EventSubscriber
  def onInvite(event: InviteReceivedEvent): Unit = {
    println(s"got invite ${event.getInvites}")
  }

}
