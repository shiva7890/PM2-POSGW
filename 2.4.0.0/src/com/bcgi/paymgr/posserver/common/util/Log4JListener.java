package com.bcgi.paymgr.posserver.common.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Enumeration;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.core.ReConfigurable;
import org.jpos.util.LogEvent;
import org.jpos.util.LogListener;

public class Log4JListener
  implements LogListener, ReConfigurable
  
{
  Logger logger=null; 
  private Level _level;

  public Log4JListener()
  {
    setLevel(10000);
  }

  public void setLevel(int level) {
    this._level = Level.toLevel(level);
  }

  public void setLevel(String level) {
    this._level = Level.toLevel(level);
  }

  public void close()
  {
  }

  public void setConfiguration(Configuration cfg)
    throws ConfigurationException
  {
    String config = cfg.get("config");
    long watch = cfg.getLong("watch");

    if (watch == 0L) {
      watch = 60000L;
    }
    if ((config != null) && (!(config.trim().equals("")))) {
      DOMConfigurator.configureAndWatch(config, watch);
    }
    setLevel(cfg.get("priority"));
  }

  public synchronized LogEvent log(LogEvent ev)
  {
	if(logger==null ){
		  //System.out.println("This is in the Date ******************************");
		  logger = Logger.getLogger(ev.getRealm().replace('/', ':'));
	}
    //System.out.println("ev.getRealm()**************"+ev.getRealm());
    if (logger.isEnabledFor(this._level)) {
      ByteArrayOutputStream w = new ByteArrayOutputStream();
      PrintStream p = new PrintStream(w);
      ev.dump(p, "");
      logger.log(this._level, w.toString());
    }
    return ev;
  }
}