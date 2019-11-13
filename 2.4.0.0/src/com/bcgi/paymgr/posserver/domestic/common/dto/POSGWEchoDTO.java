/*
 * Created on Apr 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.bcgi.paymgr.posserver.domestic.common.dto;


public class POSGWEchoDTO extends BasePOSGWMessageDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String networkMgmtInfoCode;
	private String storeId;
	private String subAgentId;
	private String errorCode;

	public String getNetworkMgmtInfoCode() {
		return networkMgmtInfoCode;
	}

	public void setNetworkMgmtInfoCode(String networkMgmtInfoCode) {
		this.networkMgmtInfoCode = networkMgmtInfoCode;
	}
	
	/**
	 * @return Returns the storeId.
	 */
	public String getStoreId() {
		return storeId;
	}
	/**
	 * @param storeId The storeId to set.
	 */
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
		/**
	 * @return Returns the subAgentId.
	 */
	public String getSubAgentId() {
		return subAgentId;
	}
	/**
	 * @param subAgentId The subAgentId to set.
	 */
	public void setSubAgentId(String subAgentId) {
		this.subAgentId = subAgentId;
	}

	/**
	 * @return Returns the errorCode.
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode The errorCode to set.
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
