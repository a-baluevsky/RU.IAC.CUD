package ru.spb.iac.cud.core;

import java.util.List;
import javax.ejb.Local;

import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.exceptions.InvalidCredentials;
import ru.spb.iac.cud.exceptions.RevokedCertificate;
import ru.spb.iac.cud.items.AuditFunction;
import ru.spb.iac.cud.items.AuthMode;

/**
 * @author bubnov
 */
@Local
public interface AccessManagerLocal {
	public String authenticate_login(String login, String password,
			AuthMode authMode, String IPAddress, String codeSys)
			throws GeneralFailure, InvalidCredentials;

	public Long authenticate_login_obo(String login, AuthMode authMode,
			String IPAddress, String codeSys) throws GeneralFailure;

	public String authenticate_uid_obo(String uid, AuthMode authMode,
			String IPAddress, String codeSys) throws GeneralFailure;

	public String authenticate_cert_sn(String sn, AuthMode authMode,
			String IPAddress, String codeSys) throws GeneralFailure,
			InvalidCredentials, RevokedCertificate;

	public void audit_pro(String idIS, String login,
			List<AuditFunction> userFunctions, Long idUserAuth, String IPAddress)
			throws GeneralFailure;

	public void is_exist(String idIS) throws GeneralFailure;

	public void change_password(String login, String password,
			String new_password, String IPAddress) throws GeneralFailure,
			InvalidCredentials;

	public void sys_audit_public(Long idServ, String inp_param, String result,
			String ip_adr, Long idUser, String loginUser, String codeSys);

}
