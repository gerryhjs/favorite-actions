package utils

import java.util.logging.Logger

fun DEBUG(anything: Any) {
  Logger.getGlobal().info(anything.toString())
}