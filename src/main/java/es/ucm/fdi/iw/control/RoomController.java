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

	@Autowired
	private LocalData localData;
    
    private void getRooms(Model model) {
    	List<Room> r = entityManager.createQuery("select r from Room r").getResultList();
		model.addAttribute("rooms", r);
		log.info("Successfully fetched rooms list from database.");
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
    
    @GetMapping("/")
	public String getIndex(Model model) {
    	getRooms(model);
		return "salas";
	}
    
    @GetMapping(value = "/{id}/photo")
	public StreamingResponseBody getPhoto(@PathVariable long id, Model model) throws IOException {
		File f = localData.getFile("room", "" + id);
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

    
    @PostMapping("edit")
	@Transactional
	public String editRoom(HttpServletResponse response, @ModelAttribute Room edited,
			Model model, HttpSession session) throws IOException {
		
    	if(hasPermissions(response, session)) {
	    	Room target = entityManager.find(Room.class, edited.getId());
			target.setName(edited.getName());
			target.setMaxSize(edited.getMaxSize());
			target.setDescrip(edited.getDescrip());
			log.info("Successfully edited Room with id {} ", target.getId());
	   		getRooms(model);
		}
    	else
       		log.info("Failed to edit Room with id {} ", edited.getId());
   		return "salas";
	}
    
    @PostMapping("del")
   	@Transactional
   	public String deleteRoom(HttpServletResponse response, @ModelAttribute Room edited,
   			Model model, HttpSession session) throws IOException {
    	
    	if(hasPermissions(response, session)) {
    		Room target = entityManager.find(Room.class, edited.getId());
       		entityManager.remove(target);
       		log.info("Successfully removed Room with id {} ", edited.getId());
       		getRooms(model);
    	}
    	else
       		log.info("Failed to remove Room with id {} ", edited.getId());
   		return "salas";
   	}
    
    @PostMapping("add")
   	@Transactional
   	public String addRoom(HttpServletResponse response, @ModelAttribute Room toAdd,
   			Model model, HttpSession session) throws IOException {
    	
    	if(hasPermissions(response, session)) {
       		entityManager.persist(toAdd);
       		log.info("Successfully added Room with id {} ", toAdd.getId());
       		getRooms(model);
    	}
    	else
       		log.info("Failed to add Room with id {} ", toAdd.getId());
   		return "salas";
   	}
    
	@PostMapping("changephoto")
	public String postPhoto(HttpServletResponse response, @RequestParam("photo") MultipartFile photo, Room room,
			Model model, HttpSession session) throws IOException {
		// check permissions
		if(hasPermissions(response, session)) {
			String id = String.valueOf(room.getId());
			log.info("Updating photo for Room Id {}", room.getId());
			File f = localData.getFile("room", id);
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
			getRooms(model);
		}
		
		return "salas";
	}
}