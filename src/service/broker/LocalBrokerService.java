package service.broker;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

import client.Server;
import service.core.BrokerService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationService;
//import service.registry.ServiceRegistry;
import service.dodgydrivers.DDQService;

/**
 * Implementation of the broker service that uses the Service Registry.
 * 
 * @author Rem
 *
 */

@WebService(
		serviceName="BrokerService",
		targetNamespace="http://core.service/",
		portName="BrokerServicePort"
)
//@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)

public class LocalBrokerService implements BrokerService {
	
	public static final String AFQAddress = "http://localhost:9001/StockService/GetStockQuote";
	public static final String DDQAddress = "http://localhost:9002/StockService/GetStockQuote";
	public static final String GPQAddress = "http://localhost:9003/StockService/GetStockQuote";
	
	private static UDDIClerk clerk = null;
	
	public LocalBrokerService() {
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
		
		try {
			String myBusKey = WebServices.WebServicesHelper.createBusiness("LocalBrokerService", clerk);

			BusinessService myService = WebServices.WebServicesHelper.createWSDLService("LocalBrokerService", myBusKey, Server.ENDPOINT_URL);
			BusinessService svc = clerk.register(myService);
			if (svc == null) {
				System.out.println("Save failed!");
				System.exit(1);
			}

			String myServKey = svc.getServiceKey();

			clerk.discardAuthToken();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	// Step.4 Begin
//		// publish the service to jUDDI
//		public void publish() {
//			try {
//				String myBusKey = WebServices.WebServicesHelper.createBusiness("LocalBrokerService", clerk);
//
//				BusinessService myService = WebServices.WebServicesHelper.createWSDLService("LocalBrokerService", myBusKey, Server.ENDPOINT_URL);
//				BusinessService svc = clerk.register(myService);
//				if (svc == null) {
//					System.out.println("Save failed!");
//					System.exit(1);
//				}
//
//				String myServKey = svc.getServiceKey();
//
//				clerk.discardAuthToken();
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		// Step.4 Finish

	
	//	public List<Quotation> getQuotations(ClientInfo info) {
	public Quotation[] getQuotations(ClientInfo info) throws Exception {
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
//		String Addr[] = {AFQAddress+"?wsdl",DDQAddress+"?wsdl",GPQAddress+"?wsdl"};
		String Name[] = {"AFQ","DDQ","GPQ"};
		// Authenticate UDDI User
		String token = WebServices.WebServicesClientHelper.getAuthKey(security, "uddi", "uddi");
		QuotationService quotationService = null;
		QName qname;
		Service service;
		List<Quotation> quotations = new LinkedList<Quotation>();
		URL URL;
		
		for (String name : Name) {
			try {
				
				BusinessList findBusiness = WebServices.WebServicesClientHelper.partialBusinessNameSearch(inquiry, token,
						name + UDDIConstants.WILDCARD);

				// Get the 1 business we expect to find (or loop through many
				// matching businesses)
				BusinessInfo info1 = findBusiness.getBusinessInfos().getBusinessInfo().get(0);
				ServiceDetail serviceDetail = WebServices.WebServicesClientHelper.getServiceDetail(inquiry, token, info1);
				
				// Step.5 Begin
				// invoke the associated service
				// For each service, look for a binding template and contact the
				// service...
				System.out.println("Found: " + info1.getName());
				for (int k = 0; k < serviceDetail.getBusinessService().size(); k++) {
					BindingTemplate bindingTemplate = serviceDetail.getBusinessService().get(k).getBindingTemplates()
							.getBindingTemplate().get(0);

					System.out.println("Access: " + bindingTemplate.getBindingKey());
					URL = new URL(bindingTemplate.getAccessPoint().getValue());
					qname = new QName("http://core.service/", "BrokerService");
					service = Service.create(URL, qname);
					quotationService  =  service.getPort(
			        		new QName("http://core.service/", "BrokerServicePort"),
			        		QuotationService.class
			        		);
					quotations.add(quotationService.generateQuotation(info));
					//System.out.println(helloWorld.sayHi("It's Meee!!!"));
				}
				// Step.5 Finish
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		
		
		
//		QName qname;
//		Service service;
//		List<Quotation> quotations = new LinkedList<Quotation>();
//		URL URL;
//		
//		for(String Address: Addr) {
//			URL = new URL(Address);
//			qname = new QName("http://core.service/", "BrokerService");
//			service = Service.create(URL, qname);
//			QuotationService Service  =  service.getPort(
//	        		new QName("http://core.service/", "BrokerServicePort"),
//	        		QuotationService.class
//	        		);
//			quotations.add(Service.generateQuotation(info));
//		}
		
		
		
		
//		quotations.add(DDQService.generateQuotation(info));
		
//		URL URL2 = new URL("http://localhost:9000/StockService/GetStockQuote?wsdl");
//		
//		URL URL3 = new URL("http://localhost:9000/StockService/GetStockQuote?wsdl");
		
//		URL[] SERVICE_URLS = { URL1, URL2, URL3 };
//		URL[] SERVICE_URLS = { URL, URL, URL };

		

		
//		for (String name : ServiceRegistry.list()) {
		
//		for (int k = 0; k<SERVICE_URLS.length; k++) {
//			qname = new QName("http://core.service/", "BrokerService");
//			service = Service.create(SERVICE_URLS[k], qname);
			
//			if (name.startsWith("qs-")) {
//				QuotationService service = ServiceRegistry.lookup(name, QuotationService.class);
//				//quotations.add(service.generateQuotation(info));
//				quotations.add(service.generateQuotation(info));
//			}
//		}

//		return quotations;
		return quotations.toArray(new Quotation[quotations.size()]);
//		return null;
	}
}
