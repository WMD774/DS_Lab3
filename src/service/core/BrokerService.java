package service.core;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import service.registry.Service;

/**
 * Interface for defining the behaviours of the broker service
 * @author Rem
 *
 */
@WebService
public interface BrokerService extends Service {
//	@WebMethod	public List<Quotation> getQuotations(ClientInfo info);
	@WebMethod	public Quotation[] getQuotations(ClientInfo info) throws Exception;
}
