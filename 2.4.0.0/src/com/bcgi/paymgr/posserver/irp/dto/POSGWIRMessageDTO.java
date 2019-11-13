/*
 * Created on Apr 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.bcgi.paymgr.posserver.irp.dto;
import java.io.Serializable;

/**
 * @author jagadeesh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class POSGWIRMessageDTO implements Serializable
{

//	stores country Code
	private String destinationCountryCode;

	private String originCountryCode;
	// Stores the Destination Carrier Id in IRP
	private String destinationCarrier;

	//Stores the the Currency Code for Originating Carrier
	private String originCurrencyCode;

	//Stores the the Currency Code for Destination Carrier
	private String destinationCurrencyCode;

	private String baseCurrencyCode;

	private String destSubscriberMIN;

	private String destAmount;

	// The source country code Description which is two characters
	private String sourceCountry;

	//The destination country code Description which is two characters
	private String destinationCountry;
	
	private String baseAmount;
	
	private String ISOMin;

	public String getBaseCurrencyCode() {
		return baseCurrencyCode;
	}

	public void setBaseCurrencyCode(String baseCurrencyCode) {
		this.baseCurrencyCode = baseCurrencyCode;
	}

	public String getDestAmount() {
		return destAmount;
	}

	public void setDestAmount(String destAmount) {
		this.destAmount = destAmount;
	}

	public String getDestinationCarrier() {
		return destinationCarrier;
	}

	public void setDestinationCarrier(String destinationCarrier) {
		this.destinationCarrier = destinationCarrier;
	}

	public String getDestinationCountry() {
		return destinationCountry;
	}

	public void setDestinationCountry(String destinationCountry) {
		this.destinationCountry = destinationCountry;
	}

	public String getDestinationCountryCode() {
		return destinationCountryCode;
	}

	public void setDestinationCountryCode(String destinationCountryCode) {
		this.destinationCountryCode = destinationCountryCode;
	}

	public String getDestinationCurrencyCode() {
		return destinationCurrencyCode;
	}

	public void setDestinationCurrencyCode(String destinationCurrencyCode) {
		this.destinationCurrencyCode = destinationCurrencyCode;
	}

	public String getDestSubscriberMIN() {
		return destSubscriberMIN;
	}

	public void setDestSubscriberMIN(String destSubscriberMIN) {
		this.destSubscriberMIN = destSubscriberMIN;
	}

	public String getOriginCountryCode() {
		return originCountryCode;
	}

	public void setOriginCountryCode(String originCountryCode) {
		this.originCountryCode = originCountryCode;
	}

	public String getOriginCurrencyCode() {
		return originCurrencyCode;
	}

	public void setOriginCurrencyCode(String originCurrencyCode) {
		this.originCurrencyCode = originCurrencyCode;
	}

	public String getSourceCountry() {
		return sourceCountry;
	}

	public void setSourceCountry(String sourceCountry) {
		this.sourceCountry = sourceCountry;
	}

	public String getBaseAmount() {
		return baseAmount;
	}

	public void setBaseAmount(String baseAmount) {
		this.baseAmount = baseAmount;
	}

	public String getISOMin() {
		return ISOMin;
	}

	public void setISOMin(String min) {
		ISOMin = min;
	}
}
