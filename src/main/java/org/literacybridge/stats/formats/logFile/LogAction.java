package org.literacybridge.stats.formats.logFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author willpugh
 */
public enum LogAction {

  play("play"),
  playing("playing"),
  played("played"),
  category("category"),
  record("record"),
  time_recorded("time"),
  paused("paused"),
  unpaused("unpaused"),
  survey("survey"),
  shuttingDown("shutting");

  static protected final Logger logger = LoggerFactory.getLogger(LogAction.class);

  final public String actionName;

  private LogAction(String actionName) {
    this.actionName = actionName;
  }

  static LogAction lookup(final String actionName) {

    for (LogAction action : LogAction.values()) {
      if (action.actionName.equalsIgnoreCase(actionName)) {
        return action;
      }
    }

    logger.trace("Tried to find " + actionName + " but did not match.");
    return null;
  }

}
