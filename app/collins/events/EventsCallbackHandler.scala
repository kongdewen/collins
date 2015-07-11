package collins.events

import collins.callbacks.CallbackActionHandler
import akka.actor.ActorRef
import java.beans.PropertyChangeEvent
import collins.models.Asset
import collins.models.AssetMetaValue
import collins.models.IpAddresses
import play.api.Logger
import collins.models.asset.AllAttributes

case class EventsCallbackHandler(writer: ActorRef) extends CallbackActionHandler {
  private[this] val logger = Logger(getClass)

  override def apply(pce: PropertyChangeEvent) = getValueOption(pce) match {
    case None =>
    case Some(v) =>
      processEvent(pce, v)
  }

  def processEvent(pce: PropertyChangeEvent, v: AnyRef) = v match {
    case a: Asset =>
      writer ! Message(Category.Asset, pce.getPropertyName, AllAttributes.get(a))
    case i: IpAddresses =>
      writer ! Message(Category.IpAddress, pce.getPropertyName, AllAttributes.get(i.getAsset))
    case o =>
      logger.error("Unsupported type in event callback handler %s. Supported types are 'Asset' and 'IpAddresses'".format(maybeNullString(o)))
  }
}