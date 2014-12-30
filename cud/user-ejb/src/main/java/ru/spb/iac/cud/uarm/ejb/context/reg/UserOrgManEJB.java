package ru.spb.iac.cud.uarm.ejb.context.reg;

import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.uarm.ejb.entity.JournAppOrgManagerBssT;

/**
 * Session Bean implementation class HomeBean
 */
@Stateless(mappedName = "userOrgManEJB")
@LocalBean
 public class UserOrgManEJB {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserOrgManEJB.class);
	
	@PersistenceContext(unitName = "CUDUserConsolePU")
    private EntityManager entityManager;
	
    public UserOrgManEJB() {
        // TODO Auto-generated constructor stub
    }

    public void save(JournAppOrgManagerBssT user) {

       LOGGER.debug("userOrgManEJB:save:01");
      
       try{
    	
    	   
    	   user.setCreated(new Date());
    	   entityManager.persist(user);
    	   
    	   
       }catch(Exception e){
    	   LOGGER.error("userOrgManEJB:save:error:"+e);
       }
       
       
     }
}
