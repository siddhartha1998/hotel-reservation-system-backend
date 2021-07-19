package np.com.hotel.hotelreservationapp.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import np.com.hotel.hotelreservationapp.exception.FileStorageException;
import np.com.hotel.hotelreservationapp.model.Hotel;
import np.com.hotel.hotelreservationapp.model.HotelDocument;

import np.com.hotel.hotelreservationapp.payload.response.MessageResponse;
import np.com.hotel.hotelreservationapp.repository.HotelDocumentRepository;
import np.com.hotel.hotelreservationapp.utils.AppConstants;

@Service
public class HotelDocumentService {

	@Value("${hotelDocument.storage.location}")
	public String uploadDir;
	
	@Autowired
	HotelDocumentRepository hotelDocumentRepo;

	private Path fileStoragePath;
	public String fileStorageLocation;
	public HotelDocumentService(@Value("${hotelDocument.storage.location:temp}") String fileStorageLocation) {

	 this.fileStorageLocation = fileStorageLocation;
	fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();

	 try {
	Files.createDirectories(fileStoragePath);
	} catch (IOException e) {
	throw new RuntimeException("Issue in creating file directory");
	}
	}
	
	
	public ResponseEntity<?> storeHotelDocument(MultipartFile hotelDocument,Hotel hotel, String newFileName, String username, String fileDownloadUri){
		if(!(hotelDocument.getOriginalFilename().endsWith(AppConstants.PDF_FILE_FORMAT)||
				hotelDocument.getOriginalFilename().endsWith(AppConstants.CAPITAL_PDF_FILE_FORMAT)
				))
		throw new FileStorageException(AppConstants.INVALID_FILE_FORMAT);
		// Normalize file name
	
		Path filePath = Paths.get(fileStoragePath + "\\" + newFileName);
		 //getting local date and time
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();

		 try {
		Files.copy(hotelDocument.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
		throw new RuntimeException("Issue in storing the file", e);
		}

		 try {
		// Check if the file's name contains invalid characters
		if (newFileName.contains("..")) {
		throw new FileStorageException("Sorry! Filename contains invalid path sequence " + newFileName);
		}
		}
		catch (Exception e) {
			throw new RuntimeException("Issue in storing the file", e);
			}

		HotelDocument doc=new HotelDocument();
		doc.setFileName(newFileName);
		doc.setUploadedBy(username);
		doc.setUploadedTime(dtf.format(now));
		doc.setHotel(hotel);
		doc.setFileType(hotelDocument.getContentType());
		doc.setUploadDir(fileDownloadUri);
		
		hotelDocumentRepo.save(doc);
	
		
		return ResponseEntity.ok(new MessageResponse("Profile Picture Add Successfully."));

		}



     public Resource loadFileAsResource(String fileName) {
	    	try {
				//Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
				Path filePath= this.fileStoragePath.resolve(fileName).normalize();

				Resource resource = new UrlResource(filePath.toUri());

				if (resource.exists()) {
					return resource;
				} else {
					throw new FileStorageException(AppConstants.FILE_NOT_FOUND + fileName);
				}
			} catch (MalformedURLException ex) {
				throw new FileStorageException(AppConstants.FILE_NOT_FOUND + fileName, ex);
			}
	    	
		}  
    
}
