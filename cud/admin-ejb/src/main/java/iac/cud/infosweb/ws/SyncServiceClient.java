package iac.cud.infosweb.ws;

import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.Function;
import ru.spb.iac.cud.items.Role;
import ru.spb.iac.cud.services.sync.SyncServices;

@Name("ssClient")
 public class SyncServiceClient {

	@Logger
	private Log log;

	static String endpointURI = "http://cudvm/CudServices/SyncService?wsdl";

	static String idIS = "urn:eis:cud";

	private SyncServices syncServices = null;

	

	public void invoke() {
		
		

	}

	public void sync_roles() throws Exception {
		try {
			List<Role> roles = new ArrayList<Role>();

			Role role = new Role();
			role.setCode("urn:role:operator");
			role.setName("Роль оператора");
			role.setDescription("Ведение списков");
			roles.add(role);

			role = new Role();
			role.setCode("urn:role:manager");
			role.setName("Роль менеджера");
			role.setDescription("Управление системой");
			roles.add(role);

			role = new Role();
			role.setCode("urn:role:user");
			role.setName("Роль пользователя");
			role.setDescription("Просмотр списков");
			roles.add(role);

			getPort(endpointURI).sync_roles(idIS, roles);

		} catch (GeneralFailure e1) {
			log.error("sync_roles:error1:" + e1);
			throw e1;
		}
	}

	public void sync_functions() throws Exception {

		List<Function> functions = new ArrayList<Function>();

		// пользователи
		Function func = new Function();
		
		   func.setCode(ResourcesMap.USER.getCode()+":"+ActionsMap.CREATE.
		   getCode()); 
		   func.setName("Создание пользователя");
		   func.setDescription("Создание пользователя"); 
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.USER.getCode()+
		   ":"+ActionsMap.UPDATE.getCode());
		   func.setName("Редактирование пользователя");
		   func.setDescription("Редактирование пользователя");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.USER.getCode()+
		   ":"+ActionsMap.UPDATE_ROLE.getCode());
		   func.setName("Редактирование ролей пользователя");
		   func.setDescription("Редактирование ролей пользователя");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.USER.getCode()+
		   ":"+ActionsMap.UPDATE_GROUP.getCode());
		   func.setName("Редактирование групп пользователя");
		   func.setDescription("Редактирование групп пользователя");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.USER.getCode()+
		   ":"+ActionsMap.DELETE.getCode());
		   func.setName("Удаление пользователя");
		   func.setDescription("Удаление пользователя"); 
		   functions.add(func);
		   
		   //роли 
		   func = new Function();
		   func.setCode(ResourcesMap.ROLE.getCode
		   ()+":"+ActionsMap.CREATE.getCode()); 
		   func.setName("Создание роли");
		   func.setDescription("Создание роли"); 
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.ROLE.getCode()+
		   ":"+ActionsMap.UPDATE.getCode());
		   func.setName("Редактирование роли");
		   func.setDescription("Редактирование роли"); 
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.ROLE.getCode()+
		   ":"+ActionsMap.DELETE.getCode()); 
		   func.setName("Удаление роли");
		   func.setDescription("Удаление роли");
		   functions.add(func);
		   
		   //группы  пользователей 
		   func = new Function();
		   func.setCode(ResourcesMap
		   .UGROUP.getCode()+":"+ActionsMap.CREATE.getCode());
		   func.setName("Создание группы пользователей");
		   func.setDescription("Создание группы пользователей");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.UGROUP.getCode(
		   )+":"+ActionsMap.UPDATE.getCode());
		   func.setName("Редактирование группы пользователей");
		   func.setDescription("Редактирование группы пользователей");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.UGROUP.getCode(
		   )+":"+ActionsMap.UPDATE_ROLE.getCode());
		   func.setName("Редактирование ролей группы пользователей");
		   func.setDescription("Редактирование ролей группы пользователей");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.UGROUP.getCode(
		   )+":"+ActionsMap.UPDATE_USER.getCode());
		   func.setName("Редактирование пользователей группы пользователей");
		   func
		   .setDescription("Редактирование пользователей группы пользователей!"
		   ); 
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.UGROUP.getCode(
		   )+":"+ActionsMap.DELETE.getCode());
		   func.setName("Удаление группы пользователей");
		   func.setDescription("Удаление группы пользователей");
		   functions.add(func);
		   
		   //ис 
		   func = new Function();
		   func.setCode(ResourcesMap.IS.getCode
		   ()+":"+ActionsMap.CREATE.getCode()); 
		   func.setName("Создание ИС");
		   func.setDescription("Создание ИС"); 
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.IS.getCode()+":"
		   +ActionsMap.UPDATE.getCode()); 
		   func.setName("Редактирование ИС");
		   func.setDescription("Редактирование ИС"); 
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.IS.getCode()+":"
		   +ActionsMap.DELETE.getCode()); 
		   func.setName("Удаление ИС");
		   func.setDescription("Удаление ИС");
		   functions.add(func);
		   
		   //ресурсы 
		   func = new Function();
		   func.setCode(ResourcesMap.RES.getCode
		   ()+":"+ActionsMap.CREATE.getCode());
		   func.setName("Создание ресурса");
		   func.setDescription("Создание ресурса"); 
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.RES.getCode()+":"
		   +ActionsMap.UPDATE.getCode());
		   func.setName("Редактирование ресурса");
		   func.setDescription("Редактирование ресурса");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.RES.getCode()+":"
		   +ActionsMap.DELETE.getCode()); 
		   func.setName("Удаление ресурса");
		   func.setDescription("Удаление ресурса");
		   functions.add(func);
		   
		   //параметры конфигурации 
		   func = new Function();
		   func.setCode(ResourcesMap
		   .CONF_PARAM.getCode()+":"+ActionsMap.UPDATE.getCode());
		   func.setName("Редактирование параметра конфигурации");
		   func.setDescription("Редактирование параметра конфигурации");
		   functions.add(func);
		   
		   //журнал действий пользователя 
		   func = new Function();
		   func.setCode
		   (ResourcesMap.AUDIT_USER.getCode()+":"+ActionsMap.START.getCode());
		   func.setName("Запуск архивирования аудита действий пользователя");
		   func
		   .setDescription("Запуск архивирования аудита действий пользователя");
		   functions.add(func);
		   
		   //журнал вызовов сервисов 
		   func = new Function();
		   func.setCode(ResourcesMap
		   .AUDIT_SYS.getCode()+":"+ActionsMap.START.getCode());
		   func.setName("Запуск архивирования вызовов сервисов");
		   func.setDescription("Запуск архивирования вызовов сервисов");
		   functions.add(func);
		   
		   //процесс архивирование аудита действий пользователя 
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_ARCH_AUDIT_USER.getCode
		   ()+":"+ActionsMap.START.getCode());
		   func.setName("Запуск процесса архивирования аудита действий пользователя"
		   ); 
		   func.setDescription(
		   "Запуск процесса архивирования аудита действий пользователя");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_ARCH_AUDIT_USER
		   .getCode()+":"+ActionsMap.PAUSE.getCode());
		   func.setName("Пауза процесса архивирования аудита действий пользователя"
		   ); 
		   func.setDescription(
		   "Пауза процесса архивирования аудита действий пользователя");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_ARCH_AUDIT_USER
		   .getCode()+":"+ActionsMap.STOP.getCode()); 
		   func.setName(
		   "Останов процесса архивирования аудита действий пользователя");
		   func.setDescription
		   ("Останов процесса архивирования аудита действий пользователя");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_ARCH_AUDIT_USER
		   .getCode()+":"+ActionsMap.SET_PARAM.getCode());
		   func.setName(
		   "Параметры процесса архивирования аудита действий пользователя");
		   func.setDescription(
		   "Параметры процесса архивирования аудита действий пользователя");
		   functions.add(func);
		   
		   //процесс архивирование аудита вызова сервисов 
		   func = new Function();
		   func
		   .setCode(ResourcesMap.PROC_ARCH_AUDIT_SYS.getCode()+":"+ActionsMap
		   .START.getCode());
		   func.setName("Запуск процесса архивирования аудита вызова сервисов");
		   func.setDescription("Запуск процесса архивирования вызова сервисов");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_ARCH_AUDIT_SYS
		   .getCode()+":"+ActionsMap.PAUSE.getCode());
		   func.setName("Пауза процесса архивирования аудита вызова сервисов");
		   func.setDescription("Пауза процесса архивирования вызова сервисов");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_ARCH_AUDIT_SYS
		   .getCode()+":"+ActionsMap.STOP.getCode());
		   func.setName("Останов процесса архивирования аудита вызова сервисов"
		   ); 
		   func.setDescription(
		   "Останов процесса архивирования аудита вызова сервисов");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_ARCH_AUDIT_SYS
		   .getCode()+":"+ActionsMap.SET_PARAM.getCode());
		   func.setName("Параметры процесса архивирования аудита вызова сервисов"
		   ); 
		   func.setDescription(
		   "Параметры процесса архивирования аудита вызова сервисов");
		   functions.add(func);
		   
		   //процесс архивирование токенов 
		   func = new Function();
		   func.setCode
		   (ResourcesMap.PROC_ARCH_TOKEN.getCode()+":"+ActionsMap
		   .START.getCode());
		   func.setName("Запуск процесса архивирования токенов");
		   func.setDescription("Запуск процесса архивирования токенов");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_ARCH_TOKEN
		   .getCode()+":"+ActionsMap.PAUSE.getCode());
		   func.setName("Пауза процесса архивирования токенов");
		   func.setDescription("Пауза процесса архивирования токенов");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_ARCH_TOKEN
		   .getCode()+":"+ActionsMap.STOP.getCode());
		   func.setName("Останов процесса архивирования токенов");
		   func.setDescription("Останов процесса архивирования токенов");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_ARCH_TOKEN
		   .getCode()+":"+ActionsMap.SET_PARAM.getCode());
		   func.setName("Параметры процесса архивирования токенов");
		   func.setDescription("Параметры процесса архивирования токенов");
		   functions.add(func);
		 
		
		   //ППС не привязанных 
		   func = new Function();
		   func.setCode(ResourcesMap
		   .PROC_BIND_UNBIND.getCode()+":"+ActionsMap.START.getCode());
		   func.setName("Запуск ППС не привязанных УЗП К ОГК ИОГВ");
		   func.setDescription("Запуск ППС не привязанных  УЗП К ОГК ИОГВ");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_BIND_UNBIND
		   .getCode()+":"+ActionsMap.PAUSE.getCode());
		   func.setName("Пауза ППС не привязанных  УЗП К ОГК ИОГВ");
		   func.setDescription("Пауза ППС не привязанных  УЗП К ОГК ИОГВ");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_BIND_UNBIND
		   .getCode()+":"+ActionsMap.STOP.getCode());
		   func.setName("Останов ППС не привязанных  УЗП К ОГК ИОГВ");
		   func.setDescription("Останов ППС не привязанных  УЗП К ОГК ИОГВ");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_BIND_UNBIND
		   .getCode()+":"+ActionsMap.SET_PARAM.getCode());
		   func.setName("Параметры ППС не привязанных  УЗП К ОГК ИОГВ");
		   func.setDescription("Параметры ППС не привязанных  УЗП К ОГК ИОГВ");
		   functions.add(func);
		   
		   //ППС не активных 
		   func = new Function();
		   func.setCode(ResourcesMap
		   .PROC_BIND_NOACT.getCode()+":"+ActionsMap.START.getCode());
		   func.setName("Запуск ППС не активных УЗП К ОГК ИОГВ");
		   func.setDescription("Запуск ППС не активных  УЗП К ОГК ИОГВ");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_BIND_NOACT
		   .getCode()+":"+ActionsMap.PAUSE.getCode());
		   func.setName("Пауза ППС не активных  УЗП К ОГК ИОГВ");
		   func.setDescription("Пауза ППС не активных  УЗП К ОГК ИОГВ");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_BIND_NOACT
		   .getCode()+":"+ActionsMap.STOP.getCode());
		   func.setName("Останов ППС не активных  УЗП К ОГК ИОГВ");
		   func.setDescription("Останов ППС не активных  УЗП К ОГК ИОГВ");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.PROC_BIND_NOACT
		   .getCode()+":"+ActionsMap.SET_PARAM.getCode());
		   func.setName("Параметры ППС не активных  УЗП К ОГК ИОГВ");
		   func.setDescription("Параметры ППС не активных  УЗП К ОГК ИОГВ");
		   functions.add(func);
		   
		   // привязка УЗП к ОГК ИОГВ 
		   func = new Function();
		   func.setCode(ResourcesMap
		   .BINDING_IOGV.getCode()+":"+ActionsMap.CREATE.getCode());
		   func.setName("Создание привязки УЗП к ОГК ИОГВ");
		   func.setDescription("Создание привязки УЗП к ОГК ИОГВ");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.BINDING_IOGV.getCode
		   ()+":"+ActionsMap.UPDATE.getCode());
		   func.setName("Изменение привязки УЗП к ОГК ИОГВ");
		   func.setDescription("Изменение привязки УЗП к ОГК ИОГВ");
		   functions.add(func);
		   
		   func = new Function();
		   func.setCode(ResourcesMap.BINDING_IOGV.getCode
		   ()+":"+ActionsMap.UPDATE.getCode());
		   func.setName("Удаление привязки УЗП к ОГК ИОГВ");
		   func.setDescription("Удаление привязки УЗП к ОГК ИОГВ");
		   functions.add(func);
		 

		func = new Function();
		func.setCode(ResourcesMap.ROLE.getCode() + ":"
				+ ActionsMap.UPDATE_USER.getCode());
		func.setName("Редактирование пользователей роли");
		func.setDescription("Редактирование пользователей роли");
		functions.add(func);

		func = new Function();
		func.setCode(ResourcesMap.ROLE.getCode() + ":"
				+ ActionsMap.UPDATE_GROUP.getCode());
		func.setName("Редактирование групп роли");
		func.setDescription("Редактирование групп роли");
		functions.add(func);

		try {

			getPort(endpointURI).sync_functions(idIS, functions);

		} catch (GeneralFailure e1) {
			log.error("sync_functions:error1:" + e1.getMessage());
			throw e1;
		}
	}

	private SyncServices getPort(String endpointURI)
			throws MalformedURLException {

		if (this.syncServices == null) {
			QName serviceName = new QName("http://sync.services.cud.iac/",
					"SyncServicesImplService");
			URL wsdlURL = new URL(endpointURI);

			Service service = Service.create(wsdlURL, serviceName);

			QName portName = new QName("http://sync.services.cud.iac/",
					"SyncServicesImplPort");

			this.syncServices = service.getPort(portName, SyncServices.class);
		}
		return this.syncServices;
	}
}
