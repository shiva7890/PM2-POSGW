
package com.bcgi.paymgr.posserver.domestic.common.dto;


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
   
    private String subscriberId1;
    private String subscriberId2;
    private String subscriberId3;
    
    private String customerType;
    private String topUpId;
   
    private String overrideFraudId;
    private String paymentTypeId;
    private String errorCode;
    private boolean isPin = false;

    private String validationId;
    private String orderId;
    private String orderStatus;
    private POSGWIRMessageDTO messageDTO ;
    // Following Variables added for Panama Requirement ---- 30-Jun-2011 
    private String additionalData;
    private String atmTerminalData;
    private String cardIssuer;
    private String receivingInstitutionCode;
    private String accountIdentification;
    private String token;
    private String track2Data;
    private String settlmentDate;//added by ayub panama
    private String acqInsIdentificationCode ;//added by ayub panama
    private String originalDataEleemnts ;//added by ayub panama
    private String responceCodeOfRecharge;
    
    private String couponRedeemTransId;
    private String couponBenefitType;
    private String couponBenefitValue;
    private String connectionId;
    private String operatorID;
    
    private String packetCode;
    private String productCode;
    private String quantity;
   
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getPacketCode() {
		return packetCode;
	}
	public void setPacketCode(String packetCode) {
		this.packetCode = packetCode;
	}
	public String getOperatorID() {
		return operatorID;
	}
	public void setOperatorID(String operatorID) {
		this.operatorID = operatorID;
	}
	public String getConnectionId() {
		return connectionId;
	}
	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}
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

	/*public String toString(){
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
		buffer.append(line_separator);
		return buffer.toString();
	}*/
	
	public String toString()
	{
		String line_separator = System.getProperty("line.separator");
		StringBuffer buffer = new StringBuffer();
		buffer.append("getSubscriberId1():"+getSubscriberId1());
		buffer.append(line_separator);
		buffer.append("  getSubscriberId2():"+ getSubscriberId2());
		buffer.append(line_separator);
		buffer.append("  getSubscriberId3():"+ getSubscriberId3());
		buffer.append(line_separator);
		buffer.append("  getCustomerType():"+ getCustomerType());
		buffer.append(line_separator);
		buffer.append("  getDistributorId():"+ getDistributorId());
		buffer.append(line_separator);
		buffer.append("  getTransactionAmount():"+ getTransactionAmount());
		buffer.append(line_separator);
		buffer.append("  getSystemTraceAuditNumber():"+ getSystemTraceAuditNumber());
		buffer.append(line_separator);
		buffer.append("  getSubAgentId():"+ getSubAgentId());
		buffer.append(line_separator);
		buffer.append("  getStoreId():"+ getStoreId());
		buffer.append(line_separator);
		buffer.append("  getTopUpId():"+ getTopUpId());
		buffer.append(line_separator);
		buffer.append("  getInitiatedUser():"+ getInitiatedUser());
		buffer.append(line_separator);
		buffer.append("  getReplenishmentChannelId():"+ getReplenishmentChannelId());
		buffer.append(line_separator);
		buffer.append("  getOverrideFraudId():"+ getOverrideFraudId());
		buffer.append(line_separator);
		buffer.append("  getPaymentTypeId():"+ getPaymentTypeId());
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
	/**
	 * @return Returns the subscriberId1.
	 */
	public String getSubscriberId1() {
		return subscriberId1;
	}
	/**
	 * @param subscriberId1 The subscriberId1 to set.
	 */
	public void setSubscriberId1(String subscriberId1) {
		this.subscriberId1 = subscriberId1;
	}
	/**
	 * @return Returns the subscriberId2.
	 */
	public String getSubscriberId2() {
		return subscriberId2;
	}
	/**
	 * @param subscriberId2 The subscriberId2 to set.
	 */
	public void setSubscriberId2(String subscriberId2) {
		this.subscriberId2 = subscriberId2;
	}
	/**
	 * @return Returns the subscriberId3.
	 */
	public String getSubscriberId3() {
		return subscriberId3;
	}
	/**
	 * @param subscriberId3 The subscriberId3 to set.
	 */
	public void setSubscriberId3(String subscriberId3) {
		this.subscriberId3 = subscriberId3;
	}
	/**
	 * @return Returns the customerType.
	 */
	public String getCustomerType() {
		return customerType;
	}
	/**
	 * @param customerType The customerType to set.
	 */
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	/**
	 * @return Returns the topUpId.
	 */
	public String getTopUpId() {
		return topUpId;
	}
	/**
	 * @param topUpId The topUpId to set.
	 */
	public void setTopUpId(String topUpId) {
		this.topUpId = topUpId;
	}
	
	/**
	 * @return Returns the overrideFraudId.
	 */
	public String getOverrideFraudId() {
		return overrideFraudId;
	}
	/**
	 * @param overrideFraudId The overrideFraudId to set.
	 */
	public void setOverrideFraudId(String overrideFraudId) {
		this.overrideFraudId = overrideFraudId;
	}
	/**
	 * @return Returns the paymentTypeId.
	 */
	public String getPaymentTypeId() {
		return paymentTypeId;
	}
	/**
	 * @param paymentTypeId The paymentTypeId to set.
	 */
	public void setPaymentTypeId(String paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}
	/**
	 * @return Returns the isPin.
	 */
	public boolean isPin() {
		return isPin;
	}
	/**
	 * @param isPin The isPin to set.
	 */
	public void setPin(boolean isPin) {
		this.isPin = isPin;
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
	public String getValidationId() {
		return validationId;
	}
	public void setValidationId(String validationId) {
		this.validationId = validationId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public POSGWIRMessageDTO getMessageDTO() {
		return messageDTO;
	}
	public void setMessageDTO(POSGWIRMessageDTO messageDTO) {
		this.messageDTO = messageDTO;
	}
	
	// Following Setters and Getters are generated for Panama Requirement --- 30-Jun-2011
	public String getAdditionalData() {
		return additionalData;
	}
	public void setAdditionalData(String additionalData) {
		this.additionalData = additionalData;
	}
	public String getAtmTerminalData() {
		return atmTerminalData;
	}
	public void setAtmTerminalData(String atmTerminalData) {
		this.atmTerminalData = atmTerminalData;
	}
	public String getCardIssuer() {
		return cardIssuer;
	}
	public void setCardIssuer(String cardIssuer) {
		this.cardIssuer = cardIssuer;
	}
	public String getReceivingInstitutionCode() {
		return receivingInstitutionCode;
	}
	public void setReceivingInstitutionCode(String receivingInstitutionCode) {
		this.receivingInstitutionCode = receivingInstitutionCode;
	}
	public String getAccountIdentification() {
		return accountIdentification;
	}
	public void setAccountIdentification(String accountIdentification) {
		this.accountIdentification = accountIdentification;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getTrack2Data() {
		return track2Data;
	}
	public void setTrack2Data(String track2Data) {
		this.track2Data = track2Data;
	}
	public String getSettlmentDate() {
		return settlmentDate;
	}
	public void setSettlmentDate(String settlmentDate) {
		this.settlmentDate = settlmentDate;
	}
	public String getAcqInsIdentificationCode() {
		return acqInsIdentificationCode;
	}
	public void setAcqInsIdentificationCode(String acqInsIdentificationCode) {
		this.acqInsIdentificationCode = acqInsIdentificationCode;
	}
	public String getOriginalDataEleemnts() {
		return originalDataEleemnts;
	}
	public void setOriginalDataEleemnts(String originalDataEleemnts) {
		this.originalDataEleemnts = originalDataEleemnts;
	}
	public String getResponceCodeOfRecharge() {
		return responceCodeOfRecharge;
	}
	public void setResponceCodeOfRecharge(String responceCodeOfRecharge) {
		this.responceCodeOfRecharge = responceCodeOfRecharge;
	}
	public String getCouponBenefitType() {
		return couponBenefitType;
	}
	public void setCouponBenefitType(String couponBenefitType) {
		this.couponBenefitType = couponBenefitType;
	}
	public String getCouponBenefitValue() {
		return couponBenefitValue;
	}
	public void setCouponBenefitValue(String couponBenefitValue) {
		this.couponBenefitValue = couponBenefitValue;
	}
	public String getCouponRedeemTransId() {
		return couponRedeemTransId;
	}
	public void setCouponRedeemTransId(String couponRedeemTransId) {
		this.couponRedeemTransId = couponRedeemTransId;
	}

	
	
	

}
