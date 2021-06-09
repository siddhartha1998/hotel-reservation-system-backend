package np.com.hotel.hotelreservationapp.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name="tbl_room")

public class Room {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String hotelUsername;

	@NotBlank
	private Long roomNumber;
	
	@NotBlank
	private String roomType;
	
	
	@NotBlank
	private String description;
	

	
	private boolean active=true;
	
	private boolean availability=true;
	
	@ManyToOne
	private Hotel hotel;
	
	@OneToOne(mappedBy = "room")
	private TemporaryReservation temp;
	
	@JsonIgnore
	@OneToMany(mappedBy = "room")
	private List<ReservationDetails>reservation = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "room")
	private List<RoomPicture>roomPic = new ArrayList<>();
	

	
public Room() {
		
	
	}


public Room(String hotelUsername, @NotBlank Long roomNumber, @NotBlank String roomType,
	 String description, boolean active, boolean availability, 
	 Hotel hotel, List<ReservationDetails> reservation) {
	super();
	this.hotelUsername = hotelUsername;
	this.roomNumber = roomNumber;
	this.roomType = roomType;
	this.description = description;
	this.active = active;
	this.availability = availability;
	this.hotel = hotel;
	this.reservation = reservation;
	
	
	
}


public Long getId() {
	return id;
}


public void setId(Long id) {
	this.id = id;
}


public String getHotelUsername() {
	return hotelUsername;
}


public void setHotelUsername(String hotelUsername) {
	this.hotelUsername = hotelUsername;
}


public Long getRoomNumber() {
	return roomNumber;
}


public void setRoomNumber(Long roomNumber) {
	this.roomNumber = roomNumber;
}


public String getRoomType() {
	return roomType;
}


public void setRoomType(String roomType) {
	this.roomType = roomType;
}


public String getDescription() {
	return description;
}


public void setDescription(String description) {
	this.description = description;
}



public boolean isActive() {
	return active;
}


public void setActive(boolean active) {
	this.active = active;
}


public Hotel getHotel() {
	return hotel;
}


public void setHotel(Hotel hotel) {
	this.hotel = hotel;
}


public boolean isAvailability() {
	return availability;
}


public void setAvailability(boolean availability) {
	this.availability = availability;
}


public List<RoomPicture> getRoomPic() {
	return roomPic;
}


public void setRoomPic(List<RoomPicture> roomPic) {
	this.roomPic = roomPic;
}


public TemporaryReservation getTemp() {
	return temp;
}


public void setTemp(TemporaryReservation temp) {
	this.temp = temp;
}


public List<ReservationDetails> getReservation() {
	return reservation;
}


public void setReservation(List<ReservationDetails> reservation) {
	this.reservation = reservation;
}











}