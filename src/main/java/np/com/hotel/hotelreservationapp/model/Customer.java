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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name="tbl_customer_details")
public class Customer {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	private String fullname;
	
	@NotBlank
	private String username;

	@NotBlank
	private String address;
	
	@Email
	@NotBlank
	private String email;
	
	@NotBlank
	private String idType;
	
	@NotBlank
	private String idNumber;
	
	@NotBlank
	private Integer age;
	
	@NotBlank
	private String gender;
	
	private Long phone;
	
	

	private boolean active=true;
	
	@OneToOne
	private UsersAuthentication user;

	@JsonIgnore
	@OneToMany(mappedBy = "customer")
	private List<ReservationDetails> reservation=new ArrayList<>();
	public Customer() {
		
		
	}
	public Customer(@NotBlank String fullname, @NotBlank String username, @NotBlank String address,
			@Email @NotBlank String email, @NotBlank String idType, @NotBlank String idNumber, @NotBlank Integer age,
			@NotBlank String gender, Long phone, UsersAuthentication user,
			List<ReservationDetails> reservation) {
		super();
		this.fullname = fullname;
		this.username = username;
		this.address = address;
		this.email = email;
		this.idType = idType;
		this.idNumber = idNumber;
		this.age = age;
		this.gender = gender;
		this.phone = phone;
		this.user = user;
		this.reservation = reservation;
	}
	public Long getId() {
		return id;
	}
	
	
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public String getFullname() {
		return fullname;
	}
	
	
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
	
	public String getUsername() {
		return username;
	}
	
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public String getAddress() {
		return address;
	}
	
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	public String getEmail() {
		return email;
	}
	
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	public String getIdType() {
		return idType;
	}
	
	
	public void setIdType(String idType) {
		this.idType = idType;
	}
	
	
	public String getIdNumber() {
		return idNumber;
	}
	
	
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	
	
	public Integer getAge() {
		return age;
	}
	
	
	public void setAge(Integer age) {
		this.age = age;
	}
	
	
	public String getGender() {
		return gender;
	}
	
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	
	public Long getPhone() {
		return phone;
	}
	
	
	public void setPhone(Long phone) {
		this.phone = phone;
	}
	
	
	
	public UsersAuthentication getUser() {
		return user;
	}
	
	
	public void setUser(UsersAuthentication user) {
		this.user = user;
	}
	
	
	public List<ReservationDetails> getReservation() {
		return reservation;
	}
	
	
	public void setReservation(List<ReservationDetails> reservation) {
		this.reservation = reservation;
	}
	
	
	public boolean isActive() {
		return active;
	}
	
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
		
}
