package com.livequery.agent.filesystem.core

import org.apache.log4j.Logger

/**
 * FileChangeObserver class observes and is notified of changes to application file.
 *
 * @param fileName Name of the file that is being observed for changes
 */
class FileChangeObserver(fileName: String) {
  private val logger: Logger = org.apache.log4j.Logger.getLogger(this.getClass.getCanonicalName)

  def onNext(data: List[Any]) = {
    for (d <- data) {
      logger.debug(String.format("Record read = %s", d))
    }
  }

  def onComplete() = {
    logger.info(String.format("Completion notification received for file"))
  }

  def onError(throwable: Throwable) = {
    logger.error(String.format("Exception while attempting to read from file  %s : {%s}", this.fileName, throwable))
  }
}
