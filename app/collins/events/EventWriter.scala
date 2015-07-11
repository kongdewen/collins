package collins.events

import org.apache.activemq.artemis.api.core.TransportConfiguration
import org.apache.activemq.artemis.api.core.client.ActiveMQClient
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory

import play.api.Logger

import collins.models.asset.AllAttributes

import akka.actor.Actor

case class Message(category: Category, property: String, asset: AllAttributes)

class EventWriter extends Actor {
  private[this] val logger = Logger(getClass)

  def receive = {
    case m: Message =>
      logger.trace("Received a message of type %s for asset with tag %s for property %s".format(m.category, m.asset.asset.tag, m.property))
      val sl = ActiveMQClient.createServerLocatorWithoutHA(new TransportConfiguration(classOf[InVMConnectorFactory].getName()))
      val sf = sl.createSessionFactory()
      val session = sf.createSession()
      val producer = session.createProducer(EventsConfig.queueName)
      val message = session.createMessage(false)
      message.putStringProperty("tag", m.asset.asset.tag)
      producer.send(message)
      session.close()
      sf.close()
      logger.trace("Sent event to MOM")
  }
}