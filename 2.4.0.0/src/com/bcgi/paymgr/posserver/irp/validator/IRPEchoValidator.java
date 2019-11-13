package com.bcgi.paymgr.posserver.irp.validator;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.common.util.DateUtil;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageDataConstant;
import com.bcgi.paymgr.posserver.irp.dto.POSGWEchoDTO;


public class IRPEchoValidator {

	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(IRPEchoValidator.class);
	
	public static String isValidReqData(POSGWEchoDTO posGWEchoDTO){
				
		return isValidData(posGWEchoDTO);

	}

	private static String isValidData(POSGWEchoDTO posGWEchoDTO)
	{
		
		String transactionDateTime = posGWEchoDTO.getTransactionDateTime();
     	logger.info("transactionDateTime"+transactionDateTime);
		String status = IRPReqMsgDataValidator.isValidTransactionDateTime(transactionDateTime);
		
        if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
		{
        	
			String monthday = transactionDateTime.substring(0,IRPMessageDataConstant.TRANSACTION_DATE_END_POSITION);
			String year = DateUtil.getYear(monthday);
			String transactionDate = year+monthday;
			
			
			status = IRPReqMsgDataValidator.isValidTransactionDate(transactionDate);
			if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
			String transactionTime = transactionDateTime.substring(IRPMessageDataConstant.TRANSACTION_TIME_START_POSITION,transactionDateTime.length());
			
			status = IRPReqMsgDataValidator.isValidTransactionTime(transactionTime);
			if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
		}
		else{
			return status;
		}
		
		String systemTraceAuditNum = posGWEchoDTO.getSystemTraceAuditNumber();
     	logger.info("systemTraceAuditNum"+systemTraceAuditNum);
		status = IRPReqMsgDataValidator.isValidSystemTraceAuditNumber(systemTraceAuditNum);
		logger.info("systemTraceAuditNum-status===isValidData==>"+status);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		String distributorId = posGWEchoDTO.getDistributorId();
     	logger.info("distributorId"+distributorId);
		status = IRPReqMsgDataValidator.isValidDistributorId(distributorId);
		logger.info("distributorId-status===isValidData==>"+status);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		String networkMgmtInfoCode = posGWEchoDTO.getNetworkMgmtInfoCode();
     	logger.info("networkMgmtInfoCode"+networkMgmtInfoCode);
		status = IRPReqMsgDataValidator.isValidNetworkMangementInfoCode(networkMgmtInfoCode);
		logger.info("networkMgmtInfoCode-status===isValidData==>"+status);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}



}

