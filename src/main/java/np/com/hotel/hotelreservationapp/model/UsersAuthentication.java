package np.com.hotel.hotelreservationapp.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity
@Table(name="tbl_users_authentication")
public class UsersAuthentication {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long userId;
	
	@NotBlank
	String username;
	
	@NotBlank
	@Size(min=6,max=20)
	String password;
	
	@NotBlank
	@Email
	String email;
	
	private boolean isEnabled=false;
	
	private boolean active=true;
	
	@JsonIgnore
	@OneToOne(mappedBy="user")
	private Customer customer;
	
	@JsonIgnore
	@OneToOne(mappedBy="user")
	private Hotel hotel;
	
	@ManyToMany(fetch = FetchType.LAZY)

	 @JoinTable( name = "user_roles",
	 joinColumns=@JoinColumn(name="user_id"),
     inverseJoinColumns=@JoinColumn(name ="role_id"))
	 private Set<Role> roles = new HashSet<>();
	
	@JsonIgnore
	@OneToOne(mappedBy = "user",cascade=CascadeType.ALL)
	private ProfilePicture profile;
	

	public UsersAuthentication() {
		super();
		
	}



	public UsersAuthentication( String username,
			@NotBlank @Size(min = 6, max = 20) String password, @NotBlank @Email String email, boolean isEnabled,
			boolean active, Customer customer, Hotel hotel, Set<Role> roles) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.isEnabled = isEnabled;
		this.active = active;
		this.customer = customer;
		this.hotel = hotel;
		this.roles = roles;
	}



	public Long getUserId() {
		return userId;
	}



	public void setUserId(Long userId) {
		this.userId = userId;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public boolean isEnabled() {
		return isEnabled;
	}



	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}



	public boolean isActive() {
		return active;
	}



	public void setActive(boolean active) {
		this.active = active;
	}



	public Customer getCustomer() {
		return customer;
	}



	public void setCustomer(Customer customer) {
		this.customer = customer;
	}



	public Hotel getHotel() {
		return hotel;
	}



	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}



	public Set<Role> getRoles() {
		return roles;
	}



	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	



	public ProfilePicture getProfile() {
		return profile;
	}



	public void setProfile(ProfilePicture profile) {
		this.profile = profile;
	}



	@Override
	public String toString() {
		return "UsersAuthentication [userId=" + userId + ", username=" + username + ", password=" + password
				+ ", email=" + email + ", isEnabled=" + isEnabled + ", active=" + active + ", customer=" + customer
				+ ", hotel=" + hotel + ", roles=" + roles + "]";
	}



	


	
	
	
	

}
