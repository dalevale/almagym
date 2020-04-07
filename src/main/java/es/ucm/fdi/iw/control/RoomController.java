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
	
    private static Logger log = LogManager.getLogger(
        RoomController.class);

    @GetMapping("/")
	public String getRooms(HttpSession session, Model model) {
		List<Room> r = entityManager.createQuery("select r from Room r").getResultList();
		model.addAttribute("rooms", r);
		List<Equipment> e = entityManager.createQuery("select e from Equipment e where e.room != null").getResultList();
		model.addAttribute("equipments", e);
		return "salas";
	}
	
    @PostMapping("edit/{id}")
	@Transactional
	public String editRoom(HttpServletResponse response, @PathVariable long id, @ModelAttribute Room edited,
			Model model) throws IOException {
		Room target = entityManager.find(Room.class, id);
		model.addAttribute("room", target);
		entityManager.remove(target);
		target.setName(edited.getName());
		target.setMaxSize(edited.getMaxSize());
		target.setDescrip(edited.getDescrip());
		List<Room> r = entityManager.createQuery("select r from Room r").getResultList();
		model.addAttribute("rooms", r);
		List<Equipment> e = entityManager.createQuery("select e from Equipment e where e.room != null").getResultList();
		model.addAttribute("equipments", e);
		return "salas";
	}
    
    @PostMapping("del/{id}")
   	@Transactional
   	public String deleteRoom(@PathVariable long id, @ModelAttribute Room edited,
   			Model model) throws IOException {
   		Room target = entityManager.find(Room.class, id);
//   		model.addAttribute("room", target);
   		entityManager.remove(target);
   		List<Room> r = entityManager.createQuery("select r from Room r").getResultList();
   		model.addAttribute("rooms", r);
   		List<Equipment> e = entityManager.createQuery("select e from Equipment e where e.room != null").getResultList();
   		model.addAttribute("equipments", e);
   		return "salas";
   	}

}
