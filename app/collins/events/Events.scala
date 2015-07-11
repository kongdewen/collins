package collins.events

import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ
import play.api.Logger
import play.api.Play.current
import play.api.libs.concurrent.Akka
import akka.actor.Props
import akka.routing.FromConfig
import collins.callbacks.Callback
import java.io.File
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl
import org.apache.activemq.artemis.api.core.TransportConfiguration
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMAcceptorFactory
import org.apache.activemq.artemis.core.server.ActiveMQServers

object Events {
  private[this] val logger = Logger(getClass)
  def setupAmqp() {
    if (EventsConfig.enabled) {
      logger.trace("Posting of events is enabled")
      if (EventsConfig.useEmbeddedServer) {
        logger.trace("Using an embedded server for events")
        val configuration = new ConfigurationImpl();
        //we only need this for the server lock file
        // configuration.setJournalDirectory("target/data/journal");
        configuration.setPersistenceEnabled(false);
        configuration.setSecurityEnabled(false);
        configuration.getAcceptorConfigurations().add(new TransportConfiguration(classOf[InVMAcceptorFactory].getName()));

        // Step 2. Create and start the server
        val server = ActiveMQServers.newActiveMQServer(configuration);
        server.start()
        initializeCallbacks()
      } else {

      }
    }
  }

  private[this] def initializeCallbacks() {
    val writer = Akka.system.actorOf(Props[EventWriter].withRouter(FromConfig()), name = "event_writer")

    val callback = EventsCallbackHandler(writer)
    Callback.on("asset_update", callback)
    Callback.on("asset_create", callback)
    Callback.on("asset_delete", callback)
    Callback.on("asset_purge", callback)
    Callback.on("ipAddresses_create", callback)
    Callback.on("ipAddresses_update", callback)
    Callback.on("ipAddresses_delete", callback)
  }
}