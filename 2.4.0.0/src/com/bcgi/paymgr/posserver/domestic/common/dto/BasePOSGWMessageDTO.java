/*
 * Created on Apr 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.bcgi.paymgr.posserver.domestic.common.dto;
import java.io.Serializable;
import com.bcgi.paymgr.posserver.irp.dto.POSGWIRMessageDTO;

public abstract class BasePOSGWMessageDTO implements POSGWMessageDTO,Serializable
{
	private String requestType;
	private String processingCode;
	
	private String referenceNumber;
	private String distributorId;
	private String carrierId;
	private String subscriberMIN;
	private String transactionDate;
	private String transactionTime;
	private String replenishmentChannelId;
	private String transactionStatus;
	private String reasonId;
	private String transactionType;
	private String responseCode;
	private String errorDescription;
	
	private String systemTraceAuditNumber;
	private String transactionDateTime;
	
	//TODO: Revisit this to verify if this correct 
	private String messageFormatType;
	
	private String authorizationNumber = "";
	private String transactionId;
	private String billingSystemTransactionId;
	private String initiatedUser;
	
	private String referenceNumber1;
	private String referenceNumber2;
	 
	private POSGWIRMessageDTO irMessageDTO;

	/**
	 * @return Returns the carrierId.
	 */
	public String getCarrierId() {
		return carrierId;
	}
	/**
	 * @param carrierId The carrierId to set.
	 */
	public void setCarrierId(String carrierId) {
		this.carrierId = carrierId;
	}
	/**
	 * @return Returns the distributorId.
	 */
	public String getDistributorId() {
		return distributorId;
	}
	/**
	 * @param distributorId The distributorId to set.
	 */
	public void setDistributorId(String distributorId) {
		this.distributorId = distributorId;
	}
	/**
	 * @return Returns the processingCode.
	 */
	public String getProcessingCode() {
		return processingCode;
	}
	/**
	 * @param processingCode The processingCode to set.
	 */
	public void setProcessingCode(String processingCode) {
		this.processingCode = processingCode;
	}
	/**
	 * @return Returns the requestType.
	 */
	public String getRequestType() {
		return requestType;
	}
	/**
	 * @param requestType The requestType to set.
	 */
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	/**
	 * @return Returns the responseCode.
	 */
	public String getResponseCode() {
		return responseCode;
	}
	/**
	 * @param responseCode The responseCode to set.
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	/**
	 * @return Returns the transactionDate.
	 */
	public String getTransactionDate() {
		return transactionDate;
	}
	/**
	 * @param transactionDate The transactionDate to set.
	 */
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	/**
	 * @return Returns the transactionTime.
	 */
	public String getTransactionTime() {
		return transactionTime;
	}
	/**
	 * @param transactionTime The transactionTime to set.
	 */
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

	public String getReplenishmentChannelId() {
		return replenishmentChannelId;
	}
	public void setReplenishmentChannelId(String replenishmentChannelId) {
		this.replenishmentChannelId = replenishmentChannelId;
	}

	public String toString(){
		String line_separator = System.getProperty("line.separator");

		StringBuffer buffer = new StringBuffer();

		buffer.append(line_separator);
		buffer.append(line_separator);
		buffer.append("BasePOSMessageDTO Data: ");
		buffer.append(line_separator);
		buffer.append("   requestType: "+getRequestType());
		buffer.append(line_separator);
		buffer.append("   processingCode: "+getProcessingCode());
		buffer.append(line_separator);
		buffer.append("   referenceNumber:"+getReferenceNumber());
		buffer.append(line_separator);
		buffer.append("   distributorId:"+getDistributorId());
		buffer.append(line_separator);
		buffer.append("   responseCode:"+getResponseCode());
		buffer.append(line_separator);
		buffer.append("   carrierId:"+getCarrierId());
		buffer.append(line_separator);
		buffer.append("   transactionDate:"+getTransactionDate());
		buffer.append(line_separator);
		buffer.append("   transactionTime:"+getTransactionTime());
		buffer.append(line_separator);


		buffer.append("   requestType:"+requestType);
		buffer.append(line_separator);
		buffer.append("   processingCode:"+processingCode);
		buffer.append(line_separator);
		buffer.append("   referenceNumer:"+referenceNumber);
		buffer.append(line_separator);
		buffer.append("   distributorId:"+distributorId);
		buffer.append(line_separator);
		buffer.append("   responseCode:"+responseCode);
		buffer.append(line_separator);
		buffer.append("   carrierId:"+carrierId);
		buffer.append(line_separator);
		buffer.append("   transactionDate:"+transactionDate);
		buffer.append(line_separator);
		buffer.append("   transactionTime:"+transactionTime);
		buffer.append(line_separator);
		buffer.append(line_separator);

		return buffer.toString();
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public POSGWIRMessageDTO getIrMessageDTO() {
		return irMessageDTO;
	}
	public void setIrMessageDTO(POSGWIRMessageDTO irMessageDTO) {
		this.irMessageDTO = irMessageDTO;
	}
	public String getTransactionStatus() {
		return transactionStatus;
	}
	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	public String getReasonId() {
		return reasonId;
	}
	public void setReasonId(String reasonId) {
		this.reasonId = reasonId;
	}
	public String getMessageFormatType() {
		return messageFormatType;
	}
	public void setMessageFormatType(String messageFormatType) {
		this.messageFormatType = messageFormatType;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	public String getSubscriberMIN() {
		return subscriberMIN;
	}
	public void setSubscriberMIN(String subscriberMIN) {
		this.subscriberMIN = subscriberMIN;
	}
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public String getAuthorizationNumber() {
		return authorizationNumber;
	}
	public void setAuthorizationNumber(String authorizationNumber) {
		this.authorizationNumber = authorizationNumber;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getBillingSystemTransactionId() {
		return billingSystemTransactionId;
	}
	public void setBillingSystemTransactionId(String billingSystemTransactionId) {
		this.billingSystemTransactionId = billingSystemTransactionId;
	}
	public String getInitiatedUser() {
		return initiatedUser;
	}
	public void setInitiatedUser(String initiatedUser) {
		this.initiatedUser = initiatedUser;
	}
	public String getSystemTraceAuditNumber() {
		return systemTraceAuditNumber;
	}
	public void setSystemTraceAuditNumber(String systemTraceAuditNumber) {
		this.systemTraceAuditNumber = systemTraceAuditNumber;
	}
	public String getTransactionDateTime() {
		return transactionDateTime;
	}
	public void setTransactionDateTime(String transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
	}

	public String getReferenceNumber1() {
		return referenceNumber1;
	}
	public void setReferenceNumber1(String referenceNumber1) {
		this.referenceNumber1 = referenceNumber1;
	}
	public String getReferenceNumber2() {
		return referenceNumber2;
	}
	public void setReferenceNumber2(String referenceNumber2) {
		this.referenceNumber2 = referenceNumber2;
	}
}
