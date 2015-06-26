package collins.graphs

import play.api.Application
import play.twirl.api.Content

import collins.models.asset.AssetView

trait GraphView {
  val app: Application
  def get(asset: AssetView): Option[Content]
  def isGraphable(asset: AssetView): Boolean = true
  def validateConfig() {
  }
}
