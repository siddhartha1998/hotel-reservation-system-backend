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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import np.com.hotel.hotelreservationapp.exception.FileStorageException;
import np.com.hotel.hotelreservationapp.exception.ResourceNotFoundException;
import np.com.hotel.hotelreservationapp.model.ProfilePicture;
import np.com.hotel.hotelreservationapp.model.UsersAuthentication;
import np.com.hotel.hotelreservationapp.payload.response.MessageResponse;
import np.com.hotel.hotelreservationapp.repository.CustomerRepository;
import np.com.hotel.hotelreservationapp.repository.ProfilePictureRepository;
import np.com.hotel.hotelreservationapp.utils.AppConstants;

@Service
public class ProfilePictureService {
	@Value("${file.storage.location}")
	public String uploadDir;
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	ProfilePictureRepository profilePic;

	private Path fileStoragePath;
	public String fileStorageLocation;
	public ProfilePictureService(@Value("${file.storage.location:temp}") String fileStorageLocation) {

	 this.fileStorageLocation = fileStorageLocation;
	fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();

	 try {
	Files.createDirectories(fileStoragePath);
	} catch (IOException e) {
	throw new RuntimeException("Issue in creating file directory");
	}
	}
	
	public ResponseEntity<?> storeProfilePicture(MultipartFile profile,String username,String fileDownloadUri,String randomChars) {
	if(!(profile.getOriginalFilename().endsWith(AppConstants.PNG_FILE_FORMAT) ||
     profile.getOriginalFilename().endsWith(AppConstants.JPEG_FILE_FORMAT)||
     profile.getOriginalFilename().endsWith(AppConstants.JPG_FILE_FORMAT)||
     profile.getOriginalFilename().endsWith(AppConstants.CAPITAL_PNG_FILE_FORMAT)||
     profile.getOriginalFilename().endsWith(AppConstants.CAPITAL_JPEG_FILE_FORMAT)||
     profile.getOriginalFilename().endsWith(AppConstants.CAPITAL_JPG_FILE_FORMAT)))
	throw new FileStorageException(AppConstants.INVALID_FILE_FORMAT);
	// Normalize file name
	String fileName = StringUtils.cleanPath(profile.getOriginalFilename());
	String newFileName= randomChars+"_"+username+"_"+fileName;
	Path filePath = Paths.get(fileStoragePath + "\\" + newFileName);

	 try {
	Files.copy(profile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
	} catch (IOException e) {
	throw new RuntimeException("Issue in storing the file", e);
	}

	 try {
	// Check if the file's name contains invalid characters
	if (newFileName.contains("..")) {
	throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
	}

	 //getting local date and time
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();

	
	 customerRepository.findByUsername(username).map(profilePic->{
//        profilePic.setProfileDir(fileDownloadUri);
        return customerRepository.save(profilePic);
    });
    
    return ResponseEntity.ok(new MessageResponse("Profile picture uploaded"));
	
	} catch (Exception ex) {
	throw new FileStorageException("Could not store file " + newFileName + ". Please try again!", ex);
	}
	}

	
	public ResponseEntity<?> storeAdminProfilePicture(MultipartFile profile,UsersAuthentication userAuthentication, String newFileName, String username, String fileDownloadUri){
		if(!(profile.getOriginalFilename().endsWith(AppConstants.PNG_FILE_FORMAT) ||
	     profile.getOriginalFilename().endsWith(AppConstants.JPEG_FILE_FORMAT)||
	     profile.getOriginalFilename().endsWith(AppConstants.JPG_FILE_FORMAT)||
	     profile.getOriginalFilename().endsWith(AppConstants.CAPITAL_PNG_FILE_FORMAT)||
	     profile.getOriginalFilename().endsWith(AppConstants.CAPITAL_JPEG_FILE_FORMAT)||
	     profile.getOriginalFilename().endsWith(AppConstants.CAPITAL_JPG_FILE_FORMAT)))
		throw new FileStorageException(AppConstants.INVALID_FILE_FORMAT);
		// Normalize file name
	
		Path filePath = Paths.get(fileStoragePath + "\\" + newFileName);
		 //getting local date and time
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();

		 try {
		Files.copy(profile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
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

		ProfilePicture pic=new ProfilePicture();
		pic.setFileName(newFileName);
		pic.setUploadedBy(username);
		pic.setUploadedTime(dtf.format(now));
		pic.setUser(userAuthentication);
		pic.setFileType(profile.getContentType());
		pic.setUploadDir(fileDownloadUri);
		
		profilePic.save(pic);
		
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
     
     
     public ResponseEntity<?> updateProfilePicture(MultipartFile profile,String username,Long profileId,String fileDownloadUri,String randomChars) {

    	 if(!(profile.getOriginalFilename().endsWith(AppConstants.PNG_FILE_FORMAT) || profile.getOriginalFilename().endsWith(AppConstants.JPEG_FILE_FORMAT)||profile.getOriginalFilename().endsWith(AppConstants.JPG_FILE_FORMAT)))
    	 throw new FileStorageException(AppConstants.INVALID_FILE_FORMAT);
    	 // Normalize file name
    	 String fileName = StringUtils.cleanPath(profile.getOriginalFilename());
    	 String newFileName= randomChars+"_"+username+"_"+fileName;
    	 Path filePath = Paths.get(fileStoragePath + "\\" + newFileName);

    	  try {
    	 Files.copy(profile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    	 } catch (IOException e) {
    	 throw new RuntimeException("Issue in storing the file", e);
    	 }

    	  try {
    	 // Check if the file's name contains invalid characters
    	 if (newFileName.contains("..")) {
    	 throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
    	 }

    	  //getting local date and time
    	 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    	 LocalDateTime now = LocalDateTime.now();

    	 profilePic.findById(profileId).map(updateProfile->{
    		 updateProfile.setFileName(newFileName);
    		 updateProfile.setUploadedBy(username);
    		 updateProfile.setUploadedTime(dtf.format(now));
    		 updateProfile.setFileType(profile.getContentType());
    		 updateProfile.setUploadDir(fileDownloadUri);
    		 
    		 return profilePic.save(updateProfile);
    	 }).orElseThrow(()-> new ResourceNotFoundException("Profile "+profileId+ " not found"));
    	 

    	  return ResponseEntity.ok(new MessageResponse("Profile picture updated"));
    	 } catch (Exception ex) {
    	 throw new FileStorageException("Could not store file " + newFileName + ". Please try again!", ex);
    	 }
    	 }
}
