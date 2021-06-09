package np.com.hotel.hotelreservationapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="tbl_reservation_details")
public class ReservationDetails {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	private String hotelName;
	
	
	@NotBlank
	private Long roomNumber;
	
	private String reservationTime;
	
	@NotBlank
	private String checkInDate;
	
	private String checkOutDate;
	
	@NotBlank
	private String noOfGuest;
	
	@ManyToOne
	private Customer customer;
	
	
	@NotBlank
	private String type;
	
	private boolean active=true;
	
	
	@ManyToOne
	private Hotel hotel;
	
	@JsonIgnore
	@ManyToOne
	private Room room;
	
//	@JsonIgnore
//	@OneToOne
//	private Room room;
	

	public ReservationDetails() {
		
	}

	public ReservationDetails(String hotelName, Room room,  Long roomNumber,String checkInDate,
			String checkOutDate, String noOfGuest, Customer customer,
			 String type,String reservationTime, Hotel hotel) {
		super();
		this.hotelName=hotelName;
		this.room=room;
		this.roomNumber = roomNumber;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
		this.noOfGuest = noOfGuest;
		this.customer = customer;
		this.type = type;
		this.reservationTime=reservationTime;
		this.hotel=hotel;
	}

	
	public String getHotelName() {
		return hotelName;
	}


	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}




	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}




	public Long getRoomNumber() {
		return roomNumber;
	}

	

	public void setRoomNumber(Long roomNumber) {
		this.roomNumber = roomNumber;
	}

	
	public String getReservationTime() {
		return reservationTime;
	}


	public void setReservationTime(String reservationTime) {
		this.reservationTime = reservationTime;
	}



	public String getCheckInDate() {
		return checkInDate;
	}


	public void setCheckInDate(String checkInDate) {
		this.checkInDate = checkInDate;
	}


	public String getCheckOutDate() {
		return checkOutDate;
	}


	public void setCheckOutDate(String checkOutDate) {
		this.checkOutDate = checkOutDate;
	}


	public String getNoOfGuest() {
		return noOfGuest;
	}


	public void setNoOfGuest(String noOfGuest) {
		this.noOfGuest = noOfGuest;
	}


	public Customer getCustomer() {
		return customer;
	}


	public void setCustomer(Customer customer) {
		this.customer = customer;
	}



	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
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

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	


	


}
