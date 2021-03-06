package WebServices;

//import java.rmi.RemoteException;

import org.apache.juddi.v3.client.UDDIConstants;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceDetail;
//import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

public class WebServicesClientHelper {
	/**
	 * Gets a UDDI style auth token, otherwise, appends credentials to the ws
	 * proxies (not yet implemented)
	 *
	 * @param username
	 * @param password
	 * @param style
	 * @return
	 */
	
	// Step.2 Begin
	// authentication token
	public static String getAuthKey(UDDISecurityPortType security, String username, String password) {
		try {
			GetAuthToken getAuthTokenRoot = new GetAuthToken();
			getAuthTokenRoot.setUserID(username);
			getAuthTokenRoot.setCred(password);

			AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
			return rootAuthToken.getAuthInfo();
		} catch (Exception ex) {
			System.out.println("Could not authenticate with the provided credentials " + ex.getMessage());
		}
		return null;
	}
	// Step.2 Finish

	// Step.3 Begin
	// find the Businesses you are interested in
	public static BusinessList partialBusinessNameSearch(UDDIInquiryPortType inquiry, String token, String partialName) throws Exception {
		
		System.out.println("WSCHpBNS");
		FindBusiness fb = new FindBusiness();
		fb.setAuthInfo(token);
		org.uddi.api_v3.FindQualifiers fq = new org.uddi.api_v3.FindQualifiers();
		fq.getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
		
		fb.setFindQualifiers(fq);
		Name searchname = new Name();
		searchname.setValue(partialName);
		fb.getName().add(searchname);
		return inquiry.findBusiness(fb);
	}
	// Step.3 Finish

	// Step.4 Begin
	// retrieve service information for each business you are interested in
	public static ServiceDetail getServiceDetail(UDDIInquiryPortType inquiry, String token, BusinessInfo info) throws Exception {
		GetServiceDetail gsd = new GetServiceDetail();
		for (int k = 0; k < info.getServiceInfos().getServiceInfo().size(); k++) {
			gsd.getServiceKey().add(info.getServiceInfos().getServiceInfo().get(k).getServiceKey());
		}
		gsd.setAuthInfo(token);
		return inquiry.getServiceDetail(gsd);
	}
	// Step.4 Finish
}
