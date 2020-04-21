

package es.ucm.fdi.iw.control;

import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.ucm.fdi.iw.model.Lesson;
import es.ucm.fdi.iw.model.User;

@Controller
public class RootController {

	@Autowired
	private EntityManager entityManager;
	
    private static Logger log = LogManager.getLogger(
        RootController.class);

    private final Random random = new Random();
    
    @GetMapping("/")            
    public String index(HttpSession session,  Model model) {
	    return "index";
    }
    
    @GetMapping("inicio")
    public String inicio(HttpSession session, Model model) {
    	return "inicio";
    }
    
  
    
    @GetMapping("/horarios/")
	public String getHorarios(HttpSession session, Model model) { 
		return "horarios";
	} 
    
    @GetMapping("/usuarios/")
	public String getUsers(HttpSession session, Model model) {
		List<User> u = entityManager.createQuery("SELECT u FROM User u").getResultList();
		model.addAttribute("users", u);
		return "usuarios";
	} 
}
