package ru.spb.iac.cud.core.eis;

import java.util.List;
import javax.ejb.Remote;

import ru.spb.iac.cud.exceptions.GeneralFailure;

/**
 * @author bubnov
 */
@Remote
public interface AdminManagerRemote {

	public void access(String codeSystem, List<String> uidsUsers,
			String modeExec, List<String> codesRoles, Long idUserAuth,
			String IPAddress) throws GeneralFailure;

	public void access_groups(String codeSystem, List<String> uidsUsers,
			String modeExec, List<String> codesGroups, Long idUserAuth,
			String IPAddress) throws GeneralFailure;

	public void cert_change(String codeSystem, String newCert, Long idUserAuth,
			String IPAddress) throws GeneralFailure;

}
