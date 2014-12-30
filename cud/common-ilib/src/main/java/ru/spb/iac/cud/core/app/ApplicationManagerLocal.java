package ru.spb.iac.cud.core.app;

import java.util.List;
import javax.ejb.Local;

import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.app.AppAttribute;
import ru.spb.iac.cud.items.app.AppAccept;

/**
 * @author bubnov
 */
@Local
public interface ApplicationManagerLocal {

	public AppAccept system_registration(List<AppAttribute> attributes,
			String principal, Long idUser, String IPAddress)
			throws GeneralFailure;

	public AppAccept user_registration(List<AppAttribute> attributes,
			String principal, Long idUser, String IPAddress)
			throws GeneralFailure;

	public AppAccept access_roles(String modeExec, String loginUser,
			String codeSystem, List<String> codesRoles, String principal,
			Long idUser, String IPAddress) throws GeneralFailure;

	public AppAccept access_groups(String modeExec, String loginUser,
			String codeSystem, List<String> codesGroups, String principal,
			Long idUser, String IPAddress) throws GeneralFailure;

	public AppAccept block(String modeExec, String loginUser,
			String blockReason, String principal, Long idUser, String IPAddress)
			throws GeneralFailure;

	public AppAccept system_modification(String codeSystem,
			List<AppAttribute> attributes, String principal, Long idUser,
			String IPAddress) throws GeneralFailure;

	public AppAccept user_modification(String loginUser,
			List<AppAttribute> attributes, String principal, Long idUser,
			String IPAddress) throws GeneralFailure;

	public AppAccept user_identity_modification(String loginUser, String login,
			String password, Long idUser, String IPAddress)
			throws GeneralFailure;

	public AppAccept user_cert_modification(String modeExec, String loginUser,
			String certBase64, Long idUser, String IPAddress)
			throws GeneralFailure;

	public AppAccept user_dep_modification(String loginUser,
			List<AppAttribute> attributes, String principal, Long idUser,
			String IPAddress) throws GeneralFailure;

	public Long principal_exist(String principal) throws GeneralFailure;
}
