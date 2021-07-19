package np.com.hotel.hotelreservationapp.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import np.com.hotel.hotelreservationapp.repository.ProfilePictureRepository;
import np.com.hotel.hotelreservationapp.repository.RoomPictureRepository;
import np.com.hotel.hotelreservationapp.services.ProfilePictureService;
import np.com.hotel.hotelreservationapp.services.RoomPictureService;
import np.com.hotel.hotelreservationapp.utils.AppConstants;

@RestController
@RequestMapping("/api/downloadPicture")
public class DownloadPictureController {
	
	@Autowired
	ProfilePictureRepository profilePictureRepo;
	
	@Autowired
	ProfilePictureService profilePictureService;
	
	@Autowired
	RoomPictureRepository roomPictureRepo;
	
	@Autowired
	RoomPictureService roomPictureService;
	
	@GetMapping("downloadFile/{fileName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
	
		Resource resource = profilePictureService.loadFileAsResource(fileName);
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
	
	

	@GetMapping("downloadRoomFile/{fileName}")
	public ResponseEntity<Resource> downloadRoomFile(@PathVariable String fileName, HttpServletRequest request) {
		

		
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
	
	

}
