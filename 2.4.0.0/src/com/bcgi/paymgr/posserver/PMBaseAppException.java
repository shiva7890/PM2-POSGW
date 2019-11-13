package com.bcgi.paymgr.posserver;

public class PMBaseAppException extends Exception{

    public PMBaseAppException(String message) {
      super(message);
      
    }

    public PMBaseAppException(Exception exp) {
        super(exp);
        exp.printStackTrace();
    }
    
    public PMBaseAppException(String message, Exception exp) {
        super(exp);
        exp.printStackTrace();
    }
    
    	
}