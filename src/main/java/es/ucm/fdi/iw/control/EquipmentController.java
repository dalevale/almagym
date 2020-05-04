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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
	
    @PostMapping("edit")
	@Transactional
	@ResponseBody
	public String editEquipment(HttpServletResponse response, @RequestBody Equipment.Transfer eRequest,
			HttpSession session) throws IOException {
    	
    	if(hasPermissions(response, session)) {
			Equipment e = new Equipment();
	        e.setId(eRequest.id); 
	        e.setName(eRequest.name);
	        e.setQuantity(eRequest.quantity);
	        e.setRoom(entityManager.find(Room.class, eRequest.room));
			entityManager.merge(e);  
			log.info("Successfully edited Room with id {} ", e.getId());
		}
    	else
       		log.info("Failed to edit Room with id {} ", 1);
   		return "exito";
	}
    
    @PostMapping("del/{id}")
   	@Transactional
   	@ResponseBody
   	public String deleteEquipment(@PathVariable long id, HttpServletResponse response,
   			HttpSession session) throws IOException {
    	
    	if(hasPermissions(response, session)) {
    		Equipment target = entityManager.find(Equipment.class, id);
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
   	public String addEquipment(HttpServletResponse response, @RequestBody Equipment.Transfer eRequest,
   			HttpSession session) throws IOException {
    	
    	if(hasPermissions(response, session)) {
    		Equipment e= new Equipment();
    		e.setName(eRequest.name);
    		e.setQuantity(eRequest.quantity);
    		e.setRoom(entityManager.find(Room.class, eRequest.room));
            entityManager.persist(e);
       		log.info("Successfully added Room with id {} ", eRequest.id);
            return String.valueOf(e.getId());
    	}
    	else
       		log.info("Failed to add Room");
   		return "exito";
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
    
    
	@PostMapping("changephoto/{id}")
	@ResponseBody
	public String postPhoto(@PathVariable long id, HttpServletResponse response, @RequestParam("photo") MultipartFile photo,
			HttpSession session) throws IOException {
		// check permissions
		if(hasPermissions(response, session)) {
			String idStr = String.valueOf(id);
			log.info("Updating photo for Room Id {}", id);
			File f = localData.getFile("equipment", idStr);
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
		}
		
		return "exito";
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