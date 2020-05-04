package es.ucm.fdi.iw.model;

import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

//Esta clase tiene informacion sonbre una maquina del gimnasio la cual esta relacionada con una sala
@Entity
public class Equipment {

	private long id;
	private String name;
	private int quantity;
	private Room room;
	
	/**
	 * Objeto para persistir a/de JSON
	 * @author mfreire
	 * @author EnriqueTorrijos
	 */
	public static class Transfer {
		public long id;
		public String name;
		public int quantity;
		public long room;
		
		public Transfer() { 
		}
		
		public Transfer(Equipment e) {
			this.id = e.getId();
			this.name = e.getName();
			this.quantity = e.getQuantity();
			this.room = e.room.getId();
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

	@ManyToOne(targetEntity = Room.class)
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
