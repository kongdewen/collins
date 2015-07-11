package collins.events

import java.io.File

import collins.util.MessageHelper
import collins.util.config.Configurable

object EventsConfig extends Configurable {

  object Messages extends MessageHelper("events") {
    def invalidJournalDir(t: String) =
      messageWithDefault("deploymentConfig", "event server deployment configuration %s is invalid".format(t), t)
  }

  override val namespace = "events"
  override val referenceConfigFilename = "events_reference.conf"

  def useEmbeddedServer = getBoolean("useEmbeddedServer", true)
  def enabled = getBoolean("enabled", false)
  def queueName = getString("queueName", "collins.events")

  override protected def validateConfig() {
  }
}