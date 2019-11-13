package com.bcgi.paymgr.posserver.fw.controller;

import org.jpos.iso.ISORequestListener;

public interface MessageController extends ISORequestListener {
	public void setWorkerThreadSize(int minsize , int maxsize);
}
