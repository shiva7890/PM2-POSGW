
package com.bcgi.paymgr.posserver.irp.dto;


public class POSGWAccountReversalDTO extends BasePOSGWMessageDTO {

	private String transactionAmount;
	private String storeId;
	private String subAgentId;
	private String rechargeReferenceNumber;
	private String rechargeAuthorizationNumber;
	private String paymentType;	
	private String transactionStatus;	
    private String prepaidAccBalance;
    private String captureDate;
    private String modality;
    private String orderId;
    private String validationId;
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
	 * @return Returns the transactionAmount.
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
	 * @return Returns the pinStatus.
	 */
	

	/**
	 * @param paymentType The paymentType to set.
	 */
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	/**
	* @return Returns the paymentType.
	*/
	public String getPaymentType() {
		return paymentType;
	}

	public String toString(){
		String line_separator = System.getProperty("line.separator");

		StringBuffer buffer = new StringBuffer();
		buffer.append(line_separator);
		buffer.append(line_separator);
		buffer.append("POSAccountRechargeDTO Data: ");
		buffer.append(line_separator);
		buffer.append("   requestType: "+getRequestType());
		buffer.append(line_separator);
		buffer.append("   processingCode: "+getProcessingCode());
		buffer.append(line_separator);
		buffer.append("   referenceNumer:"+getReferenceNumber());
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

		buffer.append("   transactionAmount:"+transactionAmount);
		buffer.append(line_separator);
		buffer.append("   storeId:"+storeId);
		buffer.append(line_separator);
		buffer.append("   subAgentId:"+subAgentId);
		buffer.append(line_separator);
		
		buffer.append("   transactionState:"+transactionStatus);

		buffer.append(line_separator);
		buffer.append(line_separator);
		return buffer.toString();
	}
	public String getPrepaidAccBalance() {
		return prepaidAccBalance;
	}
	public void setPrepaidAccBalance(String prepaidAccBalance) {
		this.prepaidAccBalance = prepaidAccBalance;
	}
	public String getTransactionStatus() {
		return transactionStatus;
	}
	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	public String getCaptureDate() {
		return captureDate;
	}
	public void setCaptureDate(String captureDate) {
		this.captureDate = captureDate;
	}
	
	public String getModality() {
		return modality;
	}
	public void setModality(String modality) {
		this.modality = modality;
	}
	public String getRechargeReferenceNumber() {
		return rechargeReferenceNumber;
	}
	public void setRechargeReferenceNumber(String rechargeReferenceNumber) {
		this.rechargeReferenceNumber = rechargeReferenceNumber;
	}
	public String getRechargeAuthorizationNumber() {
		return rechargeAuthorizationNumber;
	}
	public void setRechargeAuthorizationNumber(String rechargeAuthorizationNumber) {
		this.rechargeAuthorizationNumber = rechargeAuthorizationNumber;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getValidationId() {
		return validationId;
	}
	public void setValidationId(String validationId) {
		this.validationId = validationId;
	}
	
	
	
	


}

