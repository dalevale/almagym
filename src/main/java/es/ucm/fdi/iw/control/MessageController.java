package es.ucm.fdi.iw.control;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.Room;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Message.Transfer;

/**
 * User-administration controller
 * 
 * @author mfreire
 */
@Controller
@RequestMapping("messages")
public class MessageController {
	
	private static final Logger log = LogManager.getLogger(MessageController.class);
	
	@Autowired 
	private EntityManager entityManager;
		
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@GetMapping("/")
	//TODO cambiar query a named
	public String getIndex(Model model, HttpSession session) {
		long userId = ((User)session.getAttribute("u")).getId();		
		User u = entityManager.find(User.class, userId);
		List<Message> lastMsgs = new ArrayList<Message>();
		List<Message> allMsgs = entityManager.createQuery("SELECT m FROM Message m WHERE m.recipient.id = :userId "
				+ "OR m.sender.id = :userId ORDER BY m.id DESC", Message.class)
				.setParameter("userId", userId)
				.getResultList();
		for(Message m : allMsgs)
			putOnlyLastMsgs(u, m, lastMsgs);
		log.info("Generating last messages for each conversation list for user {} ({} messages)", 
				u.getUsername(),
				lastMsgs.size());
		model.addAttribute("lastMessages", lastMsgs);
		return "messages";
	}

	private void putOnlyLastMsgs(User user, Message m, List<Message> lastMsgs) {
		User otherUser = m.getRecipient().getId() == user.getId()? m.getSender() : m.getRecipient();
		boolean valid = true;
		for(Message message: lastMsgs) {
			if(message.getRecipient().getId() == otherUser.getId() 
					|| message.getSender().getId() == otherUser.getId()) 
				valid = false;
		}
		if(valid)
			lastMsgs.add(m);
	}
	
	@GetMapping(path = "/getConvosWithUser/{otherUserId}", produces = "application/json")
	@Transactional
	@ResponseBody
	public List<Message.Transfer> getConvosWithUser(@PathVariable long otherUserId, HttpSession session) {
		long userId = ((User)session.getAttribute("u")).getId();		
		List<Message> exchangedMsgs = entityManager.createQuery("SELECT m FROM Message m WHERE (m.recipient.id = :userId AND m.sender.id = :otherUserId)" 
				+ "OR (m.recipient.id = :otherUserId AND m.sender.id = :userId)ORDER BY m.id DESC", Message.class)
				.setParameter("userId", userId)
				.setParameter("otherUserId", otherUserId)
				.getResultList();
		return Message.asTransferObjects(exchangedMsgs);
	}
	
	//show how many unread -- FIXME: añadir, para cada interlocutuar, id, nombre, y cuantos sin leer
	@GetMapping(value = "/unread", produces = "application/json")
	@ResponseBody
	public String checkUnread(HttpSession session) {
		long userId = ((User)session.getAttribute("u")).getId();		
		long unread = entityManager.createNamedQuery("Message.countUnread", Long.class)
			.setParameter("userId", userId)
			.getSingleResult();
		session.setAttribute("unread", unread);
		return "{\"unread\": " + unread + "}";
	}
	
	@GetMapping(value="/readMsg/{id}", produces = "application/json")
	@Transactional
	@ResponseBody
	public String readMessage(@PathVariable long id, HttpSession session) throws IOException {
		User u = ((User)session.getAttribute("u"));
		String username = u.getUsername();
		Message m = entityManager.find(Message.class, id);
		String ret = "";
		// ojo: solo si has recibido ese mensaje
		if(m.getRecipient().getUsername().equals(username)) {
			LocalDateTime timeRead = LocalDateTime.now();
			m.setDateRead(timeRead);
			entityManager.persist(m);
			entityManager.flush();
			ret = timeRead.toString();
		}
		return ret;
	}
		
	@PostMapping(path = "/sendMsgToUser/{id}")
	@ResponseBody
	@Transactional
	public String postMsg(@PathVariable long id,Model model, @RequestBody Message.Transfer messageRequest,
			HttpSession session) throws IOException {
		
		String text = messageRequest.getText();
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
		rootNode.put("sent", m.getDateSent().toString());
		String json = mapper.writeValueAsString(rootNode);
		
		log.info("Sending a message to {} with contents '{}'", id, json);

		messagingTemplate.convertAndSend("/user/"+u.getUsername()+"/queue/updates", json);
		return "{\"result\": \"message sent.\"}";
	}	
	
}