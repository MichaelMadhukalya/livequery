package com.livequery.agent.filesystem.core

import org.apache.log4j.Logger

/**
 * FileObserver class observes and is notified of changes to the application file
 * it is listening to.
 *
 * @param fileName Name of the file that is being observed for changes
 */
class FileObserver(fileName: String) {
  val LOG: Logger = Logger.getLogger(this.getClass.getCanonicalName)

  def onNext(data: List[AnyRef]) = {
    for (d <- data) {
      LOG.debug(String.format("Record read = %s", d))
    }
  }

  def onComplete() = {
    LOG.info(String.format("Completion notification received for file"))
  }

  def onError(throwable: Throwable) = {
    LOG.error(String.format("Exception while attempting to read from file  %s : {%s}", this.fileName, throwable))
  }
}
