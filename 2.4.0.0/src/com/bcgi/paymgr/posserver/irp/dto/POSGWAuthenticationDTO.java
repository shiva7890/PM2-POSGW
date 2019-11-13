/*
 * Created on Apr 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.bcgi.paymgr.posserver.irp.dto;


public class POSGWAuthenticationDTO extends BasePOSGWMessageDTO {

	
	private String storeId;
	private String subAgentId;
	private String transactionId;
	private String transactionAmount;
	private String emailId;
	
	/* Added on July 17th for Lockout parameters */ 
	private String startTime;
	private String endTime;
	private String location;
	private String timeZone;
	private String timeFlag;
	private String transactionFlow;
	private String authorizationNumber;

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
	 * @return Returns the transactionId.
	 */
	public String getTransactionId() {
		return transactionId;
	}
	/**
	 * @param subAgentId The subAgentId to set.
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @return Returns the transactionId.
	 */
	public String getTransactionAmount() {
		return transactionAmount;
	}
	/**
	 * @param transactionAmount The transactionAmount to set.
	 */
	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	/**
	 * @return Returns the emailId.
	 */
	public String getEmailId() {
		return emailId;
	}
	/**
	 * @param emailId The emailId to set.
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String toString(){
		String line_separator = System.getProperty("line.separator");

		StringBuffer buffer = new StringBuffer();
		buffer.append(line_separator);
		buffer.append(line_separator);
		buffer.append("POSGWAuthenticationDTO Data: ");
		buffer.append(line_separator);
		buffer.append("   transactionType: "+ getTransactionType());
		buffer.append(line_separator);
		buffer.append("   distributorId:"+getDistributorId());
		buffer.append(line_separator);
		buffer.append("   messageFormatType:"+getMessageFormatType());
		buffer.append(line_separator);
		buffer.append("   subAgentId:"+subAgentId);
		buffer.append(line_separator);
		buffer.append("   storeId:"+storeId);
		buffer.append(line_separator);
		buffer.append("   SubscriberMIN:"+getSubscriberMIN());
		buffer.append(line_separator);
		
	   /*
	 	buffer.append("   requestType: "+getRequestType());
		buffer.append(line_separator);
		buffer.append("   processingCode: "+getProcessingCode());
		buffer.append(line_separator);
		buffer.append("   referenceNumer:"+getReferenceNumber());
		buffer.append(line_separator);
		buffer.append("   responseCode:"+getResponseCode());
		buffer.append(line_separator);
		buffer.append("   carrierId:"+getCarrierId());
		buffer.append(line_separator);
		buffer.append("   transactionDate:"+getTransactionDate());
		buffer.append(line_separator);
		buffer.append("   transactionTime:"+getTransactionTime());
		buffer.append(line_separator);
		buffer.append("   transactionId:"+transactionId);
		buffer.append(line_separator);
		buffer.append("   transactionAmount:"+transactionAmount);
		buffer.append(line_separator);
        */
		buffer.append(line_separator);
		buffer.append(line_separator);

		return buffer.toString();
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getTimeFlag() {
		return timeFlag;
	}
	public void setTimeFlag(String timeFlag) {
		this.timeFlag = timeFlag;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public String getTransactionFlow() {
		return transactionFlow;
	}
	public String getAuthorizationNumber() {
		return authorizationNumber;
	}
	public void setAuthorizationNumber(String authorizationNumber) {
		this.authorizationNumber = authorizationNumber;
	}
	public void setTransactionFlow(String transactionFlow) {
		this.transactionFlow = transactionFlow;
	}
	

}
