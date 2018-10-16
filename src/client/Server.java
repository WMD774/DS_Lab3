package client;

//import java.text.NumberFormat;

import javax.xml.ws.Endpoint;

import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.BusinessService;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

//import org.apache.juddi.v3.client.config.UDDIClerk;
//import org.apache.juddi.v3.client.config.UDDIClient;

import service.auldfellas.AFQService;
import service.broker.LocalBrokerService;
//import service.core.BrokerService;
//import service.core.ClientInfo;
//import service.core.Quotation;
import service.dodgydrivers.DDQService;
import service.girlpower.GPQService;
//import service.registry.ServiceRegistry;

public class Server {
//	public static final String BROKER_SERVICE = "bs-BrokerService";  
//	public static final String GIRL_POWER_SERVICE = "qs-GirlPowerService";  
//	public static final String AULD_FELLAS_SERVICE = "qs-AuldFellasService";  
//	public static final String DODGY_DRIVERS_SERVICE = "qs-DodgyDriversService";
//	public static final String AFQAddress = "http://localhost:9001/StockService/GetStockQuote";
//	public static final String DDQAddress = "http://localhost:9002/StockService/GetStockQuote";
//	public static final String GPQAddress = "http://localhost:9003/StockService/GetStockQuote";

	
//	static {
//		Endpoint.publish(AFQAddress, new AFQService());
//		Endpoint.publish(DDQAddress, new DDQService());
//		Endpoint.publish(GPQAddress, new GPQService());		
//		
//		// Create the services and bind them to the registry.
//		ServiceRegistry.bind(GIRL_POWER_SERVICE, new GPQService());
//		ServiceRegistry.bind(AULD_FELLAS_SERVICE, new AFQService());
//		ServiceRegistry.bind(DODGY_DRIVERS_SERVICE, new DDQService());
//		ServiceRegistry.bind(BROKER_SERVICE, new LocalBrokerService());
//	}
	
	/**
	 * This is the starting point for the application. Here, we must
	 * get a reference to the Broker Service and then invoke the
	 * getQuotations() method on that service.
	 * 
	 * Finally, you should print out all quotations returned
	 * by the service.
	 * 
	 * @param args
	 */
	
	public static final String ENDPOINT_URL = "http://localhost:9000/StockService/GetStockQuote";
	
	
	public UDDIClerk createUDDIClerk(UDDIClerk clerk) {
		// Step.1 Begin
    	// create a UDDIClerk object
		try {
			UDDIClient uddiClient = new UDDIClient("META-INF/uddi.xml");
			clerk = uddiClient.getClerk("default");
			if (clerk == null)
				throw new Exception("the clerk wasn't found, check the config file!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Step.1 Finish
		return clerk;
	}
	
	public boolean checkService(String name) throws Exception {
		UDDISecurityPortType security = null;
		UDDIInquiryPortType inquiry = null;
		
		try {
			UDDIClient client = new UDDIClient("META-INF/simple-browse-uddi.xml");

			Transport transport = client.getTransport("default");

			security = transport.getUDDISecurityService();
			inquiry = transport.getUDDIInquiryService();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String token = WebServices.WebServicesClientHelper.getAuthKey(security, "uddi", "uddi");
	    BusinessList findBusiness = WebServices.WebServicesClientHelper.partialBusinessNameSearch(inquiry, token,
	              name + UDDIConstants.WILDCARD);
	    
		return (findBusiness.getBusinessInfos() == null);
	}
	
	// Step.4 Begin
	// publish the service to jUDDI
	public void publish(UDDIClerk clerk,String name,String Address) {
		try {
			String myBusKey = WebServices.WebServicesHelper.createBusiness(name, clerk);

			BusinessService myService = WebServices.WebServicesHelper.createWSDLService(name, myBusKey, Address);
			BusinessService svc = clerk.register(myService);
			if (svc == null) {
				System.out.println("Save failed!");
				System.exit(1);
			}

			String myServKey = svc.getServiceKey();
			System.out.println("myService key:  " + myServKey);
			
			clerk.discardAuthToken();
			System.out.println(name + "Business Registered!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// Step.4 Finish
	
	
	
	public static void main(String[] args) throws Exception {
		Server server_afq = new Server();
		UDDIClerk clerk_afq = null;
		clerk_afq = server_afq.createUDDIClerk(clerk_afq);
		Endpoint.publish(AFQService.AFQAddress, new AFQService());
		if (server_afq.checkService("AFQ")) server_afq.publish(clerk_afq,"AFQService", AFQService.AFQAddress);
		
		Server server_ddq = new Server();
		UDDIClerk clerk_ddq = null;
		clerk_ddq = server_ddq.createUDDIClerk(clerk_ddq);
		Endpoint.publish(DDQService.DDQAddress, new DDQService());
		if (server_ddq.checkService("DDQ")) server_ddq.publish(clerk_ddq,"DDQService", DDQService.DDQAddress);
		
		Server server_gpq = new Server();
		UDDIClerk clerk_gpq = null;
		clerk_gpq = server_gpq.createUDDIClerk(clerk_gpq);
		Endpoint.publish(GPQService.GPQAddress, new GPQService());
		if (server_gpq.checkService("GPQ")) server_gpq.publish(clerk_gpq,"GPQService", GPQService.GPQAddress);
		
		Server server_lbs = new Server();
		UDDIClerk clerk_lbs = null;
		clerk_lbs = server_lbs.createUDDIClerk(clerk_lbs);
		Endpoint.publish(ENDPOINT_URL, new LocalBrokerService());
		if (server_lbs.checkService("Local")) server_lbs.publish(clerk_lbs,"LocalBrokerService", ENDPOINT_URL);
		
//		AFQService afq = new AFQService();
//		Endpoint.publish(AFQService.AFQAddress, afq);
//		if (afq.checkService())	afq.publish(afq.getClerk());
//		
//		DDQService ddq = new DDQService();
//		Endpoint.publish(DDQService.DDQAddress, ddq);
//		if (ddq.checkService())	ddq.publish(ddq.getClerk());
//		
//		GPQService gpq = new GPQService();
//		Endpoint.publish(GPQService.GPQAddress, gpq);
//		if (gpq.checkService())	gpq.publish(gpq.getClerk());
//		
//		
//		LocalBrokerService lbs = new LocalBrokerService();
//		Endpoint.publish(ENDPOINT_URL, lbs);
//		if (lbs.checkService())	lbs.publish(lbs.getClerk());
		
//		BrokerService brokerService = ServiceRegistry.lookup(BROKER_SERVICE, BrokerService.class);
//		Endpoint.publish(ENDPOINT_URL, lbs);
//		Endpoint.publish("http://localhost:9000/StockService/GetStockQuote", new AFQService());
//		Endpoint.publish("http://localhost:9000/StockService/GetStockQuote", new DDQService());
//		Endpoint.publish("http://localhost:9000/StockService/GetStockQuote", new GPQService());
//		//Create the broker and run the test data
//		for (ClientInfo info : clients) {
//			displayProfile(info);
//			
//			// Retrieve quotations from the broker and display them...
//			for(Quotation quotation : brokerService.getQuotations(info)) {
//				displayQuotation(quotation);
//			}
//			
//			// Print a couple of lines between each client
//			System.out.println("\n");
//		}
	}
	
//	/**
//	 * Display the client info nicely.
//	 * 
//	 * @param info
//	 */
//	public static void displayProfile(ClientInfo info) {
//		System.out.println("|=================================================================================================================|");
//		System.out.println("|                                     |                                     |                                     |");
//		System.out.println(
//				"| Name: " + String.format("%1$-29s", info.name) + 
//				" | Gender: " + String.format("%1$-27s", (info.gender==ClientInfo.MALE?"Male":"Female")) +
//				" | Age: " + String.format("%1$-30s", info.age)+" |");
//		System.out.println(
//				"| License Number: " + String.format("%1$-19s", info.licenseNumber) + 
//				" | No Claims: " + String.format("%1$-24s", info.noClaims+" years") +
//				" | Penalty Points: " + String.format("%1$-19s", info.points)+" |");
//		System.out.println("|                                     |                                     |                                     |");
//		System.out.println("|=================================================================================================================|");
//	}
//
//	/**
//	 * Display a quotation nicely - note that the assumption is that the quotation will follow
//	 * immediately after the profile (so the top of the quotation box is missing).
//	 * 
//	 * @param quotation
//	 */
//	public static void displayQuotation(Quotation quotation) {
//		System.out.println(
//				"| Company: " + String.format("%1$-26s", quotation.company) + 
//				" | Reference: " + String.format("%1$-24s", quotation.reference) +
//				" | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.price))+" |");
//		System.out.println("|=================================================================================================================|");
//	}
//	
//	/**
//	 * Test Data
//	 */
//	public static final ClientInfo[] clients = {
//		new ClientInfo("Niki Collier", ClientInfo.FEMALE, 43, 0, 5, "PQR254/1"),
//		new ClientInfo("Old Geeza", ClientInfo.MALE, 65, 0, 2, "ABC123/4"),
//		new ClientInfo("Hannah Montana", ClientInfo.FEMALE, 16, 10, 0, "HMA304/9"),
//		new ClientInfo("Rem Collier", ClientInfo.MALE, 44, 5, 3, "COL123/3"),
//		new ClientInfo("Jim Quinn", ClientInfo.MALE, 55, 4, 7, "QUN987/4"),
//		new ClientInfo("Donald Duck", ClientInfo.MALE, 35, 5, 2, "XYZ567/9")		
//	};
}
