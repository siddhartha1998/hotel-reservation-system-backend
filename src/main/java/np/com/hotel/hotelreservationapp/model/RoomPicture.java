package np.com.hotel.hotelreservationapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name="tbl_room_picture")
public class RoomPicture {
	
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
	
	
	@ManyToOne
	private Room room;
	
	public RoomPicture() {
		
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


	public Room getRoom() {
		return room;
	}


	public void setRoom(Room room) {
		this.room = room;
	}
	
	
	
	
	
	

}
