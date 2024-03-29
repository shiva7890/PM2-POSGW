/*
 * Copyright (c) 2000 jPOS.org.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the jPOS project
 *    (http://www.jpos.org/)". Alternately, this acknowledgment may
 *    appear in the software itself, if and wherever such third-party
 *    acknowledgments normally appear.
 *
 * 4. The names "jPOS" and "jPOS.org" must not be used to endorse
 *    or promote products derived from this software without prior
 *    written permission. For written permission, please contact
 *    license@jpos.org.
 *
 * 5. Products derived from this software may not be called "jPOS",
 *    nor may "jPOS" appear in their name, without prior written
 *    permission of the jPOS project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE JPOS PROJECT OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the jPOS Project.  For more
 * information please see <http://www.jpos.org/>.
 */

package com.bcgi.paymgr.posserver.domestic.panama.msgpackager;
       

import org.jpos.iso.IFA_AMOUNT;
import org.jpos.iso.IFA_BINARY;
import org.jpos.iso.IFA_BITMAP;
import org.jpos.iso.IFA_LLCHAR;
import org.jpos.iso.IFA_LLLCHAR;
import org.jpos.iso.IFA_LLLNUM;
import org.jpos.iso.IFA_LLNUM;
import org.jpos.iso.IFA_NUMERIC;
import org.jpos.iso.IF_CHAR;
import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISOFieldPackager;

/**
 * ISO 8583 v1987 ASCII Packager
 *
 * @author apr@cs.com.uy
 * @version $Id: //depot/xb/Payment/2.0/POS-GW/Releases/2.4.0.0/src/com/bcgi/paymgr/posserver/domestic/panama/msgpackager/LRP_ISO87APackager.java#2 $
 * @see ISOPackager
 * @see ISOBasePackager
 * @see ISOComponent
 */
public class LRP_ISO87APackager extends ISOBasePackager {
    protected ISOFieldPackager fld[] = {
    /*000*/ new IFA_NUMERIC (  4, "MESSAGE TYPE INDICATOR"),
    /*001*/ new IFA_BITMAP  ( 16, "BIT MAP"),
    /*002*/ new IFA_LLNUM   ( 19, "PAN - PRIMARY ACCOUNT NUMBER"),
    /*003*/ new IFA_NUMERIC (  6, "PROCESSING CODE"),
    /*004*/ new IFA_NUMERIC ( 12, "AMOUNT, TRANSACTION"),
    /*005*/ new IFA_NUMERIC ( 12, "AMOUNT, SETTLEMENT"),
    /*006*/ new IFA_NUMERIC ( 12, "AMOUNT, CARDHOLDER BILLING"),
    /*007*/ new IFA_NUMERIC ( 10, "TRANSMISSION DATE AND TIME"),
    /*008*/ new IFA_NUMERIC (  8, "AMOUNT, CARDHOLDER BILLING FEE"),
    /*009*/ new IFA_NUMERIC (  8, "CONVERSION RATE, SETTLEMENT"),
    /*010*/ new IFA_NUMERIC (  8, "CONVERSION RATE, CARDHOLDER BILLING"),
    /*011*/ new IFA_NUMERIC (  6, "SYSTEM TRACE AUDIT NUMBER"),
    /*012*/ new IFA_NUMERIC (  6, "TIME, LOCAL TRANSACTION"),
    /*013*/ new IFA_NUMERIC (  4, "DATE, LOCAL TRANSACTION"),
    /*014*/ new IFA_NUMERIC (  4, "DATE, EXPIRATION"),
    /*015*/ new IFA_NUMERIC (  4, "DATE, SETTLEMENT"),
    /*016*/ new IFA_NUMERIC (  4, "DATE, CONVERSION"),
    /*017*/ new IFA_NUMERIC (  4, "DATE, CAPTURE"),
    /*018*/ new IFA_NUMERIC (  4, "MERCHANTS TYPE"),
    /*019*/ new IFA_NUMERIC (  3, "ACQUIRING INSTITUTION COUNTRY CODE"),
    /*020*/ new IFA_NUMERIC (  3, "PAN EXTENDED COUNTRY CODE"),
    /*021*/ new IFA_NUMERIC (  3, "FORWARDING INSTITUTION COUNTRY CODE"),
    /*022*/ new IFA_NUMERIC (  3, "POINT OF SERVICE ENTRY MODE"),
    /*023*/ new IFA_NUMERIC (  3, "CARD SEQUENCE NUMBER"),
    /*024*/ new IFA_NUMERIC (  3, "NETWORK INTERNATIONAL IDENTIFIEER"),
    /*025*/ new IFA_NUMERIC (  2, "POINT OF SERVICE CONDITION CODE"),
    /*026*/ new IFA_NUMERIC (  2, "POINT OF SERVICE PIN CAPTURE CODE"),
    /*027*/ new IFA_NUMERIC (  1, "AUTHORIZATION IDENTIFICATION RESP LEN"),
    //Doc n8
    /*028*/ new IFA_AMOUNT  (  9, "AMOUNT, TRANSACTION FEE"),
    //Doc n8
    /*029*/ new IFA_AMOUNT  (  9, "AMOUNT, SETTLEMENT FEE"),
    //Doc n8
    /*030*/ new IFA_AMOUNT  (  9, "AMOUNT, TRANSACTION PROCESSING FEE"),
    //Doc n8
    /*031*/ new IFA_AMOUNT  (  9, "AMOUNT, SETTLEMENT PROCESSING FEE"),
    /*032*/ new IFA_LLNUM   ( 11, "ACQUIRING INSTITUTION IDENT CODE"),
    /*033*/ new IFA_LLNUM   ( 11, "FORWARDING INSTITUTION IDENT CODE"),
     //Doc n28 LLVAR
    /*034*/ new IFA_LLCHAR  ( 28, "PAN EXTENDED"),
    //Doc z37 LLVAR
    /*035*/ new IFA_LLNUM   ( 37, "TRACK 2 DATA"),
    //Doc n104 LLLVAR
    /*036*/ new IFA_LLLCHAR (104, "TRACK 3 DATA"),
    /*037*/ new IF_CHAR     ( 12, "RETRIEVAL REFERENCE NUMBER"),
    /*038*/ new IF_CHAR     (  6, "AUTHORIZATION IDENTIFICATION RESPONSE"),
    /*039*/ new IF_CHAR     (  2, "RESPONSE CODE"),
    //IRP
    /*040*/ //new IF_CHAR   (  3, "SERVICE RESTRICTION CODE"),
    /*040*/ new IF_CHAR     (  2, "SERVICE RESTRICTION CODE"),
    //modified from 8 to 16 for terminal id as per iso doc
    /*041*/ new IF_CHAR     (  16, "CARD ACCEPTOR TERMINAL IDENTIFICACION"),
    /*042*/ new IF_CHAR     ( 15, "CARD	 ACCEPTOR IDENTIFICATION CODE" ),
    /*043*/ new IF_CHAR     ( 40, "CARD ACCEPTOR NAME/LOCATION"),
    /*044*/ //new IFA_LLCHAR  ( 25, "ADITIONAL RESPONSE DATA"),
    /*044*/ new IFA_LLCHAR  ( 27, "ADITIONAL RESPONSE DATA"),
    /*045*/ new IFA_LLCHAR  ( 76, "TRACK 1 DATA"),
    /*046*/ new IFA_LLLCHAR (999, "PACKET_CODE"),
    /*047*/ new IFA_LLLCHAR (999, "ADITIONAL DATA - NATIONAL"),
    //IRP
    /*048*/ //new IFA_LLLCHAR (999, "ADITIONAL DATA - PRIVATE"),
    /*048*/ new IFA_LLLCHAR ( 47, "ADITIONAL DATA - PRIVATE"),
    //IRP
    /*049*/ //new IF_CHAR     (  3, "CURRENCY CODE, TRANSACTION"),
    /*049*/ new IFA_NUMERIC  (  3, "CURRENCY CODE, TRANSACTION"),
    //IRP
    /*050*/ //new IF_CHAR     (  3, "CURRENCY CODE, SETTLEMENT"),
    /*050*/ new IFA_NUMERIC   (  3, "CURRENCY CODE, SETTLEMENT"),
    /*051*/ new IF_CHAR     (  3, "CURRENCY CODE, CARDHOLDER BILLING"   ),
    //Doc h16
    /*052*/ new IFA_BINARY  (  8, "PIN DATA"   ),
    //Doc n18
    /*053*/ new IFA_NUMERIC ( 16, "SECURITY RELATED CONTROL INFORMATION"),
    //Doc an120 fixed
    /*054*/ new IFA_LLLCHAR (120, "ADDITIONAL AMOUNTS"),
    /*055*/ new IFA_LLLCHAR (999, "RESERVED ISO"),
    /*056*/ new IFA_LLLCHAR (999, "RESERVED ISO"),
    //IRP
    /*057*/ //new IFA_LLLCHAR (999, "RESERVED NATIONAL"),
     /*057*/ new IFA_LLNUM (20, "RESERVED NATIONAL"),
    /*058*/ new IFA_LLLCHAR (999, "RESERVED NATIONAL"),
    /*059*/ new IFA_LLLCHAR (999, "RESERVED NATIONAL"),
    //Doc an7 LVAR
    /*060*/ //new IFA_LLLCHAR (999, "RESERVED PRIVATE"),
    /*060*/ new IFA_LLLCHAR (15, "ATM TERMINAL DATA"),
    //IRP
    /*061*/ //new IFA_LLLCHAR (999, "RESERVED PRIVATE"),
    /*061*/ new IFA_LLLCHAR (16, "CARD ISSUER AND AUTHORIZER"),
    //IRP
    /*062*/ //new IFA_LLLCHAR (999, "RESERVED PRIVATE"),
    /*062*/ new IFA_LLLCHAR (06, "RESERVED PRIVATE"),
    /*063*/ //new IFA_LLLCHAR (999, "RESERVED PRIVATE"),
    /*063*/ new IF_CHAR     ( 8, "ORDER ID"),
	/*064*/ new IF_CHAR  (  8, "VALIDATION ID"),
    //Doc h16
    /*064*/ //new IFA_BINARY  (  8, "MESSAGE AUTHENTICATION CODE FIELD"),
    //Doc h16
    /*065*/ new IFA_BINARY  (  1, "BITMAP, EXTENDED"),
    /*066*/ new IFA_NUMERIC (  1, "SETTLEMENT CODE"),
    /*067*/ new IFA_NUMERIC (  2, "EXTENDED PAYMENT CODE"),
    /*068*/ new IFA_NUMERIC (  3, "RECEIVING INSTITUTION COUNTRY CODE"),
    /*069*/ new IFA_NUMERIC (  3, "SETTLEMENT INSTITUTION COUNTRY CODE"),
    /*070*/ new IFA_NUMERIC (  3, "NETWORK MANAGEMENT INFORMATION CODE"),
    /*071*/ new IFA_NUMERIC (  4, "MESSAGE NUMBER"),
    /*072*/ new IFA_NUMERIC (  4, "MESSAGE NUMBER LAST"),
    /*073*/ new IFA_NUMERIC (  6, "DATE ACTION"),
    /*074*/ new IFA_NUMERIC ( 10, "CREDITS NUMBER"),
    /*075*/ new IFA_NUMERIC ( 10, "CREDITS REVERSAL NUMBER"),
    /*076*/ new IFA_NUMERIC ( 10, "DEBITS NUMBER"),
    /*077*/ new IFA_NUMERIC ( 10, "DEBITS REVERSAL NUMBER"),
    /*078*/ new IFA_NUMERIC ( 10, "TRANSFER NUMBER"),
    /*079*/ new IFA_NUMERIC ( 10, "TRANSFER REVERSAL NUMBER"),
    /*080*/ new IFA_NUMERIC ( 10, "INQUIRIES NUMBER"),
    /*081*/ new IFA_NUMERIC ( 10, "AUTHORIZATION NUMBER"),
    /*082*/ new IFA_NUMERIC ( 12, "CREDITS, PROCESSING FEE AMOUNT"),
    /*083*/ new IFA_NUMERIC ( 12, "CREDITS, TRANSACTION FEE AMOUNT"),
    /*084*/ new IFA_NUMERIC ( 12, "DEBITS, PROCESSING FEE AMOUNT"),
    /*085*/ new IFA_NUMERIC ( 12, "DEBITS, TRANSACTION FEE AMOUNT"),
    //doc n15
    /*086*/ new IFA_NUMERIC ( 16, "CREDITS, AMOUNT"),
    //doc n15
    /*087*/ new IFA_NUMERIC ( 16, "CREDITS, REVERSAL AMOUNT"),
    //doc n15
    /*088*/ new IFA_NUMERIC ( 16, "DEBITS, AMOUNT"),
    //doc n15
    /*089*/ new IFA_NUMERIC ( 16, "DEBITS, REVERSAL AMOUNT"),
    /*090*/ new IFA_NUMERIC ( 42, "ORIGINAL DATA ELEMENTS"),
    /*091*/ new IF_CHAR     (  1, "FILE UPDATE CODE"),
    /*092*/ new IF_CHAR     (  2, "FILE SECURITY CODE"),
    //doc n5
    /*093*/ new IF_CHAR     (  6, "RESPONSE INDICATOR"),
    /*094*/ new IF_CHAR     (  7, "SERVICE INDICATOR"),
    /*095*/ new IF_CHAR     ( 42, "REPLACEMENT AMOUNTS"),
    //doc an8
    /*096*/ new IFA_BINARY  ( 16, "MESSAGE SECURITY CODE"),
    //doc n16
    /*097*/ new IFA_AMOUNT  ( 17, "AMOUNT, NET SETTLEMENT"),
    /*098*/ new IF_CHAR     ( 25, "PAYEE"),
    /*099*/ new IFA_LLNUM   ( 11, "SETTLEMENT INSTITUTION IDENT CODE"),
    /*100*/ new IFA_LLNUM   ( 11, "RECEIVING INSTITUTION IDENT CODE"),
    /*101*/ new IFA_LLCHAR  ( 17, "FILE NAME"),
    /*102*/ new IFA_LLCHAR  ( 28, "ACCOUNT IDENTIFICATION 1"),
    /*103*/ new IFA_LLCHAR  ( 28, "ACCOUNT IDENTIFICATION 2"),
    /*104*/ new IFA_LLLCHAR (100, "TRANSACTION DESCRIPTION"),
    /*105*/ new IFA_LLLCHAR (999, "RESERVED ISO USE"),
    /*106*/ new IFA_LLLCHAR (999, "RESERVED ISO USE"),
    /*107*/ new IFA_LLLCHAR (999, "RESERVED ISO USE"),
    /*108*/ new IFA_LLLCHAR (999, "RESERVED ISO USE"),
    /*109*/ new IFA_LLLCHAR (999, "RESERVED ISO USE"),
    /*110*/ new IFA_LLLCHAR (999, "RESERVED ISO USE"),
    /*111*/ new IFA_LLLCHAR (999, "RESERVED ISO USE"),
    /*112*/ new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"),
    //doc n11 LLVAR
    /*113*/ new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"),
    /*114*/ new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"   ),
    /*115*/ new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"),
    /*116*/ new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"  ),
    /*117*/ new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"),
    /*118*/ new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"),
    /*119*/ new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"),
    /*120*/ new IFA_LLLCHAR (999, "RESERVED PRIVATE USE"),
    /*121*/ new IFA_LLLCHAR (999, "RESERVED PRIVATE USE"),
    /*122*/ new IFA_LLLCHAR (999, "RESERVED PRIVATE USE"),
    /*123*/ new IFA_LLLCHAR (999, "RESERVED PRIVATE USE"),
    /*124*/ new IFA_LLLCHAR (999, "RESERVED PRIVATE USE"),
    /*125*/ new IFA_LLLCHAR (999, "RESERVED PRIVATE USE"),
    /*126*/ //new IFA_LLLCHAR (999, "RESERVED PRIVATE USE"),
    /*126*/ new IFA_LLLCHAR (800, "TOKEN"),
    /*127*/ new IFA_LLLCHAR (999, "RESERVED PRIVATE USE"),
    //doc h16
    //IRP
    /*128*/// new IFA_BINARY  (  8, "MAC 2"),
    /*128*/ new IFA_BINARY  (  16, "MAC 2"),
        };
    public LRP_ISO87APackager() {
        super();
        setFieldPackager(fld);
    }
}
