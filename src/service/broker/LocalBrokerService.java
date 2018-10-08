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
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)

public class LocalBrokerService implements BrokerService {
	
	public static final String AFQAddress = "http://localhost:9001/StockService/GetStockQuote";
	public static final String DDQAddress = "http://localhost:9002/StockService/GetStockQuote";
	public static final String GPQAddress = "http://localhost:9003/StockService/GetStockQuote";
	
	//	public List<Quotation> getQuotations(ClientInfo info) {
	public Quotation[] getQuotations(ClientInfo info) throws Exception {
		String Addr[] = {AFQAddress+"?wsdl",DDQAddress+"?wsdl",GPQAddress+"?wsdl"};
		QName qname;
		Service service;
		List<Quotation> quotations = new LinkedList<Quotation>();
		URL URL;
		
		for(String Address: Addr) {
			URL = new URL(Address);
			qname = new QName("http://core.service/", "BrokerService");
			service = Service.create(URL, qname);
			QuotationService Service  =  service.getPort(
	        		new QName("http://core.service/", "BrokerServicePort"),
	        		QuotationService.class
	        		);
			quotations.add(Service.generateQuotation(info));
		}
		
		
		
		
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
