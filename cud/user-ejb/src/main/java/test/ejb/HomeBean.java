package test.ejb;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class HomeBean
 */
@Stateless(mappedName = "hbean")
@LocalBean
 public class HomeBean {

    /**
     * Default constructor. 
     */
    public HomeBean() {
        // TODO Auto-generated constructor stub
    }

    public String test() {

       
       return "bubnov";
    }
}
