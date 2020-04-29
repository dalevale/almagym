package es.ucm.fdi.iw.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Equipment;
import es.ucm.fdi.iw.model.Room;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;

@Controller
@RequestMapping("equipos")
public class EquipmentController {

	@Autowired
	private EntityManager entityManager;
    private static Logger log = LogManager.getLogger(EquipmentController.class);

	@Autowired
	private LocalData localData;

    public void getEquipment(Model model) {
    	List<Room> r = entityManager.createQuery("select r from Room r").getResultList();
		model.addAttribute("rooms", r);
		List<Equipment> e = entityManager.createQuery("select e from Equipment e where e.room != null").getResultList();
		model.addAttribute("equipments", e);
    }
    
    @GetMapping("/")
	public String getIndex(Model model) {
    	getEquipment(model);
		return "equipos";
	}
	
    @PostMapping("edit")
	@Transactional
	public String editEquipment(HttpServletResponse response, @ModelAttribute Equipment edited,
			Model model){
		Equipment target = entityManager.find(Equipment.class, edited.getId());
		target.setName(edited.getName());
		target.setQuantity(edited.getQuantity());
   		getEquipment(model);
   		return "redirect:/salas/";
	}
    
    @PostMapping("del")
   	@Transactional
   	public String deleteEquipment(@ModelAttribute Equipment edited,
   			Model model){
   		Equipment target = entityManager.find(Equipment.class, edited.getId());
   		entityManager.remove(target);
   		getEquipment(model);
   		return "redirect:/salas/";
   	}
    
    @PostMapping("add")
   	@Transactional
   	public String addEquipment(HttpServletResponse response, HttpSession session, @ModelAttribute Equipment toAdd,
   			@RequestParam (required = true)long roomId, Model model) throws IOException {
    	if(hasPermissions(response, session)) {
    		
    		toAdd.setRoom(entityManager.find(Room.class, roomId));
       		entityManager.persist(toAdd);
       		log.info("Successfully added Equipment with id {} ", toAdd.getId());
    	}
    	else
       		log.info("Failed to add Equipment with id {} ", toAdd.getId());

   		return "redirect:/salas/";
   	}
    
    
	private boolean hasPermissions(HttpServletResponse response, 
			HttpSession session) throws IOException {
		boolean valid = true;
		// check permissions
		User requester = (User) session.getAttribute("u");
		if (!requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres administrador!");
			valid = false;
			log.info("Error, no adiministrator priveleges!");
		}
		return valid;
	}
    
    
	@PostMapping("changephoto")
	public String postPhoto(HttpServletResponse response, @RequestParam("photo") MultipartFile photo, Equipment equipo,
			Model model, HttpSession session) throws IOException {
		// check permissions
		if(hasPermissions(response, session)) {
			String id = String.valueOf(equipo.getId());
			log.info("Updating photo for Room Id {}", equipo.getId());
			File f = localData.getFile("equipment", id);
			if (photo.isEmpty()) {
				log.info("failed to upload photo: emtpy file?");
			} else {
				try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f))) {
					byte[] bytes = photo.getBytes();
					stream.write(bytes);
				} catch (Exception e) {
					log.warn("Error uploading " + id + " ", e);
				}
				log.info("Successfully uploaded photo for Room Id {} into {}!", id, f.getAbsolutePath());
			}
			getEquipment(model);
		}
		
		return "redirect:/salas/";
	}
 
	@GetMapping(value = "/{id}/photo")
	public StreamingResponseBody getPhoto(@PathVariable long id, Model model) throws IOException {
		File f = localData.getFile("equipment", "" + id);
		InputStream in;
		if (f.exists()) {
			in = new BufferedInputStream(new FileInputStream(f));
		} else {
			in = new BufferedInputStream(
					getClass().getClassLoader().getResourceAsStream("static/img/default-sala.jpg"));
		}
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileCopyUtils.copy(in, os);
			}
		};
	}
}