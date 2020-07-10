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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;
import es.ucm.fdi.iw.model.User.Transfer;

/**
 * User-administration controller
 * 
 * @author mfreire
 */
@Controller
@RequestMapping("user")
public class UserController {

	@Autowired
	private static final Logger log = LogManager.getLogger(UserController.class);

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private LocalData localData;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@GetMapping("/{id}")
	public String getUser(@PathVariable long id, Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		return "user";
	}

	@PostMapping("/{id}")
	@Transactional
	public String postUser(HttpServletResponse response, @PathVariable long id, @ModelAttribute User edited,
			@RequestParam(required = false) String pass2, Model model, HttpSession session) throws IOException {

		User target = entityManager.find(User.class, id);

		if (target == null) {
			edited.setEnabled((byte) 1);
			entityManager.persist(edited);
			entityManager.flush();

			return "redirect:/index/";
		}

		model.addAttribute("user", target);

		User requester = (User) session.getAttribute("u");
		if (requester.getId() != target.getId() && !requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres administrador, y Ã©ste no es tu perfil");
		}

		if (edited.getPassword() != null && edited.getPassword().equals(pass2)) {
			target = entityManager.merge(edited);
			target.setPassword(passwordEncoder.encode(edited.getPassword()));
		}
		return "user";
	}

	// @GetMapping("/search") con un parametro nombre, devuelve usuarios+ids con un nombre LIKE ese
	// para poder enviarles mensajes
	@GetMapping(value = "/searchUsers/{username}", produces = "application/json")
	@ResponseBody
	public List<Transfer> searchUsersByUsername(@PathVariable String username, HttpSession session) {
		List<User> userList = entityManager.createQuery("SELECT u FROM User u WHERE u.username LIKE CONCAT('%',:username,'%') AND u.enabled = 1", User.class)
				.setParameter("username", username).getResultList();
		List<Transfer> transferUserList = new ArrayList<Transfer>();
		for(User u: userList) {
			if (u.getId() != ((User)session.getAttribute("u")).getId()){
				Transfer t = new Transfer(u);
				transferUserList.add(t);
			}
		}
		return transferUserList;
	}
	
	@GetMapping(value = "/getIdByUsername/{username}")
	@ResponseBody
	public long getIdByUsername(@PathVariable String username, HttpSession session) {
		long t;
		try {
			User u = entityManager.createNamedQuery("User.byUsername", User.class)
					.setParameter("username", username)
					.getSingleResult();
			t = u.getId();
		}
		catch (NoResultException e){
			t = 0;
		}
		return t;
	}

	@PostMapping("/{id}/msg")
	@ResponseBody
	@Transactional
	public String postMsg(@PathVariable long id, 
			@RequestBody JsonNode o, Model model, HttpSession session) 
		throws JsonProcessingException {
		
		String text = o.get("message").asText();
		User u = entityManager.find(User.class, id);
		User sender = entityManager.find(
				User.class, ((User)session.getAttribute("u")).getId());
		model.addAttribute("user", u);
		
		// construye mensaje, lo guarda en BD
		Message m = new Message();
		m.setRecipient(u);
		m.setSender(sender);
		m.setDateSent(LocalDateTime.now());
		m.setText(text);
		entityManager.persist(m);
		entityManager.flush(); // to get Id before commit
		
		// construye json
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("from", sender.getUsername());
		rootNode.put("to", u.getUsername());
		rootNode.put("text", text);
		rootNode.put("id", m.getId());
		rootNode.put("userId", sender.getId());
		String json = mapper.writeValueAsString(rootNode);
		
		log.info("Sending a message to {} with contents '{}'", id, json);

		messagingTemplate.convertAndSend("/user/"+u.getUsername()+"/queue/updates", json);
		return "{\"result\": \"message sent.\"}";
	}	

	@GetMapping(value = "/{id}/photo")
	public StreamingResponseBody getPhoto(@PathVariable long id) throws IOException {
		File f = localData.getFile("user", "" + id);
		InputStream in;
		if (f.exists()) {
			in = new BufferedInputStream(new FileInputStream(f));
		} else {
			in = new BufferedInputStream(
					getClass().getClassLoader().getResourceAsStream("static/img/unknown-user.jpg"));
		}
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileCopyUtils.copy(in, os);
			}
		};
	}

	@PostMapping("/{id}/photo")
	public String postPhoto(HttpServletResponse response, @RequestParam("photo") MultipartFile photo,
			@PathVariable("id") String id, Model model, HttpSession session) throws IOException {
		User target = entityManager.find(User.class, Long.parseLong(id));
		model.addAttribute("user", target);

		// check permissions
		User requester = (User) session.getAttribute("u");
		if (requester.getId() != target.getId() && !requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres administrador, y éste no es tu perfil");
			return "user";
		}

		log.info("Updating photo for user {}", id);
		File f = localData.getFile("user", id);
		if (photo.isEmpty()) {
			log.info("failed to upload photo: emtpy file?");
		} else {
			try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f))) {
				byte[] bytes = photo.getBytes();
				stream.write(bytes);
			} catch (Exception e) {
				log.warn("Error uploading " + id + " ", e);
			}
			log.info("Successfully uploaded photo for {} into {}!", id, f.getAbsolutePath());
		}
		return "user";
	}

	@GetMapping(value = "/adduser")
	@Transactional
	public String create(Model model, HttpSession session) {
		User u = new User();
		model.addAttribute("user", u);
		u.setPassword("0");
		u.setRoles("CUSTOMER");
		entityManager.persist(u);
		return "user";
	}

	@PostMapping("/delete/{id}")
	@ResponseBody
	@Transactional
	public String remove(@PathVariable long id, Model model) {
		User target = entityManager.find(User.class, id);
		entityManager.remove(target);
		log.info("Successfully removed User with id {} ", target.getId());
		return "exito";
	}
}
