package client;

//import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Scanner;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

//import service.auldfellas.AFQService;
//import service.broker.LocalBrokerService;
import service.core.BrokerService;
import service.core.ClientInfo;
import service.core.Quotation;
//import service.dodgydrivers.DDQService;
//import service.girlpower.GPQService;
//import service.registry.ServiceRegistry;

public class Client {
//	public static final String BROKER_SERVICE = "bs-BrokerService";  
//	public static final String GIRL_POWER_SERVICE = "qs-GirlPowerService";  
//	public static final String AULD_FELLAS_SERVICE = "qs-AuldFellasService";  
//	public static final String DODGY_DRIVERS_SERVICE = "qs-DodgyDriversService";
	
//	static {
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
	 * @throws Exception 
	 */
	
	
	
	public static void main(String[] args) throws Exception {
		
		// Get user's option.
		Scanner sc = new Scanner(System.in); 
        System.out.println("Do you want to get price from Auldfellas? (0-No, 1-Yes)"); 
        int input_a = sc.nextInt(); 
        while (input_a == 0 || input_a == 1) {
        	System.out.println("Invalid input, ONLY 0 AND 1 ARE ALLOWED. Please try again.");
        	System.out.println("Do you want to get price from Auldfellas? (0-No, 1-Yes)"); 
            input_a = sc.nextInt(); 
        }
        
        System.out.println("Do you want to get price from Dodgydrivers? (0-No, 1-Yes)"); 
        int input_d = sc.nextInt(); 
        while (input_d == 0 || input_d == 1) {
        	System.out.println("Invalid input, ONLY 0 AND 1 ARE ALLOWED. Please try again.");
        	System.out.println("Do you want to get price from Dodgydrivers? (0-No, 1-Yes)"); 
            input_d = sc.nextInt(); 
        }
        
        System.out.println("Do you want to get price from Girlpower? (0-No, 1-Yes)"); 
        int input_g = sc.nextInt(); 
        while (input_d == 0 || input_d == 1) {
        	System.out.println("Invalid input, ONLY 0 AND 1 ARE ALLOWED. Please try again.");
        	System.out.println("Do you want to get price from Girlpower? (0-No, 1-Yes)"); 
            input_d = sc.nextInt(); 
        }
        sc.close();
        System.out.println("Thanks for your cooperation!");
        
        int input = 100*input_a + 10*input_d + input_g; 
		
		// Step.1 Begin
		// connect to the jUDDI server
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
		// Step.1 Finish
		
		// Authenticate UDDI User
		String token = WebServices.WebServicesClientHelper.getAuthKey(security, "uddi", "uddi");
		BrokerService brokerService = null;
		try {
			BusinessList findBusiness = WebServices.WebServicesClientHelper.partialBusinessNameSearch(inquiry, token,
					"Local" + UDDIConstants.WILDCARD);

			// Get the 1 business we expect to find (or loop through many
			// matching businesses)
			BusinessInfo info = findBusiness.getBusinessInfos().getBusinessInfo().get(0);
			ServiceDetail serviceDetail = WebServices.WebServicesClientHelper.getServiceDetail(inquiry, token, info);
			
			// Step.5 Begin
			// invoke the associated service
			// For each service, look for a binding template and contact the
			// service...
			System.out.println("Found: " + info.getName());
//			for (int k = 0; k < serviceDetail.getBusinessService().size(); k++) {
				BindingTemplate bindingTemplate = serviceDetail.getBusinessService().get(0).getBindingTemplates()
						.getBindingTemplate().get(0);

				System.out.println("Access: " + bindingTemplate.getBindingKey());
				URL wsdlUrl = new URL(bindingTemplate.getAccessPoint().getValue());
				QName qname = new QName("http://core.service/", "BrokerService");
				Service service = Service.create(wsdlUrl, qname);
				brokerService  =  service.getPort(
		        		new QName("http://core.service/", "BrokerServicePort"),
		        		BrokerService.class
		        		);
				//System.out.println(helloWorld.sayHi("It's Meee!!!"));
//			}
			// Step.5 Finish
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		BrokerService brokerService = ServiceRegistry.lookup(BROKER_SERVICE, BrokerService.class);
		
//		URL	wsdlUrl = new URL("http://localhost:9000/StockService/GetStockQuote?wsdl");
//		
//		QName qname = new QName("http://core.service/", "BrokerService");
//        Service service = Service.create(wsdlUrl, qname);
//        BrokerService brokerService  =  service.getPort(
//        		new QName("http://core.service/", "BrokerServicePort"),
//        		BrokerService.class
//        		);
 
		// Create the broker and run the test data
		for (ClientInfo info : clients) {
			displayProfile(info);
			
			// Retrieve quotations from the broker and display them...
			for(Quotation quotation : brokerService.getQuotations(info, input)) {
				displayQuotation(quotation);
			}
			
			// Print a couple of lines between each client
			System.out.println("\n");
		}
	}
	
	/**
	 * Display the client info nicely.
	 * 
	 * @param info
	 */
	public static void displayProfile(ClientInfo info) {
		System.out.println("|=================================================================================================================|");
		System.out.println("|                                     |                                     |                                     |");
		System.out.println(
				"| Name: " + String.format("%1$-29s", info.name) + 
				" | Gender: " + String.format("%1$-27s", (info.gender==ClientInfo.MALE?"Male":"Female")) +
				" | Age: " + String.format("%1$-30s", info.age)+" |");
		System.out.println(
				"| License Number: " + String.format("%1$-19s", info.licenseNumber) + 
				" | No Claims: " + String.format("%1$-24s", info.noClaims+" years") +
				" | Penalty Points: " + String.format("%1$-19s", info.points)+" |");
		System.out.println("|                                     |                                     |                                     |");
		System.out.println("|=================================================================================================================|");
	}

	/**
	 * Display a quotation nicely - note that the assumption is that the quotation will follow
	 * immediately after the profile (so the top of the quotation box is missing).
	 * 
	 * @param quotation
	 */
	public static void displayQuotation(Quotation quotation) {
		System.out.println(
				"| Company: " + String.format("%1$-26s", quotation.company) + 
				" | Reference: " + String.format("%1$-24s", quotation.reference) +
				" | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.price))+" |");
		System.out.println("|=================================================================================================================|");
	}
	
	/**
	 * Test Data
	 */
	public static final ClientInfo[] clients = {
		new ClientInfo("Niki Collier", ClientInfo.FEMALE, 43, 0, 5, "PQR254/1"),
		new ClientInfo("Old Geeza", ClientInfo.MALE, 65, 0, 2, "ABC123/4"),
		new ClientInfo("Hannah Montana", ClientInfo.FEMALE, 16, 10, 0, "HMA304/9"),
		new ClientInfo("Rem Collier", ClientInfo.MALE, 44, 5, 3, "COL123/3"),
		new ClientInfo("Jim Quinn", ClientInfo.MALE, 55, 4, 7, "QUN987/4"),
		new ClientInfo("Donald Duck", ClientInfo.MALE, 35, 5, 2, "XYZ567/9")		
	};
}
