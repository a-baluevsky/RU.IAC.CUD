package ru.spb.iac.cud.uarm.ejb.context.reg;

import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.uarm.ejb.entity.JournAppAdminUserSysBssT;

/**
 * Session Bean implementation class HomeBean
 */
@Stateless(mappedName = "userAdminSysEJB")
@LocalBean
 public class UserAdminSysEJB {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminSysEJB.class);
	
	@PersistenceContext(unitName = "CUDUserConsolePU")
    private EntityManager entityManager;
	
    public UserAdminSysEJB() {
        // TODO Auto-generated constructor stub
    }

    public void save(JournAppAdminUserSysBssT user) {

       LOGGER.debug("userAdminSysEJB:save:01");
       LOGGER.debug("userAdminSysEJB:save:02:"+user.getAcIsBssTLong());
       try{
    	 
    	   
    	   user.setCreated(new Date());
    	   entityManager.persist(user);
    	   
    	   
       }catch(Exception e){
    	   LOGGER.error("userAdminSysEJB:save:error:"+e);
       }
       
       
     }
}
