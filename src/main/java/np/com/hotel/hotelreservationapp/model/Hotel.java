package np.com.hotel.hotelreservationapp.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="tbl_hotel")

public class Hotel {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String hotelName;
	
	@NotBlank
	private String hotelUsername;
	
	@NotBlank
	private String hotelOwner;
	
	@NotBlank
	private String city;
	
	@NotBlank
	private String hotelAddress;
	
	@NotBlank
	private Long phone;
	
	@NotBlank
	private Long panNumber;
	
	@NotBlank
	private String document;
	
	@NotBlank
	private String status;
	
	@NotBlank
	private String description;
	
	private boolean active=true;
	
	private String latitude;
	
	private String longitude;
	
	@JsonIgnore
	@OneToMany(mappedBy = "hotel")
	private List<Room>room = new ArrayList<>();
	
	
	/*@JsonIgnore
	@OneToMany(mappedBy = "hotelOffline")
	private List<OfflineReservation>offlineReservation=new ArrayList<>();
	*/
	@JsonIgnore
	@OneToMany(mappedBy = "hotel")
	private List<ReservationDetails>reservation=new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "hotel")
	private List<TemporaryReservation>temporaryReservation=new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "hotel")
	private List<HotelPicture>hotelPic=new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "hotel")
	private List<HotelDocument>hotelDocument=new ArrayList<>();
	
	@JsonIgnore
	@OneToOne
	private UsersAuthentication user;

	public Hotel() {
		
	}

	public Hotel(String hotelName,String hotelUsername, String hotelOwner,
			String city, String hotelAddress,  Long phone,  Long panNumber,
			 String document, String status,  String description, boolean active,
			String latitude, String longitude, List<Room> room, List<ReservationDetails> reservation,
			List<TemporaryReservation> temporaryReservation,  UsersAuthentication user) {
		super();
		this.hotelName = hotelName;
		this.hotelUsername = hotelUsername;
		this.hotelOwner = hotelOwner;
		this.city = city;
		this.hotelAddress = hotelAddress;
		this.phone = phone;
		this.panNumber = panNumber;
		this.document = document;
		this.status = status;
		this.description = description;
		this.active = active;
		this.latitude = latitude;
		this.longitude = longitude;
		this.room = room;
		this.reservation = reservation;
		this.temporaryReservation = temporaryReservation;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public String getHotelUsername() {
		return hotelUsername;
	}

	public void setHotelUsername(String hotelUsername) {
		this.hotelUsername = hotelUsername;
	}

	public String getHotelOwner() {
		return hotelOwner;
	}

	public void setHotelOwner(String hotelOwner) {
		this.hotelOwner = hotelOwner;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHotelAddress() {
		return hotelAddress;
	}

	public void setHotelAddress(String hotelAddress) {
		this.hotelAddress = hotelAddress;
	}

	public Long getPhone() {
		return phone;
	}

	public void setPhone(Long phone) {
		this.phone = phone;
	}

	public Long getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(Long panNumber) {
		this.panNumber = panNumber;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public List<Room> getRoom() {
		return room;
	}

	public void setRoom(List<Room> room) {
		this.room = room;
	}

	public List<ReservationDetails> getReservation() {
		return reservation;
	}

	public void setReservation(List<ReservationDetails> reservation) {
		this.reservation = reservation;
	}
	
	
	public List<TemporaryReservation> getTemporaryReservation() {
		return temporaryReservation;
	}

	public void setTemporaryReservation(List<TemporaryReservation> temporaryReservation) {
		this.temporaryReservation = temporaryReservation;
	}

	public List<HotelPicture> getHotelPic() {
		return hotelPic;
	}

	public void setHotelPic(List<HotelPicture> hotelPic) {
		this.hotelPic = hotelPic;
	}
	

	public List<HotelDocument> getHotelDocument() {
		return hotelDocument;
	}

	public void setHotelDocument(List<HotelDocument> hotelDocument) {
		this.hotelDocument = hotelDocument;
	}

	public UsersAuthentication getUser() {
		return user;
	}

	public void setUser(UsersAuthentication user) {
		this.user = user;
	}

	
	
	
}