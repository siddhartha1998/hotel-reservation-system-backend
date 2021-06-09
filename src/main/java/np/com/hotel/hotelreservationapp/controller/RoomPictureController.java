package np.com.hotel.hotelreservationapp.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import net.bytebuddy.utility.RandomString;
import np.com.hotel.hotelreservationapp.exception.ResourceNotFoundException;
import np.com.hotel.hotelreservationapp.model.Room;
import np.com.hotel.hotelreservationapp.model.RoomPicture;
import np.com.hotel.hotelreservationapp.payload.response.MessageResponse;
import np.com.hotel.hotelreservationapp.repository.CustomerRepository;
import np.com.hotel.hotelreservationapp.repository.HotelRepository;
import np.com.hotel.hotelreservationapp.repository.RoomPictureRepository;
import np.com.hotel.hotelreservationapp.repository.RoomRepository;
import np.com.hotel.hotelreservationapp.services.RoomPictureService;
import np.com.hotel.hotelreservationapp.services.UserDetailsImpl;
import np.com.hotel.hotelreservationapp.utils.AppConstants;
@RestController
@RequestMapping("/api/roomPicture")
public class RoomPictureController {

	
	
	@Autowired
	RoomRepository roomRepo;
	
	@Autowired
	HotelRepository hotelRepo;

	@Autowired
	RoomPictureRepository roomPicRepo;

	@Autowired
	CustomerRepository customerRepo;

	@Autowired
	RoomPictureService roomPictureService;


	@PostMapping("/addPictureForRoom/{hotelId}/{roomId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL') or hasRole('CUSTOMER') ")
	public ResponseEntity<?> addPictureForRoom(
			@RequestParam(required = true, value = AppConstants.ROOM_PICTURE_FILE_PARAM) MultipartFile roomPic,
			@PathVariable Long hotelId, @PathVariable Long roomId)
					throws JsonParseException, JsonMappingException, IOException {

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		Long userId = ((UserDetailsImpl) principal).getUserId();
		String username = ((UserDetailsImpl) principal).getUsername();
		
//		Optional<Hotel> h=hotelRepo.findById(hotelId);
//		Hotel hotel =h.get();
//		String hUsername=hotel.getHotelUsername();	
		
		Optional<Room> r=roomRepo.findById(roomId);
		Room room =r.get();

//		Optional<UsersAuthentication> auth = userRepo.findById(userId);
//		UsersAuthentication u = auth.get();

		// To generate random alphanumeric characters.
		String randomChars = RandomString.make();

		String fileName = StringUtils.cleanPath(roomPic.getOriginalFilename());
		String newFileName = randomChars + "_" + username + "_" + fileName;

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(AppConstants.DOWNLOAD_PATH_FOR_ROOM_PICTURE).path(newFileName).toUriString();

		// make a call to the service,
		roomPictureService.storeRoomPicture(roomPic,room, newFileName, username, fileDownloadUri);

		return ResponseEntity.ok(new MessageResponse("Room Photo update successfully!"));

		// return ResponseEntity.ok("hello");
	}

	@GetMapping("getRoomPictureLink/{id}")
	public List<RoomPicture> getRoomPictureLink(@PathVariable Long id) throws ResourceNotFoundException {
		if(roomPicRepo.findByRoomId(id)!=null) {
		return roomPicRepo.findByRoomId(id);
		}else {
			 throw new ResourceNotFoundException("No  image present");
		}
	}
	

	@GetMapping("downloadFile/{fileName}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL') or hasRole('CUSTOMER')")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		Resource resource = roomPictureService.loadFileAsResource(fileName);
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (contentType == null) {
			contentType = AppConstants.DEFAULT_CONTENT_TYPE;
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION,
						String.format(AppConstants.FILE_DOWNLOAD_HTTP_HEADER, resource.getFilename()))
				.body(resource);
	}

//	@PutMapping("/updateRoomPicture/{id}")
//
//	public ResponseEntity<?>updateProfilePicture(@RequestParam(required = true, value = AppConstants.PROFILE_FILE_PARAM) MultipartFile profile,@PathVariable Long id)
//			throws JsonParseException, JsonMappingException, IOException{
//
//		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		Long userId=((UserDetailsImpl)principal).getUserId();
//		String username=((UserDetailsImpl)principal).getUsername();
//
//		Optional<UsersAuthentication> user=userRepo.findById(id);
//		UsersAuthentication u=user.get();
////		RoomPicture p=u.g
////		Long profileId=p.getId();
//
//		if(id==userId) {
//
//			//To generate random alphanumeric characters.
//			String randomChars = RandomString.make();
//
//			String fileName=StringUtils.cleanPath(profile.getOriginalFilename());
//			String newFileName=randomChars+"_"+username+"_"+fileName;
//
//			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path(AppConstants.DOWNLOAD_PATH_FOR_PROFILE)
//					.path(newFileName).toUriString();
//
//			//make a call to the service,
//			roomPictureService.updateProfilePicture(profile,username,profileId,fileDownloadUri, randomChars);
//
//			return ResponseEntity.ok(new MessageResponse("Profile Picture updated"));
//
//		}else {
//			return ResponseEntity.ok(new MessageResponse("You are not eligible for this operation"));
//		}
//
//
//	}
//
	@DeleteMapping("deleteRoomPictureById/{id}")
	public ResponseEntity<?> deleteRoomPictureById(@PathVariable Long id) {
		Optional<RoomPicture> r = roomPicRepo.findById(id);

		if (r.isPresent()) {

			roomPicRepo.deleteById(id);
			return ResponseEntity.ok(new MessageResponse("Room Picture deleted Successfully"));

		} else {
			return ResponseEntity.ok(new MessageResponse("User with id " + id + " is not found."));
		}

	}

	
}
