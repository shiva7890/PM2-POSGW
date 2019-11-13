package com.bcgi.paymgr.posserver.common;

import com.bcgi.paymgr.posserver.PMBaseAppException;

public class ServiceLocatorException extends PMBaseAppException {
	
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(ServiceLocatorException.class);
	
    public ServiceLocatorException(String message) {
      super(message);
      logger.error("Exceptione Message: "+message);
    }

    public ServiceLocatorException(Exception exp) {
      super(exp);
      logger.error("Exceptione Message: "+exp.getMessage());
    }
    
    public ServiceLocatorException(String message, Exception exp) {
        super(exp);
        logger.error("Exceptione Message: "+message);
        logger.error("Exceptione Message: "+exp.getMessage());
      }

	
}
