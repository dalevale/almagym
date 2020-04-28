package es.ucm.fdi.iw.model;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
public class Room {
	private long id;
	private String name;
	private int maxSize;
	private String descrip;
	private List<Lesson> lessons = new ArrayList<>();
	private List<Equipment> equipments = new ArrayList<>();

	/**
	 * Objeto para persistir a/de JSON
	 * @author mfreire
	 * @author EnriqueTorrijos
	 */
	public static class Transfer {
		public long id;
		public String name;
		public int maxSize;
		public String descrip;
		public Transfer() { 
		}
		
		public Transfer(Room r) {
			this.id = r.getId();
			this.name = r.getName();
			this.maxSize = r.getMaxSize();
			this.descrip = r.getDescrip();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public String getDescrip() {
		return descrip;
	}

	public void setDescrip(String descrip) {
		this.descrip = descrip;
	}
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(targetEntity = Lesson.class)
	@JoinColumn(name = "room_id")
	public List<Lesson> getLessons() {
		return lessons;
	}

	public void setLessons(List<Lesson> lessons) {
		this.lessons = lessons;
	}
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(targetEntity = Equipment.class)
	@JoinColumn(name = "room_id") // <-- evita crear User_Book
	public List<Equipment> getEquipments() {
		return equipments;
	}

	public void setEquipments(List<Equipment> equipments) {
		this.equipments = equipments;
	}

	
	/** Checks if this room is deletable
	 * 	or in other words
	 *  checks if this room has classes inscribed in it
	 *  returns boolean
	 */
	public boolean hasNoClasses() {
		return lessons.isEmpty();
	}
}
