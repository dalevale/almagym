package es.ucm.fdi.iw.control;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.model.Equipment;
import es.ucm.fdi.iw.model.Lesson;
import es.ucm.fdi.iw.model.Room;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;

@Controller
@RequestMapping("salas")
public class RoomController {

	@Autowired
	private EntityManager entityManager;
    private static Logger log = LogManager.getLogger(RoomController.class);

    public void getRooms(Model model) {
    	List<Room> r = entityManager.createQuery("select r from Room r").getResultList();
		model.addAttribute("rooms", r);
		List<Equipment> e = entityManager.createQuery("select e from Equipment e where e.room != null").getResultList();
		model.addAttribute("equipments", e);
    }
    
    @GetMapping("/")
	public String getIndex(Model model) {
    	getRooms(model);
		return "salas";
	}
	
    @PostMapping("edit")
	@Transactional
	public String editRoom(HttpServletResponse response, @ModelAttribute Room edited,
			Model model) throws IOException {
		Room target = entityManager.find(Room.class, edited.getId());
		target.setName(edited.getName());
		target.setMaxSize(edited.getMaxSize());
		target.setDescrip(edited.getDescrip());
   		getRooms(model);
   		return "salas";
	}
    
    @PostMapping("del")
   	@Transactional
   	public String deleteRoom(@ModelAttribute Room edited,
   			Model model) throws IOException {
   		Room target = entityManager.find(Room.class, edited.getId());
   		entityManager.remove(target);
   		getRooms(model);
   		return "salas";
   	}
    
    @PostMapping("add")
   	@Transactional
   	public String addRoom(@ModelAttribute Room toAdd,
   			Model model) throws IOException {
    	toAdd.setImgPath("img/default-salas.jpg");
   		entityManager.persist(toAdd);
   		getRooms(model);
   		return "salas";
   	}
}