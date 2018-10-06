package service.broker;

import java.util.LinkedList;
import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import service.core.BrokerService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationService;
import service.registry.ServiceRegistry;

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
//	public List<Quotation> getQuotations(ClientInfo info) {
	public Quotation[] getQuotations(ClientInfo info) {
		List<Quotation> quotations = new LinkedList<Quotation>();
		
		for (String name : ServiceRegistry.list()) {
			if (name.startsWith("qs-")) {
				QuotationService service = ServiceRegistry.lookup(name, QuotationService.class);
				//quotations.add(service.generateQuotation(info));
				quotations.add(service.generateQuotation(info));
			}
		}

//		return quotations;
		return quotations.toArray(new Quotation[quotations.size()]);
//		return null;
	}
}
