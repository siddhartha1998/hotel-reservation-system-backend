package np.com.hotel.hotelreservationapp.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import np.com.hotel.hotelreservationapp.exception.FileStorageException;
import np.com.hotel.hotelreservationapp.model.Hotel;
import np.com.hotel.hotelreservationapp.model.Room;
import np.com.hotel.hotelreservationapp.repository.RoomRepository;
import np.com.hotel.hotelreservationapp.utils.AppConstants;



@Service
public class RoomService {
	
	@Value("${roompicture.storage.location}")
	private String uploadDir;
	
	@Autowired
	RoomRepository roomRepository;
	
	private Path fileStoragePath;
	public String fileStorageLocation;
	
		public RoomService(@Value("${roompicture.storage.location:temp}") String fileStorageLocation) {

	 this.fileStorageLocation = fileStorageLocation;
	fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();

	 try {
	Files.createDirectories(fileStoragePath);
	} catch (IOException e) {
	throw new RuntimeException("Issue in creating file directory");
	}
	}
	
	public ResponseEntity<?> storeRoom(MultipartFile file,Room roomDetail,String newFileName,String fileDownloadUri,String hotelUsername,Hotel ho) {
	
		if(!(file.getOriginalFilename().endsWith(AppConstants.PNG_FILE_FORMAT) ||
     file.getOriginalFilename().endsWith(AppConstants.JPEG_FILE_FORMAT)||
     file.getOriginalFilename().endsWith(AppConstants.JPG_FILE_FORMAT)||
     file.getOriginalFilename().endsWith(AppConstants.CAPITAL_PNG_FILE_FORMAT)||
     file.getOriginalFilename().endsWith(AppConstants.CAPITAL_JPEG_FILE_FORMAT)||
     file.getOriginalFilename().endsWith(AppConstants.CAPITAL_JPG_FILE_FORMAT)))
	throw new FileStorageException(AppConstants.INVALID_FILE_FORMAT);
	// Normalize file name

	Path filePath = Paths.get(fileStoragePath + "\\" + newFileName);

	 try {
	Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
	} catch (IOException e) {
	throw new RuntimeException("Issue in storing the file", e);
	}

	 try {
	// Check if the file's name contains invalid characters
	if (newFileName.contains("..")) {
	throw new FileStorageException("Sorry! Filename contains invalid path sequence " + newFileName);
	}


	//Room room=new Room();
//	roomDetail.setRoomPhoto(fileDownloadUri);
	
	roomDetail.setHotelUsername(hotelUsername);
	roomDetail.setHotel(ho);
		
    roomRepository.save(roomDetail);
    return ResponseEntity.ok("Profile picture uploaded");
	
	} catch (Exception ex) {
	throw new FileStorageException("Could not store file " + newFileName + ". Please try again!", ex);
	}
	}
	
	

}
