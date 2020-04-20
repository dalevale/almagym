package es.ucm.fdi.iw.control;


import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
 
import es.ucm.fdi.iw.model.Lesson;

@Controller
@RequestMapping("/clases")
public class LessonController {

	@Autowired
	private EntityManager entityManager;
	
    private static Logger log = LogManager.getLogger(
    		LessonController.class);

//    
//    @PostMapping("remove/{id}")
//    @Transactional 
//	public String removeClass(@PathVariable long id, HttpSession session,Model model) {
//    	int f =2;
//    	//long testf =  id;
//    	
//    	//Lesson lesson = entityManager.find(Lesson.class, id);
//    	//entityManager.remove(lesson);
////    	Room target = entityManager.find(Room.class, id);
//////		model.addAttribute("room", target);
////		entityManager.remove(target);
//    	return "exito";
//	}
//    
//    @PostMapping("edit/{id}")
//    @Transactional 
//	public String editClass(@PathVariable long id, HttpSession session,Model model) {
//    	int f =2;
//    	//long testf =  id;
//    	
//    	//Lesson lesson = entityManager.find(Lesson.class, id);
//    	//entityManager.remove(lesson);
//    	
//    	return "exito";
//	}
//    
//    @PostMapping("add/{id}")
//    @Transactional 
//	public String addClass(@PathVariable long id, HttpSession session,Model model) {
//    	int f =2;
//    	//long testf =  id;
//    	
//    	//Lesson lesson = entityManager.find(Lesson.class, id);
//    	//entityManager.remove(lesson);
//    	
//    	return "exito";
//	}
	
//    @RequestMapping(value = "generateData", method = RequestMethod.POST)
//	public String generateData(@RequestBody Lesson r, HttpServletRequest req, HttpServletResponse resp) {
//    	int f =2;
//    	 
//    	//long testf =  id;
//    	
//    	//Lesson lesson = entityManager.find(Lesson.class, id);
//    	//entityManager.remove(lesson);
//    	
//    	
//    	return "exito";
//	}
//    
    @RequestMapping(value = "addLesson", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String addLesson(@RequestBody Lesson lessonRequest, Model model) {
        Lesson lesson = new Lesson();
        model.addAttribute("lesson", lesson);
        return "exito";
    }
 
//    @RequestMapping(value = "editLesson", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,
//    consumes = MediaType.APPLICATION_JSON_VALUE)
//    public @ResponseBody String editLesson(@RequestBody Lesson lessonRequest, Model model) {
//        Lesson target = entityManager.find(Lesson.class, id);
//		model.addAttribute("lesson", target);
//		entityManager.remove(target);
//		target.setName(edited.getName());
//		target.setMaxSize(edited.getMaxSize());
//		target.setDescrip(edited.getDescrip());
//		return "exito";
//    }
//    
//    @RequestMapping(value = "removeLesson", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,
//    consumes = MediaType.APPLICATION_JSON_VALUE)
//    public @ResponseBody String removeLesson(@RequestBody Lesson lessonRequest, Model model) {
//        Lesson target = entityManager.find(Lesson.class, id);
//   		entityManager.remove(target);
//   		return "exito";
//    }
}
