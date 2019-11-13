package com.bcgi.paymgr.posserver.delegate;

import com.bcgi.paymgr.posserver.PMBaseAppException;

public class DelegateException extends PMBaseAppException {
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(DelegateException.class);

    public DelegateException(String message) {
      super(message);
      logger.error("Exceptione Message: "+message);
    }

    public DelegateException(Exception exp) {
      super(exp);
      logger.error("Exceptione Message: "+exp.getMessage());
    }

    public DelegateException(String message, Exception e) {
        super(e);
        logger.error("Exceptione Message: "+message);
      }


}
