package np.com.hotel.hotelreservationapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Entity
@Table(name="tbl_profile_picture")
public class ProfilePicture {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	private String fileName;
	@NotBlank
	private String fileType;
	@NotBlank
	private String uploadDir;
	@NotBlank
	private String uploadedBy;
	@NotBlank
	private String uploadedTime;
	@JsonIgnore
	@OneToOne
	@JsonIgnoreProperties("password")
	private UsersAuthentication user;
	
	
	public ProfilePicture() {
		
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public String getFileType() {
		return fileType;
	}


	public void setFileType(String fileType) {
		this.fileType = fileType;
	}


	public String getUploadDir() {
		return uploadDir;
	}


	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}


	public String getUploadedBy() {
		return uploadedBy;
	}


	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}


	public String getUploadedTime() {
		return uploadedTime;
	}


	public void setUploadedTime(String uploadedTime) {
		this.uploadedTime = uploadedTime;
	}


	public UsersAuthentication getUser() {
		return user;
	}


	public void setUser(UsersAuthentication user) {
		this.user = user;
	}
	
	
	
	
}
