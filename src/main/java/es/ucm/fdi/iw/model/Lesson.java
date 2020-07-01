package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.transaction.Transactional;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import es.ucm.fdi.iw.model.Message.Transfer;

//Clase encargada guardar información sobre una clase del gimnasio (fitboxing,pilates,etc)
@Entity
public class Lesson {

	private long id;
	private User profe;
	private Room room;
	private String name;
	private LocalDateTime dateIni;
	private LocalDateTime dateFin;
	private int totalStudents;
	private List<Inscription> inscriptions = new ArrayList<>(); // tiene esta relacion ya que una clase esta enlazada con muchos usuarios

	/**
	 * Convierte colecciones de mensajes a formato JSONificable
	 * @param messages
	 * @return
	 * @throws JsonProcessingException
	 */
	public static List<Transfer> asTransferObjects(Collection<Lesson> lessons) {
		ArrayList<Transfer> all = new ArrayList<>();
		for (Lesson l : lessons) {
			all.add(new Transfer(l));
		}
		return all;
	}
	
	/**
	 * Objeto para persistir a/de JSON
	 * @author mfreire
	 * @author EnriqueTorrijos
	 */
	public static class Transfer {
		public long id;
		public long profeId;
		public long roomId;
		public String name;
		public String dateIni;
		public String dateFin;
		public int totalStudents;
		
		public Transfer() { 
		}
		
		public Transfer(Lesson l) {
			this.id = l.getId();
			this.profeId = l.profe.getId();
			this.roomId = l.room.getId();
			this.name = l.getName();
			this.dateIni = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(l.getDateIni());
			this.dateFin = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(l.getDateFin());
			this.totalStudents = l.getTotalStudents();
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@OneToOne(targetEntity = User.class)
	public User getProfe() {
		return profe;
	}

	public void setProfe(User profe) {
		this.profe = profe;
	}

	public LocalDateTime getDateIni() {
		return dateIni;
	}

	public void setDateIni(LocalDateTime dateIni) {
		this.dateIni = dateIni;
	}

	public LocalDateTime getDateFin() {
		return dateFin;
	}

	public void setDateFin(LocalDateTime dateFin) {
		this.dateFin = dateFin;
	}

	/**
	 * @return
	 */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTotalStudents() {
		return totalStudents;
	}

	public void setTotalStudents(int totalStudents) {
		this.totalStudents = totalStudents;
	}

	@ManyToOne(targetEntity = Room.class) // <-- evita crear User_Book
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(targetEntity = Inscription.class)
	@JoinColumn(name = "lesson_id")
	public List<Inscription> getInscriptions() {
		return inscriptions;
	}

	public void setInscriptions(List<Inscription> inscriptions) {
		this.inscriptions = inscriptions;
	}
	
//	public void addInscription(User user) {
//		
//		Inscription insc = new Inscription(user, this);
//		
//		
//		this.inscriptions.add(insc);
//	}
	
	
	public void removeInscription(User user) {
		
			for(int i=0;i<this.inscriptions.size();i++) {
   			
   			if(this.inscriptions.get(i).getUser() == user && 
   					this.inscriptions.get(i).getLesson() == this) {
   				this.inscriptions.remove(i);
   			}
 		
   		}
	}
	

    public boolean isInscript(User user) {
    	boolean esta = false;

   		for(int i=0;i<this.inscriptions.size();i++) {
   			
   			if(this.inscriptions.get(i).getUser().getId() == user.getId())
   				
   				esta = true;
   		}
        
   		return esta;
    }
	
    public boolean estaVacio() {
    	boolean esta = false;

   			if(this.inscriptions.size() >= this.totalStudents)
   				
   				esta = true;
   		
        
   		return !esta;
    }
    

	public int totalStudentsNow() {
		int result = this.totalStudents;
		if (this.inscriptions != null)
			result -= this.inscriptions.size();
		return result;
	}

	public boolean isTeacherClass(Long idUser) {
		boolean loEs = false;
		if (this.getProfe().getId() == idUser)
				loEs = true;
		return loEs;
	}
}
