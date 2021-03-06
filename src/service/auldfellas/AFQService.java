package service.auldfellas;

import javax.jws.WebService;
//import javax.jws.soap.SOAPBinding;
//import javax.jws.soap.SOAPBinding.Style;
//import javax.jws.soap.SOAPBinding.Use;
//import javax.xml.ws.Endpoint;
import javax.xml.ws.Endpoint;

import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.BusinessService;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

//import client.Server;
//import client.Server;
//import service.broker.LocalBrokerService;
import service.core.AbstractQuotationService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationService;
//import util.WebServicesClientHelper;

/**
 * Implementation of the AuldFellas insurance quotation service.
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

public class AFQService extends AbstractQuotationService implements QuotationService {
	
	public static final String AFQAddress = "http://localhost:9001/StockService/GetStockQuote";
	
	private static UDDIClerk clerk = null;

//	public AFQService() {
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
	
//	public void createUDDIClerk() {
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
//		
//		
//		Endpoint.publish(AFQAddress, new AFQService());
//	}
	
//	public UDDIClerk getClerk() {
//		return AFQService.clerk;
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
//	              "AFQ" + UDDIConstants.WILDCARD);
//	    
//		return (findBusiness.getBusinessInfos() == null);
//	}
	
//	// Step.4 Begin
//	// publish the service to jUDDI
//	public void publish(UDDIClerk clerk) {
//		try {
//			String myBusKey = WebServices.WebServicesHelper.createBusiness("AFQService", clerk);
//
//			BusinessService myService = WebServices.WebServicesHelper.createWSDLService("AFQService", myBusKey, AFQAddress);
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
//			System.out.println("AFQ Business Registered!");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	// Step.4 Finish
	
	
	// All references are to be prefixed with an AF (e.g. AF001000)
	public static final String PREFIX = "AF";
	public static final String COMPANY = "Auld Fellas Ltd.";
	/**
	 * Quote generation:
	 * 30% discount for being male
	 * 2% discount per year over 60
	 * 20% discount for less than 3 penalty points
	 * 50% penalty (i.e. reduction in discount) for more than 60 penalty points 
	 */
	@Override
	public Quotation generateQuotation(ClientInfo info) {
		// Create an initial quotation between 600 and 1200
		double price = generatePrice(600, 600);
		
		// Automatic 30% discount for being male
		int discount = (info.gender == ClientInfo.MALE) ? 30:0;
		
		// Automatic 2% discount per year over 60...
		discount += (info.age > 60) ? (2*(info.age-60)) : 0;
		
		// Add a points discount
		discount += getPointsDiscount(info);
		
		// Generate the quotation and send it back
		return new Quotation(COMPANY, generateReference(PREFIX), (price * (100-discount)) / 100);
	}

	private int getPointsDiscount(ClientInfo info) {
		if (info.points < 3) return 20;
		if (info.points <= 6) return 0;
		return -50;
	}

}
