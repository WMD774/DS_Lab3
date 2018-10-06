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

public interface QuotationService extends Service {
	public Quotation generateQuotation(ClientInfo info);
}
