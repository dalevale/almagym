package es.ucm.fdi.iw.control;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.ucm.fdi.iw.model.Inscription;
import es.ucm.fdi.iw.model.Lesson;
import es.ucm.fdi.iw.model.Room;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Lesson.Transfer;

@Controller
@RequestMapping("clases")
public class LessonController {

	@Autowired
	private EntityManager entityManager;
	
    private static Logger log = LogManager.getLogger(
    		LessonController.class);

    @GetMapping("/")
    public String getLessons(HttpSession session, Model model) {
        List<Lesson> l = entityManager
            .createQuery("select l from Lesson l", Lesson.class)
            .getResultList();
        List<Room> r = entityManager
            .createQuery("select r from Room r", Room.class)
            .getResultList();
        List<User> p = entityManager
            .createQuery("select p from User p where roles = 'USER,TEACHER'", User.class)
            .getResultList();
    	model.addAttribute("lessons", l);
    	model.addAttribute("rooms", r);
    	model.addAttribute("profes", p);
    	return "clases";
    }

    @PostMapping(path = "/getLessons", produces = "application/json")
	@Transactional // para no recibir resultados inconsistentes
	@ResponseBody // para indicar que no devuelve vista, sino un objeto (jsonizado)
	public List<Lesson.Transfer> retrieveLessons(HttpSession session) {		
		log.info("Generating lessons list");
        List<Lesson> l = entityManager
            .createQuery("select l from Lesson l", Lesson.class)
            .getResultList();
		List<Transfer> lessons = new ArrayList<Transfer>();
				lessons = Lesson.asTransferObjects(l);
		return lessons;
	}	
    
    private Lesson fromTransfer(Lesson.Transfer lessonRequest) {
        Lesson lesson = new Lesson();
        lesson.setProfe(entityManager.find(User.class, lessonRequest.profeId)); 
        lesson.setRoom(entityManager.find(Room.class, lessonRequest.roomId)); 
        lesson.setName(lessonRequest.name);
        lesson.setTotalStudents(lessonRequest.totalStudents);
        lesson.setDateIni(LocalDateTime.parse(lessonRequest.dateIni, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        lesson.setDateFin(LocalDateTime.parse(lessonRequest.dateFin, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return lesson;
    }

    @PostMapping("addLesson")        
    @ResponseBody
    @Transactional
    public String addLesson(@RequestBody Lesson.Transfer lessonRequest) {
        Lesson lesson = fromTransfer(lessonRequest);
        entityManager.persist(lesson);
        return String.valueOf(lesson.getId());
    }
 
    @PostMapping("editLesson")        
    @ResponseBody
    @Transactional
    public String editLesson(@RequestBody Lesson.Transfer lessonRequest) { 
        Lesson lesson = fromTransfer(lessonRequest);
        lesson.setId(lessonRequest.id); 
       	entityManager.merge(lesson);  
		return "exito";
    }
    
    @PostMapping("removeLesson/{id}")    
    @ResponseBody
    @Transactional
    public String removeLesson(@PathVariable long id, Model model) {
        Lesson target = entityManager.find(Lesson.class, id);
   		entityManager.remove(target);
   		return "exito";
    }
   
    
    @GetMapping(value = "switchInscription2")    
    @Transactional
    public String switchInscription2(@RequestParam long idUser, @RequestParam long idLesson, HttpSession session) {
    	User usuario = entityManager.find(User.class, idUser);
    	Lesson lesson = entityManager.find(Lesson.class, idLesson);
        
		if (lesson.isInscript(usuario)) {
			lesson.removeInscription(usuario);
		} else if(lesson.estaVacio()){
			Inscription insc = new Inscription();
			insc.setLesson(lesson);
			insc.setUser(usuario);
			
			entityManager.persist(insc);
			lesson.getInscriptions().add(insc);
			
		}
   		
		entityManager.flush();
		
		
   		log.info("Successfully switch inscription: ", lesson.getId());
    	return "forward:/clases/";
    }
    
}