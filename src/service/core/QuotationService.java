package service.core;

import javax.jws.WebMethod;
import javax.jws.WebService;

import service.registry.Service;

/**
 * Interface to define the behaviour of a quotation service.
 * 
 * @author Rem
 *
 */
@WebService
public interface QuotationService extends Service {
	@WebMethod public Quotation generateQuotation(ClientInfo info);
}
