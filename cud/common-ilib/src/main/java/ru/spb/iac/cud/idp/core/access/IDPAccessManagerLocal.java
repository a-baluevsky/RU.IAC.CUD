package ru.spb.iac.cud.idp.core.access;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

/**
 * @author bubnov
 */
@Local
public interface IDPAccessManagerLocal {

	public Map<String, String> attributes(String login) throws Exception;

	public List<String> rolesCodes(String login, String domain)
			throws Exception;

	public List<String> resources(String login, String domain) throws Exception;

	public void saveTokenID(String tokenID, String userID) throws Exception;
}
