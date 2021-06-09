package np.com.hotel.hotelreservationapp.payload.request;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AddUserRequest {

	@NotBlank
	@Size(max=50)
	private String fullname;
	
	@NotBlank
	private String username;
	
	
	@NotBlank
	private String address;
	
	@NotBlank
	@Size(max=20)
	private Long phone;
	
	@Email
	private String email;
	
	@NotBlank
	@Size(min=8,max=50)
	private String password;
	
	@NotBlank
	private String gender;
	
	@NotBlank
	private Integer age;
	
	private Integer counter;
	
	private Set<String> role;
	
	
	
	
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
	
	
	public Long getPhone() {
		return phone;
	}
	
	
	public void setPhone(Long phone) {
		this.phone = phone;
	}
	
	
	public String getEmail() {
		return email;
	}
	
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	public String getPassword() {
		return password;
	}
	
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public String getGender() {
		return gender;
	}
	
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	
	public Integer getAge() {
		return age;
	}
	
	
	public void setAge(Integer age) {
		this.age = age;
	}
	
	
	public Integer getCounter() {
		return counter;
	}
	
	
	public void setCounter(Integer counter) {
		this.counter = counter;
	}
	
	
	public Set<String> getRole() {
		return role;
	}
	
	
	public void setRole(Set<String> role) {
		this.role = role;
	}
	
	
}
