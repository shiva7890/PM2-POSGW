
package com.bcgi.paymgr.posserver.irp.dto;


public class POSGWAccountRechargeDTO extends BasePOSGWMessageDTO {

	private String transactionAmount;
	private String storeId;
	private String subAgentId;
	private String accountBalance;
	private String pinNumber;
	private String pinStatus;
	private String pinExpiryDate;
	private String pinDenominationId;
	private String accountStatusCode;
	private String paymentType;	
	private String transactionStatus;	
    private String prepaidAccBalance;
    
    private String captureDate;
    private String posEntryMode;
    private String modality;
    private String cardAcceptorName;
    private String validationId;
    private String orderId;

	/**
	 * @return Returns the accountStatusCode.
	 */
	public String getAccountStatusCode() {
		return accountStatusCode;
	}
	/**
	 * @param accountStatusCode The accountStatusCode to set.
	 */
	public void setAccountStatusCode(String accountStatusCode) {
		this.accountStatusCode = accountStatusCode;
}

	/**
	 * @return Returns the pinDenominationId.
	 */
	public String getPinDenominationId() {
		return pinDenominationId;
	}

	/**
	 * @param pinDenominationId The pinDenominationId to set.
	 */
	public void setPinDenominationId(String pinDenominationId) {
		this.pinDenominationId = pinDenominationId;
}



	/**
	 * @return Returns the accountBalance.
	 */
	public String getAccountBalance() {
		return accountBalance;
	}
	/**
	 * @param accountBalance The accountBalance to set.
	 */
	public void setAccountBalance(String accountBalance) {
		this.accountBalance = accountBalance;
	}
	/**
	 * @return Returns the authorizationNumber.
	 */
	/**
	 * @return Returns the pinExpiryDate.
	 */
	public String getPinExpiryDate() {
		return pinExpiryDate;
	}
	/**
	 * @param pinExpiryDate The pinExpiryDate to set.
	 */
	public void setPinExpiryDate(String pinExpiryDate) {
		this.pinExpiryDate = pinExpiryDate;
	}
	/**
	 * @return Returns the pinNumber.
	 */
	public String getPinNumber() {
		return pinNumber;
	}
	/**
	 * @param pinNumber The pinNumber to set.
	 */
	public void setPinNumber(String pinNumber) {
		this.pinNumber = pinNumber;
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
	public String getPinStatus() {
		return pinStatus;
	}
	/**
	 * @param pinStatus The pinStatus to set.
	 */
	public void setPinStatus(String pinStatus) {
		this.pinStatus = pinStatus;
	}

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
		buffer.append("   accountBalance:"+accountBalance);
		buffer.append(line_separator);
		buffer.append("   pinNumber:"+pinNumber);
		buffer.append(line_separator);
		buffer.append("   pinStatus:"+pinStatus);
		buffer.append(line_separator);
		buffer.append("   pinExpiryDate:"+pinExpiryDate);
		buffer.append(line_separator);
		buffer.append("   pinDenominationId:"+pinDenominationId);
		buffer.append(line_separator);
		buffer.append("   accountStatusCode:"+accountStatusCode);
		buffer.append(line_separator);
		buffer.append("   transactionState:"+transactionStatus);
		buffer.append(line_separator);
		buffer.append("   validationId:"+validationId);
		buffer.append(line_separator);
		buffer.append("   orderId:"+orderId);

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
	public String getPosEntryMode() {
		return posEntryMode;
	}
	public void setPosEntryMode(String posEntryMode) {
		this.posEntryMode = posEntryMode;
	}
	public String getModality() {
		return modality;
	}
	public void setModality(String modality) {
		this.modality = modality;
	}
	public String getCardAcceptorName() {
		return cardAcceptorName;
	}
	public void setCardAcceptorName(String cardAcceptorName) {
		this.cardAcceptorName = cardAcceptorName;
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
