/*
 * Created on Nov 17, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.config;

import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.ErrorItem;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.ListAppender;


/**
 * Code useful to most configurators.
 *  
 * @author Ceki Gulcu
 * @since 1.3
 */
abstract public class ConfiguratorBase implements Configurator {

  /**
  Defining this value makes log4j print log4j-internal debug
  statements.

 <p> The value of this string is <b>log4j.debug</b>.

 <p>Note that the search for all option names is case sensitive.  */
public static final String DEBUG_KEY = "log4j.debug";

  protected Logger getLogger(LoggerRepository repository) {
    return repository.getLogger(this.getClass().getName());
  }
  
  protected void addError(ErrorItem errorItem) {
    getErrorList().add(errorItem);
  }
  
  abstract public List getErrorList();
  
  /**
   * Attach a list appender which will be used to collect the logging events
   * generated by log4j components, including this JoranConfigurator. These
   * events will later be output when {@link #detachListAppender} method
   * is called.
   * 
   * @param repository
   */
  protected void attachListAppender(LoggerRepository repository) {
    Logger ll = repository.getLogger(Constants.LOG4J_PACKAGE_NAME);
    Appender appender = new ListAppender();
    appender.setName(Constants.TEMP_LIST_APPENDER_NAME);
    ll.addAppender(appender);
    ll.setAdditivity(false);
  }
  
  /**
   * Output the previously collected events using the current log4j 
   * configuration. When that is completed, cluse and detach the
   * ListAppender previously created by {@link #attachListAppender}.
   * 
   * @param repository
   */
  protected void detachListAppender(LoggerRepository repository) {

    Logger ll = repository.getLogger(Constants.LOG4J_PACKAGE_NAME);
    
    // FIXME: What happens if the users wanted to set the additivity flag
    // for "org.apahce.log4j" to false in the config file? We are now 
    // potentially overriding her wishes but I don't see any other way.
    ll.setAdditivity(true);
    
    ListAppender listAppender = (ListAppender) ll.getAppender(Constants.TEMP_LIST_APPENDER_NAME);
    if(listAppender == null) {
      String errMsg = "Could not find appender "+Constants.TEMP_LIST_APPENDER_NAME;
      getLogger(repository).error(errMsg);
      addError(new ErrorItem(errMsg));
      return;
    }
    
    List eventList = listAppender.getList();
    int size = eventList.size();
    for(int i = 0; i < size; i++) {
      LoggingEvent event = (LoggingEvent) eventList.get(i);
      Logger xLogger = event.getLogger();
      if (event.getLevel().isGreaterOrEqual(xLogger.getEffectiveLevel())) {
        xLogger.callAppenders(event);
      }
    }
    listAppender.clearList();
    listAppender.close();
    ll.removeAppender(listAppender);
  }
  
  static public void attachTemporaryConsoleAppender(LoggerRepository repository) {
    Logger ll = repository.getLogger(Constants.LOG4J_PACKAGE_NAME);
    
    ConsoleAppender appender = new ConsoleAppender();
    appender.setLayout(
      new PatternLayout("LOG4J-INTERNAL: %d %level [%t] %c - %m%n"));
    appender.setName(Constants.TEMP_CONSOLE_APPENDER_NAME);
    appender.activateOptions();
    ll.addAppender(appender);
  }

  static public void detachTemporaryConsoleAppender(LoggerRepository repository, List errorList) {

    Logger ll = repository.getLogger(Constants.LOG4J_PACKAGE_NAME);
    ConsoleAppender consoleAppender =
      (ConsoleAppender) ll.getAppender(Constants.TEMP_CONSOLE_APPENDER_NAME);
    if (consoleAppender == null) {
      String errMsg =
        "Could not find appender " + Constants.TEMP_LIST_APPENDER_NAME;
      errorList.add(new ErrorItem(errMsg));
      return;
    }
    consoleAppender.close();
    ll.removeAppender(consoleAppender);
  }
  
  /**
   * Dump any errors on System.out.
   * @param errorList
   */
  public void dumpErrors() {
    List errorList = getErrorList();
    for(int i = 0; i < errorList.size(); i++) {
      ErrorItem ei = (ErrorItem) errorList.get(i);
      System.out.println(ei);
      Throwable t = ei.getException();
      if(t != null) {
        t.printStackTrace(System.out);
      }
    }
  }
  
}
