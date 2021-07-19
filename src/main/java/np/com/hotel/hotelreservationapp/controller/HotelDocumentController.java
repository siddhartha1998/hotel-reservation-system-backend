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
import np.com.hotel.hotelreservationapp.model.HotelDocument;
import np.com.hotel.hotelreservationapp.model.HotelPicture;
import np.com.hotel.hotelreservationapp.payload.response.MessageResponse;
import np.com.hotel.hotelreservationapp.repository.HotelDocumentRepository;
import np.com.hotel.hotelreservationapp.repository.HotelPictureRepository;
import np.com.hotel.hotelreservationapp.repository.HotelRepository;
import np.com.hotel.hotelreservationapp.services.HotelDocumentService;
import np.com.hotel.hotelreservationapp.services.HotelPictureService;
import np.com.hotel.hotelreservationapp.utils.AppConstants;

@RestController
@RequestMapping("/api/hotelDocument")
public class HotelDocumentController {
	
	@Autowired
	HotelRepository hotelRepo;
	
	@Autowired
	HotelDocumentRepository hotelDocumentRepo;
	
	@Autowired
	HotelDocumentService hotelDocumentService;

	@PostMapping("/addDocumentForHotel/{hotelId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL') ")
	public ResponseEntity<?> addDocumentForHotel(
			@RequestParam(required = true, value = AppConstants.HOTEL_DOCUMENT_FILE_PARAM)
			MultipartFile hotelDocument, @PathVariable Long hotelId)
					throws JsonParseException, JsonMappingException, IOException {

		
		Optional<Hotel> h=hotelRepo.findById(hotelId);
		Hotel hotel =h.get();
		String hUsername=hotel.getHotelUsername();	


		// To generate random alphanumeric characters.
		String randomChars = RandomString.make();

		String fileName = StringUtils.cleanPath(hotelDocument.getOriginalFilename());
		String newFileName = randomChars + "_" + hUsername + "_" + fileName;

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(AppConstants.DOWNLOAD_PATH_FOR_HOTEL_DOCUMENT).path(newFileName).toUriString();

		// make a call to the service,
		hotelDocumentService.storeHotelDocument(hotelDocument,hotel, newFileName, hUsername, fileDownloadUri);

		return ResponseEntity.ok(new MessageResponse("Hotel Document update successfully!"));

		// return ResponseEntity.ok("hello");
	}

	@GetMapping("getHotelDocumentLink/{id}")
	public List<HotelDocument> getHotelDocumentLink(@PathVariable Long id) throws ResourceNotFoundException {
		if(hotelDocumentRepo.findByHotelId(id)!=null) {
		return hotelDocumentRepo.findByHotelId(id);
		}else {
			 throw new ResourceNotFoundException("No  document present");
		}
	}

	@GetMapping("downloadFile/{fileName}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL')")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		Resource resource = hotelDocumentService.loadFileAsResource(fileName);
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

//	
//	@DeleteMapping("deleteHotelPictureById/{id}")
//	public ResponseEntity<?> deleteHotelPictureById(@PathVariable Long id) {
//		Optional<HotelPicture> r = hotelDocumentRepo.findById(id);
//
//		if (r.isPresent()) {
//
//			hotelPictureRepo.deleteById(id);
//			return ResponseEntity.ok(new MessageResponse("Hotel Picture deleted Successfully"));
//
//		} else {
//			return ResponseEntity.ok(new MessageResponse("User with id " + id + " is not found."));
//		}
//
//	}

}
