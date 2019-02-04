package com.livequery.agent.runtime.core;

import com.livequery.common.Environment;
import com.livequery.common.INode;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 */
public class App implements INode {

  /**
   * Logger
   */
  private static final Logger logger = Logger.getLogger(App.class.getSimpleName());

  /**
   * No visibility outside this class
   */
  private App() {
  }

  public static void main(String[] args) {
    /* Init app */
    App app = new App();

    /* Basic config */
    app.basicConfig();
    logger.info(String.format("Initializing live query runtime environment"));

    /* Add shutdown hook */
    Runtime.getRuntime().addShutdownHook(new Thread(app::terminate));

    /* Start */
    app.start();
  }

  @Override
  public void start() {
  }

  @Override
  public void terminate() {
  }

  private void basicConfig() {
    /* Set properties file with root path */
    String rootPath = System.getProperty("livequery.root");
    Environment environment = new Environment(rootPath);

    /* Set up log config */
    loggingConfig(environment.getLog4jProperties());
    logger.info(String.format("Setting up basic config for live query runtime environment"));

    /* Log root path */
    logger.info(String.format("Root path of livequery environment at : %s", rootPath));

    /* Log OS level environment properties (these properties are immutable */
    System.getenv().entrySet()
        .forEach(e -> logger
            .info(String.format("Environment (OS, Hardware) property found: (%s, %s)", e.getKey(),
                e.getValue())));

    /* Log system properties (initialized as part of JVM args) */
    System.getProperties().entrySet()
        .forEach(e -> logger.info(String
            .format("System property (JVM) found: (%s, %s)", e.getKey(), e.getValue())));

    /* Log application properties */
    environment.getProperties().entrySet()
        .forEach(e -> logger.info(String
            .format("Application property (.properties) found: (%s, %s)", e.getKey(),
                e.getValue())));
  }

  private void loggingConfig(String log4jConfig) {
    PropertyConfigurator.configure(log4jConfig);
  }
}
