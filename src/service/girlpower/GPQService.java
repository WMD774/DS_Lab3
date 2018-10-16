package service.girlpower;

import javax.jws.WebService;
//import javax.jws.soap.SOAPBinding;
//import javax.jws.soap.SOAPBinding.Style;
//import javax.jws.soap.SOAPBinding.Use;

import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.BusinessService;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

//import client.Server;
//import service.auldfellas.AFQService;
//import service.broker.LocalBrokerService;
import service.core.AbstractQuotationService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationService;

/**
 * Implementation of the Girl Power insurance quotation service.
 * 
 * @author Rem
 *
 */

@WebService(endpointInterface = "service.core.QuotationService",
serviceName="QuotationService")
//@WebService(
//		serviceName="BrokerService",
//		targetNamespace="http://core.service/",
//		portName="BrokerServicePort"
//)
//@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)

public class GPQService extends AbstractQuotationService implements QuotationService {
	
	public static final String GPQAddress = "http://localhost:9003/StockService/GetStockQuote";

//	private static UDDIClerk clerk = null;
//	
//	public GPQService() {
//		// Step.1 Begin
//    	// create a UDDIClerk object
//		try {
//			UDDIClient uddiClient = new UDDIClient("META-INF/uddi.xml");
//			clerk = uddiClient.getClerk("default");
//			if (clerk == null)
//				throw new Exception("the clerk wasn't found, check the config file!");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		//Step.1 Finish
//	}
//	
//	public UDDIClerk getClerk() {
//		return GPQService.clerk;
//	}
//	
//	public boolean checkService() throws Exception {
//		UDDISecurityPortType security = null;
//		UDDIInquiryPortType inquiry = null;
//		
//		try {
//			UDDIClient client = new UDDIClient("META-INF/simple-browse-uddi.xml");
//
//			Transport transport = client.getTransport("default");
//
//			security = transport.getUDDISecurityService();
//			inquiry = transport.getUDDIInquiryService();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		String token = WebServices.WebServicesClientHelper.getAuthKey(security, "uddi", "uddi");
//	    BusinessList findBusiness = WebServices.WebServicesClientHelper.partialBusinessNameSearch(inquiry, token,
//	              "GPQ" + UDDIConstants.WILDCARD);
//	    
//		return (findBusiness.getBusinessInfos() == null);
//	}
//	
//	// Step.4 Begin
//	// publish the service to jUDDI
//	public void publish(UDDIClerk clerk) {
//		try {
//			String myBusKey = WebServices.WebServicesHelper.createBusiness("GPQService", clerk);
//
//			BusinessService myService = WebServices.WebServicesHelper.createWSDLService("GPQService", myBusKey, GPQAddress);
//			BusinessService svc = clerk.register(myService);
//			if (svc == null) {
//				System.out.println("Save failed!");
//				System.exit(1);
//			}
//
//			String myServKey = svc.getServiceKey();
//			System.out.println("myService key:  " + myServKey);
//			
//			clerk.discardAuthToken();
//			System.out.println("GPQ Business Registered!");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	// Step.4 Finish
	
// All references are to be prefixed with an DD (e.g. DD001000)
	public static final String PREFIX = "GP";
	public static final String COMPANY = "Girl Power Inc.";
	/**
	 * Quote generation:
	 * 50% discount for being female
	 * 20% discount for no penalty points
	 * 15% discount for < 3 penalty points
	 * no discount for 3-5 penalty points
	 * 100% penalty for > 5 penalty points
	 * 5% discount per year no claims
	 */
	@Override
	public Quotation generateQuotation(ClientInfo info) {
		// Create an initial quotation between 600 and 1000
		double price = generatePrice(600, 400);
		
		// Automatic 50% discount for being female
		int discount = (info.gender == ClientInfo.FEMALE) ? 50:0;
		
		// Add a points discount
		discount += getPointsDiscount(info);
		
		// Add a no claims discount
		discount += getNoClaimsDiscount(info);
		
		// Generate the quotation and send it back
		return new Quotation(COMPANY, generateReference(PREFIX), (price * (100-discount)) / 100);
	}

	private int getNoClaimsDiscount(ClientInfo info) {
		return 5*info.noClaims;
	}

	private int getPointsDiscount(ClientInfo info) {
		if (info.points == 0) return 20;
		if (info.points < 3) return 15;
		if (info.points < 6) return 0;
		return -100;	
	}

}
