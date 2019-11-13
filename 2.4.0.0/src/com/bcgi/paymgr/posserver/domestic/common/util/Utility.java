package com.bcgi.paymgr.posserver.domestic.common.util;

import org.apache.log4j.Logger;

import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.PM_DMPOSGW_MapConstant;
import com.bcgi.pmcore.dto.GenericDTO;

public class Utility {
		private final static Logger log = Logger.getLogger(Utility.class);
		
	/**
	 * This method is used to set the Particular Error code, When remote object (PM is down) is null.
	 * @param dto (it is a type of GenericDTO)
	 * @return GenericDTO
	 */
	public static GenericDTO setErrorCode(GenericDTO dto) {
		log.info("Utility.setErrorCode() Starting==>"+dto);
		
		try {
			dto.setErrorId(PM_DMPOSGW_MapConstant.getPOSGWResponse(ServerConfigConstant.PM_SERVER_DOWN));
		} catch(Exception e) {
			log.error("Utility.setErrorCode() Exception"+dto);
		}
		
		log.info("Utility.setErrorCode() Ending ==> "+dto);
		return dto;
	}
}
