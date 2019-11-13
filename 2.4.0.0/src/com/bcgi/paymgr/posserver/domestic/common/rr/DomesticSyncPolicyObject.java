/**
 * 
 */
package com.bcgi.paymgr.posserver.domestic.common.rr;

import java.util.ArrayList;

import com.bcgi.paymgr.posserver.common.ServiceLocatorException;
import com.bcgi.rrobin.policy.LoadBalancePolicy;

/**
 * @author venugopalp
 *
 */
public class DomesticSyncPolicyObject implements LoadBalancePolicy{

	private RRDomesticService rrDomesticService = null;
	private static LoadBalancePolicy me;

	static {
		try {
			me = new DomesticSyncPolicyObject();
		} catch(Exception se) {
			System.err.println(se);
			se.printStackTrace(System.err);
		}
	}
	private DomesticSyncPolicyObject() throws ServiceLocatorException  {
		try {
			rrDomesticService = new RRDomesticService();
		} 
		catch (Exception e) {
			throw new ServiceLocatorException(e);
		}
	}
	static public LoadBalancePolicy getInstance() {
		return me;
	}
	/* (non-Javadoc)
	 * @see com.bcgi.rrobin.policy.LoadBalancePolicy#loadObject()
	 */
	public ArrayList loadObject()
	{
		ArrayList resourceLst = rrDomesticService.getEJBProperties(true);
		return resourceLst;
	}
	/* (non-Javadoc)
	 * @see com.bcgi.rrobin.policy.LoadBalancePolicy#reloadObject()
	 */
	public ArrayList reloadObject() {
		ArrayList resourceLst = rrDomesticService.getEJBProperties(true);
		return resourceLst;
	}
}
