package es.ucm.fdi.iw.control;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import es.ucm.fdi.iw.model.Lesson;
import es.ucm.fdi.iw.model.Room;
import es.ucm.fdi.iw.model.User;

@Controller
@RequestMapping("clases")
public class LessonController {

	@Autowired
	private EntityManager entityManager;
	
    private static Logger log = LogManager.getLogger(
    		LessonController.class);

    @GetMapping("/")
    public String getLessons(HttpSession session, Model model) {
    	List<Lesson> l = entityManager.createQuery("select l from Lesson l").getResultList();
    	List<Lesson> r = entityManager.createQuery("select r from Room r").getResultList();
    	model.addAttribute("lessons", l);
    	model.addAttribute("rooms", r);
    	return "clases";
    }
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
    @PostMapping("addLesson")        
    @ResponseBody
    @Transactional
    public String addLesson(@RequestBody Lesson.Transfer lessonRequest) {
        Lesson lesson = new Lesson();
        lesson.setProfe(entityManager.find(User.class, lessonRequest.profeId)); 
        lesson.setRoom(entityManager.find(Room.class, lessonRequest.roomId)); 
        lesson.setName(lessonRequest.name);
        lesson.setTotalStudents(lessonRequest.totalStudents);
        lesson.setDateIni(LocalDateTime.parse(lessonRequest.dateIni, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        lesson.setDateFin(LocalDateTime.parse(lessonRequest.dateFin, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        entityManager.persist(lesson);
        return String.valueOf(lesson.getId());
    }
 
    @PostMapping("editLesson")        
    @ResponseBody
    @Transactional
    public String editLesson(@RequestBody Lesson.Transfer lessonRequest) { 
        Lesson lesson = new Lesson();
        lesson.setId(lessonRequest.id); 
        lesson.setRoom(entityManager.find(Room.class, lessonRequest.roomId)); 
        lesson.setName(lessonRequest.name);
        lesson.setTotalStudents(lessonRequest.totalStudents);
        lesson.setDateIni(LocalDateTime.parse(lessonRequest.dateIni, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        lesson.setDateFin(LocalDateTime.parse(lessonRequest.dateFin, DateTimeFormatter.ISO_LOCAL_DATE_TIME)); 
		entityManager.merge(lesson);  
		return "exito";
    }
    
    @PostMapping("removeLesson/{id}")    
    @ResponseBody
    @Transactional
    public String removeLesson(@PathVariable long id, Model model) {
        Lesson target = entityManager.find(Lesson.class, id);
   		entityManager.remove(target);
    	List<Lesson> l = entityManager.createQuery("select l from Lesson l").getResultList();
    	model.addAttribute("lessons", l);
   		return "exito";
    }
}

// file edit selection view go run [terminal] help