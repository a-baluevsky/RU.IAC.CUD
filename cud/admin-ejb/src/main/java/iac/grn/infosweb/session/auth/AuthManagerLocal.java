package iac.grn.infosweb.session.auth;
import java.util.List;
import java.util.Map;

//@Local
public interface AuthManagerLocal {
	public Map<String, List<String>[]> authComplete(Long appCode,  String login, String password) throws Exception;
	public Long authenticate(String login, String password)throws Exception;
	public List<String>[] access(Long appCode,String pageCode, Long idUser) throws Exception;
}
