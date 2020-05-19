package es.ucm.fdi.iw.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import es.ucm.fdi.iw.LocalData;
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
    
    @PostMapping("edit")
	@Transactional
	@ResponseBody
	public String editRoom(HttpServletResponse response, @RequestBody Room.Transfer roomRequest
			, HttpSession session) throws IOException {
		
    	if(hasPermissions(response, session)) {
			Room room = new Room();
	        room.setId(roomRequest.id); 
	        room.setName(roomRequest.name);
	        room.setMaxSize(roomRequest.maxSize); 
	        room.setDescrip(roomRequest.descrip);
	        //Get list of equipments and lessons
	        Room orig = entityManager.find(Room.class, room.getId());
	        room.setEquipments(orig.getEquipments());
	        room.setLessons(orig.getLessons());
			entityManager.merge(room);  
			log.info("Successfully edited Room with id {} ", room.getId());
		}
    	else
       		log.info("Failed to edit Room with id {} ", roomRequest.id);
   		return "exito";
	}
    
    @PostMapping("del/{id}")
   	@Transactional
   	@ResponseBody
   	public String deleteRoom(@PathVariable long id, HttpServletResponse response,
   			HttpSession session) throws IOException {
    	
    	if(hasPermissions(response, session)) {
    		Room target = entityManager.find(Room.class, id);
       		entityManager.remove(target);
       		log.info("Successfully removed Room with id {} ", id);
    	}
    	else
       		log.info("Failed to remove Room with id {} ", id);
   		return "exito";
   	}
    
    @PostMapping("add")
   	@Transactional
   	@ResponseBody
   	public String addRoom(HttpServletResponse response, @RequestBody Room.Transfer roomRequest,
   			HttpSession session) throws IOException {
    	
    	if(hasPermissions(response, session)) {
    		Room room = new Room();
    		room.setDescrip(roomRequest.descrip); 
    		room.setName(roomRequest.name);
    		room.setMaxSize(roomRequest.maxSize);
            entityManager.persist(room);
       		log.info("Successfully added Room with id {} ", room.getId());
            return String.valueOf(room.getId());
    	}
    	else
       		log.info("Failed to add Room");
   		return "salas";
   	}
    
    @GetMapping(value = "/{id}/photo")
	public StreamingResponseBody getPhoto(@PathVariable long id) throws IOException {
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
    
	@PostMapping("changephoto/{id}")
	@ResponseBody
	public String postPhoto(@PathVariable long id, HttpServletResponse response, @RequestParam("photo") MultipartFile photo,
			HttpSession session) throws IOException {
		// check permissions
		if(hasPermissions(response, session)) {
			String idStr = String.valueOf(id);
			log.info("Updating photo for Room Id {}", id);
			File f = localData.getFile("room", idStr);
			if (photo.isEmpty()) {
				log.info("failed to upload photo: emtpy file?");
			} else {
				try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f))) {
					byte[] bytes = photo.getBytes();
					stream.write(bytes);
					stream.close();
				} catch (Exception e) {
					log.warn("Error uploading " + id + " ", e);
				}
				log.info("Successfully uploaded photo for Room Id {} into {}!", id, f.getAbsolutePath());
			}
		}
		
		return "exito";
	}
}