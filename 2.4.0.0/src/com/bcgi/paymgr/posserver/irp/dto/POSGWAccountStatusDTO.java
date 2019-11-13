
package com.bcgi.paymgr.posserver.irp.dto;


public class POSGWAccountStatusDTO extends BasePOSGWMessageDTO {

	private String transactionAmount;
	private String storeId;
	private String subAgentId;
	private String accountStatusCode;
	private String paymentType;	
	private String transactionStatus;	
    private String captureDate;
    private String modality;
    
   
    

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
		buffer.append("POSAccountStatusDTO Data: ");
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
		buffer.append("   accountStatusCode:"+accountStatusCode);
		buffer.append(line_separator);
		buffer.append("   transactionState:"+transactionStatus);

		buffer.append(line_separator);
		buffer.append(line_separator);
		return buffer.toString();
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

}

