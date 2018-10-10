package service.dodgydrivers;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.uddi.api_v3.BusinessService;

import service.broker.LocalBrokerService;
import service.core.AbstractQuotationService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationService;

/**
 * Implementation of Quotation Service for Dodgy Drivers Insurance Company
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

public class DDQService extends AbstractQuotationService implements QuotationService {
	// All references are to be prefixed with an DD (e.g. DD001000)
	public static final String PREFIX = "DD";
	public static final String COMPANY = "Dodgy Drivers Corp.";

	private static UDDIClerk clerk = null;
	
	public DDQService() {
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
	}
	
	// Step.4 Begin
	// publish the service to jUDDI
	public void publish() {
		try {
			String myBusKey = WebServices.WebServicesHelper.createBusiness("DDQService", clerk);

			BusinessService myService = WebServices.WebServicesHelper.createWSDLService("DDQService", myBusKey, LocalBrokerService.DDQAddress);
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
	// Step.4 Finish
	
	/**
	 * Quote generation:
	 * 5% discount per penalty point (3 points required for qualification)
	 * 50% penalty for <= 3 penalty points
	 * 10% discount per year no claims
	 */
	@Override
	public Quotation generateQuotation(ClientInfo info) {
		// Create an initial quotation between 800 and 1000
		double price = generatePrice(800, 200);
		
		// 5% discount per penalty point (3 points required for qualification)
		int discount = (info.points > 3) ? 5*info.points:-50;
		
		// Add a no claims discount
		discount += getNoClaimsDiscount(info);
		
		// Generate the quotation and send it back
		return new Quotation(COMPANY, generateReference(PREFIX), (price * (100-discount)) / 100);
	}

	private int getNoClaimsDiscount(ClientInfo info) {
		return 10*info.noClaims;
	}

}
