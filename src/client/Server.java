package client;

import java.text.NumberFormat;

import javax.xml.ws.Endpoint;

import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;

import service.auldfellas.AFQService;
import service.broker.LocalBrokerService;
import service.core.BrokerService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.dodgydrivers.DDQService;
import service.girlpower.GPQService;
//import service.registry.ServiceRegistry;

public class Server {
//	public static final String BROKER_SERVICE = "bs-BrokerService";  
//	public static final String GIRL_POWER_SERVICE = "qs-GirlPowerService";  
//	public static final String AULD_FELLAS_SERVICE = "qs-AuldFellasService";  
//	public static final String DODGY_DRIVERS_SERVICE = "qs-DodgyDriversService";
	public static final String AFQAddress = "http://localhost:9001/StockService/GetStockQuote";
	public static final String DDQAddress = "http://localhost:9002/StockService/GetStockQuote";
	public static final String GPQAddress = "http://localhost:9003/StockService/GetStockQuote";
	
	static {
		AFQService afq = new AFQService();
		Endpoint.publish(AFQAddress, afq);
		//afq.publish();
		
		DDQService ddq = new DDQService();
		Endpoint.publish(DDQAddress, ddq);
		//ddq.publish();
		
		GPQService gpq = new GPQService();
		Endpoint.publish(GPQAddress, gpq);
		//gpq.publish();
		
//		Endpoint.publish(AFQAddress, new AFQService());
//		Endpoint.publish(DDQAddress, new DDQService());
//		Endpoint.publish(GPQAddress, new GPQService());		
		
		// Create the services and bind them to the registry.
//		ServiceRegistry.bind(GIRL_POWER_SERVICE, new GPQService());
//		ServiceRegistry.bind(AULD_FELLAS_SERVICE, new AFQService());
//		ServiceRegistry.bind(DODGY_DRIVERS_SERVICE, new DDQService());
//		ServiceRegistry.bind(BROKER_SERVICE, new LocalBrokerService());
	}
	
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
	
	public static void main(String[] args) {
		
		
		LocalBrokerService lbs = new LocalBrokerService();
//		BrokerService brokerService = ServiceRegistry.lookup(BROKER_SERVICE, BrokerService.class);
		Endpoint.publish(ENDPOINT_URL, lbs);
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
		
		//lbs.publish();
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
