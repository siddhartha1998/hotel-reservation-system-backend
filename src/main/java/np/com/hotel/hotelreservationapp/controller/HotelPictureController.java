package np.com.hotel.hotelreservationapp.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.bytebuddy.utility.RandomString;
import np.com.hotel.hotelreservationapp.exception.ResourceNotFoundException;
import np.com.hotel.hotelreservationapp.model.Hotel;
import np.com.hotel.hotelreservationapp.model.HotelPicture;
import np.com.hotel.hotelreservationapp.model.RoomPicture;
import np.com.hotel.hotelreservationapp.payload.response.MessageResponse;
import np.com.hotel.hotelreservationapp.repository.HotelPictureRepository;
import np.com.hotel.hotelreservationapp.repository.HotelRepository;
import np.com.hotel.hotelreservationapp.services.HotelPictureService;
import np.com.hotel.hotelreservationapp.services.UserDetailsImpl;
import np.com.hotel.hotelreservationapp.utils.AppConstants;

@RestController
@RequestMapping("/api/hotelPicture")
public class HotelPictureController {
	
	@Autowired
	HotelRepository hotelRepo;
	
	@Autowired
	HotelPictureRepository hotelPictureRepo;
	
	@Autowired
	HotelPictureService hotelPictureService;


	@PostMapping("/addPictureForHotel/{hotelId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL') ")
	public ResponseEntity<?> addPictureForHotel(
			@RequestParam(required = true, value = AppConstants.HOTEL_PICTURE_FILE_PARAM) MultipartFile hotelPic,
			@PathVariable Long hotelId)
					throws JsonParseException, JsonMappingException, IOException {

	//	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		Long userId = ((UserDetailsImpl) principal).getUserId();
	//	String username = ((UserDetailsImpl) principal).getUsername();
		
		Optional<Hotel> h=hotelRepo.findById(hotelId);
		Hotel hotel =h.get();
		String hUsername=hotel.getHotelUsername();	
		
//		Optional<Room> r=roomRepo.findById(roomId);
//		Room room =r.get();

//		Optional<UsersAuthentication> auth = userRepo.findById(userId);
//		UsersAuthentication u = auth.get();

		// To generate random alphanumeric characters.
		String randomChars = RandomString.make();

		String fileName = StringUtils.cleanPath(hotelPic.getOriginalFilename());
		String newFileName = randomChars + "_" + hUsername + "_" + fileName;

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(AppConstants.DOWNLOAD_PATH_FOR_HOTEL_PICTURE).path(newFileName).toUriString();

		// make a call to the service,
		hotelPictureService.storeHotelPicture(hotelPic,hotel, newFileName, hUsername, fileDownloadUri);

		return ResponseEntity.ok(new MessageResponse("Room Photo update successfully!"));

		// return ResponseEntity.ok("hello");
	}

	@GetMapping("getHotelPictureLink/{id}")
	public List<HotelPicture> getHotelPictureLink(@PathVariable Long id) throws ResourceNotFoundException {
		if(hotelPictureRepo.findByHotelId(id)!=null) {
		return hotelPictureRepo.findByHotelId(id);
		}else {
			 throw new ResourceNotFoundException("No  image present");
		}
	}

	@GetMapping("downloadFile/{fileName}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL') or hasRole('CUSTOMER')")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		Resource resource = hotelPictureService.loadFileAsResource(fileName);
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

	
	@DeleteMapping("deleteHotelPictureById/{id}")
	public ResponseEntity<?> deleteHotelPictureById(@PathVariable Long id) {
		Optional<HotelPicture> r = hotelPictureRepo.findById(id);

		if (r.isPresent()) {

			hotelPictureRepo.deleteById(id);
			return ResponseEntity.ok(new MessageResponse("Hotel Picture deleted Successfully"));

		} else {
			return ResponseEntity.ok(new MessageResponse("User with id " + id + " is not found."));
		}

	}
	
}
