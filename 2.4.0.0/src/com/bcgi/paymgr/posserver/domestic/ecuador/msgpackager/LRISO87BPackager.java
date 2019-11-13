package com.bcgi.paymgr.posserver.domestic.ecuador.msgpackager;

import org.jpos.iso.IFB_AMOUNT;
import org.jpos.iso.IFB_BINARY;
import org.jpos.iso.IFB_BITMAP;
import org.jpos.iso.IFB_LLCHAR;
import org.jpos.iso.IFB_LLLCHAR;
import org.jpos.iso.IFB_LLLBINARY;
import org.jpos.iso.IFB_LLNUM;
import org.jpos.iso.IFB_NUMERIC;
import org.jpos.iso.IF_CHAR;
import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISOFieldPackager;

/**
 * ISO 8583 v1987 BINARY Packager
 *
 * @author apr@cs.com.uy
 * @version $Id: //depot/xb/Payment/2.0/POS-GW/Releases/2.4.0.0/src/com/bcgi/paymgr/posserver/domestic/ecuador/msgpackager/LRISO87BPackager.java#1 $
 * @see ISOPackager
 * @see ISOBasePackager
 * @see ISOComponent
 */
public class LRISO87BPackager extends ISOBasePackager {
    private static final boolean pad = false;
    protected ISOFieldPackager fld[] = {
    		/*000*/   new IFB_NUMERIC (  4, "MESSAGE TYPE INDICATOR", true),  //MTI
    		/*001*/   new IFB_BITMAP  ( 16, "BIT MAP"),            //Bitmap
    		/*002*/   new IFB_LLNUM   ( 19, "PAN - PRIMARY ACCOUNT NUMBER", pad),  
    		/*003*/   new IFB_NUMERIC (  6, "PROCESSING CODE", true),    //Processing code
    		/*004*/   new IFB_NUMERIC ( 12, "AMOUNT, TRANSACTION", true),  //Transction Amt
    		/*005*/   new IFB_NUMERIC ( 12, "AMOUNT, SETTLEMENT", true),  //
    		/*006*/   new IFB_NUMERIC ( 12, "AMOUNT, CARDHOLDER BILLING", true),
    		/*007*/   new IFB_NUMERIC ( 10, "TRANSMISSION DATE AND TIME", true),
    		/*008*/   new IFB_NUMERIC (  8, "AMOUNT, CARDHOLDER BILLING FEE", true),
    		/*009*/   new IFB_NUMERIC (  8, "CONVERSION RATE, SETTLEMENT", true),
    		/*010*/   new IFB_NUMERIC (  8, "CONVERSION RATE, CARDHOLDER BILLING", true),
    		/*011*/   new IFB_NUMERIC (  6, "SYSTEM TRACE AUDIT NUMBER", true),   // System trace number
    		/*012*/   new IFB_NUMERIC (  6, "TIME, LOCAL TRANSACTION", true),     //Transaction Time
    		/*013*/   new IFB_NUMERIC (  4, "DATE, LOCAL TRANSACTION", true),     //Transaction Date
    		/*014*/   new IFB_NUMERIC (  4, "DATE, EXPIRATION", true),
    		/*015*/   new IFB_NUMERIC (  4, "DATE, SETTLEMENT", true),
    		/*016*/  new IFB_NUMERIC (  4, "DATE, CONVERSION", true),
    		/*017*/ new IFB_NUMERIC (  4, "DATE, CAPTURE", true),
    		/*018*/ new IFB_NUMERIC (  4, "MERCHANTS TYPE", true),
    		/*019*/ new IFB_NUMERIC (  3, "ACQUIRING INSTITUTION COUNTRY CODE", true),
    		/*020*/ new IFB_NUMERIC (  3, "PAN EXTENDED COUNTRY CODE", true),
    		/*021*/ new IFB_NUMERIC (  3, "FORWARDING INSTITUTION COUNTRY CODE", true),
    		/*022*/ new IFB_NUMERIC (  3, "POINT OF SERVICE ENTRY MODE", true),
    		/*023*/ new IFB_NUMERIC (  3, "CARD SEQUENCE NUMBER", true),               
    		/*024*/ new IFB_NUMERIC (  3, "NETWORK INTERNATIONAL IDENTIFIEER", true), //main distributor id 
    		/*025*/ new IFB_NUMERIC (  2, "POINT OF SERVICE CONDITION CODE", true),
    		/*026*/ new IFB_NUMERIC (  2, "POINT OF SERVICE PIN CAPTURE CODE", true),
    		/*027*/ new IFB_NUMERIC (  1, "AUTHORIZATION IDENTIFICATION RESP LEN",true),
    		/*028*/new IFB_AMOUNT  (  9, "AMOUNT, TRANSACTION FEE", true),
    		/*029*/new IFB_AMOUNT  (  9, "AMOUNT, SETTLEMENT FEE", true),
    		/*030*/new IFB_AMOUNT  (  9, "AMOUNT, TRANSACTION PROCESSING FEE", true),
    		/*031*/new IFB_AMOUNT  (  9, "AMOUNT, SETTLEMENT PROCESSING FEE", true),
    		/*032*/new IFB_LLNUM   ( 11, "ACQUIRING INSTITUTION IDENT CODE", pad),
    		/*033*/new IFB_LLNUM   ( 11, "FORWARDING INSTITUTION IDENT CODE", pad),
    		/*034*/new IFB_LLCHAR  ( 28, "PAN EXTENDED"),
    		/*035*/new IFB_LLNUM   ( 37, "TRACK 2 DATA", pad),      //subscrber number
    		/*036*/new IFB_LLLCHAR (104, "TRACK 3 DATA"),
    		/*037*/new IF_CHAR     ( 12, "RETRIEVAL REFERENCE NUMBER"),     //subscrber number
    		/*038*/new IF_CHAR     (  6, "AUTHORIZATION IDENTIFICATION RESPONSE"),
    		/*039*/new IF_CHAR     (  2, "RESPONSE CODE"),
    		/*040*/new IF_CHAR     (  3, "SERVICE RESTRICTION CODE"),
    		/*041*/new IF_CHAR     (  8, "CARD ACCEPTOR TERMINAL IDENTIFICACION"),   //terminal id
    		/*042*/new IF_CHAR     ( 15, "CARD ACCEPTOR IDENTIFICATION CODE" ),   //merchant id 
    		/*043*/ new IF_CHAR     ( 40, "CARD ACCEPTOR NAME/LOCATION"),
    		/*044*/new IFB_LLCHAR  ( 25, "ADITIONAL RESPONSE DATA"),
    		/*045*/new IFB_LLCHAR  ( 76, "TRACK 1 DATA"),
    		/*046*/new IFB_LLLCHAR (999, "ADITIONAL DATA - ISO"),
    		/*047*/new IFB_LLLCHAR (999, "ADITIONAL DATA - NATIONAL"),
    		/*048*/new IFB_LLLCHAR (999, "PACKETCODE - PRIVATE"),
    		/*049*/new IF_CHAR     (  3, "CURRENCY CODE, TRANSACTION"),  //currency code[newly introduced for costa rica]
    		/*050*/new IF_CHAR     (  3, "CURRENCY CODE, SETTLEMENT"),
    		/*051*/new IF_CHAR     (  3, "CURRENCY CODE, CARDHOLDER BILLING"   ),
    		/*052*/new IFB_BINARY  (  8, "PIN DATA"   ),
    		/*053*/new IFB_NUMERIC ( 16, "SECURITY RELATED CONTROL INFORMATION", true),
    		/*054*/new IFB_LLLBINARY (120, "ADDITIONAL AMOUNTS"),
    		/*055*/new IFB_LLLCHAR (999, "RESERVED ISO"),
    		/*056*/new IFB_LLLCHAR (999, "RESERVED ISO"),
    		/*057*/new IFB_LLLCHAR (999, "RESERVED NATIONAL"),
    		/*058*/new IFB_LLLCHAR (999, "RESERVED NATIONAL"),
    		/*059*/new IFB_LLLCHAR (999, "RESERVED NATIONAL"),
    		/*060*/new IFB_LLLCHAR (999, "RESERVED PRIVATE"), //recharge trans status or reversal trans status
    		/*061*/new IFB_LLLCHAR (999, "RESERVED PRIVATE"), // validation id 
    		/*062*/new IFB_LLLCHAR (999, "RESERVED PRIVATE"), //order id
    		/*063*/new IFB_LLLCHAR (999, "RESERVED PRIVATE"), // channel id [newly introduced for costa rica]
    		/*064*/new IFB_BINARY  (  8, "MESSAGE AUTHENTICATION CODE FIELD"),
            new IFB_BINARY  (  1, "BITMAP, EXTENDED"),
            new IFB_NUMERIC (  1, "SETTLEMENT CODE", true),
            new IFB_NUMERIC (  2, "EXTENDED PAYMENT CODE", true),
            new IFB_NUMERIC (  3, "RECEIVING INSTITUTION COUNTRY CODE", true),
            new IFB_NUMERIC (  3, "SETTLEMENT INSTITUTION COUNTRY CODE", true),
            new IFB_NUMERIC (  3, "NETWORK MANAGEMENT INFORMATION CODE", true),
            new IFB_NUMERIC (  4, "MESSAGE NUMBER", true),
            new IFB_NUMERIC (  4, "MESSAGE NUMBER LAST", true),
            new IFB_NUMERIC (  6, "DATE ACTION", true),
            new IFB_NUMERIC ( 10, "CREDITS NUMBER", true),
            new IFB_NUMERIC ( 10, "CREDITS REVERSAL NUMBER", true),
            new IFB_NUMERIC ( 10, "DEBITS NUMBER", true),
            new IFB_NUMERIC ( 10, "DEBITS REVERSAL NUMBER", true),
            new IFB_NUMERIC ( 10, "TRANSFER NUMBER", true),
            new IFB_NUMERIC ( 10, "TRANSFER REVERSAL NUMBER", true),
            new IFB_NUMERIC ( 10, "INQUIRIES NUMBER", true),
            new IFB_NUMERIC ( 10, "AUTHORIZATION NUMBER", true),
            new IFB_NUMERIC ( 12, "CREDITS, PROCESSING FEE AMOUNT", true),
            new IFB_NUMERIC ( 12, "CREDITS, TRANSACTION FEE AMOUNT", true),
            new IFB_NUMERIC ( 12, "DEBITS, PROCESSING FEE AMOUNT", true),
            new IFB_NUMERIC ( 12, "DEBITS, TRANSACTION FEE AMOUNT", true),
            new IFB_NUMERIC ( 16, "CREDITS, AMOUNT", true),
            new IFB_NUMERIC ( 16, "CREDITS, REVERSAL AMOUNT", true),
            new IFB_NUMERIC ( 16, "DEBITS, AMOUNT", true),
            new IFB_NUMERIC ( 16, "DEBITS, REVERSAL AMOUNT", true),
            new IFB_NUMERIC ( 42, "ORIGINAL DATA ELEMENTS", true),
            new IF_CHAR     (  1, "FILE UPDATE CODE"),
            new IF_CHAR     (  2, "FILE SECURITY CODE"),
            new IF_CHAR     (  6, "RESPONSE INDICATOR"),
            new IF_CHAR     (  7, "SERVICE INDICATOR"),
            new IF_CHAR     ( 42, "REPLACEMENT AMOUNTS"),
            new IFB_BINARY  ( 16, "MESSAGE SECURITY CODE"),
            new IFB_AMOUNT  ( 17, "AMOUNT, NET SETTLEMENT", pad),
            new IF_CHAR     ( 25, "PAYEE"),
            new IFB_LLNUM   ( 11, "SETTLEMENT INSTITUTION IDENT CODE", pad),
            new IFB_LLNUM   ( 11, "RECEIVING INSTITUTION IDENT CODE", pad),
            new IFB_LLCHAR  ( 17, "FILE NAME"),
            new IFB_LLCHAR  ( 28, "ACCOUNT IDENTIFICATION 1"),
            new IFB_LLCHAR  ( 28, "ACCOUNT IDENTIFICATION 2"),
            new IFB_LLLCHAR (100, "TRANSACTION DESCRIPTION"),
            new IFB_LLLCHAR (999, "RESERVED ISO USE"),
            new IFB_LLLCHAR (999, "RESERVED ISO USE"),
            new IFB_LLLCHAR (999, "RESERVED ISO USE"),
            new IFB_LLLCHAR (999, "RESERVED ISO USE"),
            new IFB_LLLCHAR (999, "RESERVED ISO USE"),
            new IFB_LLLCHAR (999, "RESERVED ISO USE"),
            new IFB_LLLCHAR (999, "RESERVED ISO USE"),
            new IFB_LLLCHAR (999, "RESERVED NATIONAL USE"),
            new IFB_LLLCHAR (999, "RESERVED NATIONAL USE"),
            new IFB_LLLCHAR (999, "RESERVED NATIONAL USE"   ),
            new IFB_LLLCHAR (999, "RESERVED NATIONAL USE"),
            new IFB_LLLCHAR (999, "RESERVED NATIONAL USE"  ),
            new IFB_LLLCHAR (999, "RESERVED NATIONAL USE"),
            new IFB_LLLCHAR (999, "RESERVED NATIONAL USE"),
            new IFB_LLLCHAR (999, "RESERVED NATIONAL USE"),
            new IFB_LLLCHAR (999, "RESERVED PRIVATE USE"),
            new IFB_LLLCHAR (999, "RESERVED PRIVATE USE"),
            new IFB_LLLCHAR (999, "RESERVED PRIVATE USE"),
            new IFB_LLLCHAR (999, "RESERVED PRIVATE USE"),
            new IFB_LLLCHAR (999, "RESERVED PRIVATE USE"),
            new IFB_LLLCHAR (999, "RESERVED PRIVATE USE"),
            new IFB_LLLCHAR (999, "RESERVED PRIVATE USE"),
            new IFB_LLLCHAR (999, "RESERVED PRIVATE USE"),
            new IFB_BINARY  (  8, "MAC 2")
        };
    public LRISO87BPackager() {
        super();
        setFieldPackager(fld);
    }
}
