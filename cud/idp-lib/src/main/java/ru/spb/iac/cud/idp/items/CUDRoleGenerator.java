package ru.spb.iac.cud.idp.items;

import java.security.Principal;
import java.util.List;

import org.picketlink.identity.federation.core.interfaces.RoleGenerator;

public interface CUDRoleGenerator extends RoleGenerator {

	public void setSystemCode(String codeSystem);

	public List<String> generateResources(Principal principal);
}
