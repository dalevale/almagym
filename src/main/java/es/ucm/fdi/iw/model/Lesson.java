package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

//Clase encargada guardar información sobre una clase del gimnasio (fitboxing,pilates,etc)
@Entity
public class Lesson {

	private long id;
	private User profe;
	private Room room;
	private String name;
	private LocalDateTime dateIni;
	private LocalDateTime dateFin;
	private int timeIni;
	private int duration;
	private int totalStudents;
	private List<Inscription> inscriptions; // tiene esta relacion ya que una clase esta enlazada con muchos usuarios

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

	public int getTimeIni() {
		return timeIni;
	}

	public void setTimeIni(int timeIni) {
		this.timeIni = timeIni;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
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

	@OneToMany(targetEntity = Inscription.class)
	@JoinColumn(name = "lesson_id")
	public List<Inscription> getInscriptions() {
		return inscriptions;
	}

	public void setInscriptions(List<Inscription> inscriptions) {
		this.inscriptions = inscriptions;
	}
}
