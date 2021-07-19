package np.com.hotel.hotelreservationapp.controller;

import java.io.IOException;
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
import np.com.hotel.hotelreservationapp.model.Customer;
import np.com.hotel.hotelreservationapp.model.ProfilePicture;
import np.com.hotel.hotelreservationapp.model.UsersAuthentication;
import np.com.hotel.hotelreservationapp.payload.response.MessageResponse;
import np.com.hotel.hotelreservationapp.repository.CustomerRepository;
import np.com.hotel.hotelreservationapp.repository.ProfilePictureRepository;
import np.com.hotel.hotelreservationapp.repository.UsersAuthenticationRepository;
import np.com.hotel.hotelreservationapp.services.ProfilePictureService;
import np.com.hotel.hotelreservationapp.services.UserDetailsImpl;
import np.com.hotel.hotelreservationapp.utils.AppConstants;

@RestController
@RequestMapping("/api/profilePicture")
public class ProfilePictureController {

	@Autowired
	UsersAuthenticationRepository userRepo;

	@Autowired
	ProfilePictureRepository profilePicRepo;

	@Autowired
	CustomerRepository customerRepo;

	@Autowired
	ProfilePictureService profilePictureService;

	@PostMapping("/addProfilePicture/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL') or hasRole('CUSTOMER') ")
	public ResponseEntity<?> addProfilePicture(
			@RequestParam(required = true, value = AppConstants.PROFILE_FILE_PARAM) MultipartFile profile,
			@PathVariable Long id) throws JsonParseException, JsonMappingException, IOException {

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// Long userId=((UserDetailsImpl)principal).getUserId();
		String username = ((UserDetailsImpl) principal).getUsername();

		Optional<Customer> customer = customerRepo.findById(id);
		Customer c = customer.get();
		Long cId = c.getId();

		if (cId == id) {

			// To generate random alphanumeric characters.
			String randomChars = RandomString.make();

			String fileName = StringUtils.cleanPath(profile.getOriginalFilename());
			String newFileName = randomChars + "_" + username + "_" + fileName;

			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path(AppConstants.DOWNLOAD_PATH_FOR_PROFILE).path(newFileName).toUriString();

			// make a call to the service,
			profilePictureService.storeProfilePicture(profile, username, fileDownloadUri, randomChars);

			return ResponseEntity.ok(new MessageResponse("Profile Picture uploaded successfully!"));

		} else {
			return ResponseEntity.ok(new MessageResponse("You are not eligible for this operation"));
		}

		// return ResponseEntity.ok("hello");
	}

	@PostMapping("/addProfilePictureForAdmin/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL') or hasRole('CUSTOMER') ")
	public ResponseEntity<?> addProfilePictureForAdmin(
			@RequestParam(required = true, value = AppConstants.PROFILE_FILE_PARAM) MultipartFile profile,
			@PathVariable Long id) throws JsonParseException, JsonMappingException, IOException {

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long userId = ((UserDetailsImpl) principal).getUserId();
		String username = ((UserDetailsImpl) principal).getUsername();

		Optional<UsersAuthentication> auth = userRepo.findById(userId);
		UsersAuthentication u = auth.get();

		// To generate random alphanumeric characters.
		String randomChars = RandomString.make();

		String fileName = StringUtils.cleanPath(profile.getOriginalFilename());
		String newFileName = randomChars + "_" + username + "_" + fileName;

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(AppConstants.DOWNLOAD_PATH_FOR_PROFILE).path(newFileName).toUriString();

		// make a call to the service,
		profilePictureService.storeAdminProfilePicture(profile, u, newFileName, username, fileDownloadUri);

		return ResponseEntity.ok(new MessageResponse("Profile Picture uploaded successfully"));

		// return ResponseEntity.ok("hello");
	}

	@GetMapping("getProfilePictureLink/{id}")
	public Optional<ProfilePicture> getProfilePictureLink(@PathVariable Long id) {

		return profilePicRepo.findByUserId(id);
	}

	@GetMapping("downloadFile/{fileName}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL') or hasRole('CUSTOMER')")
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

	@PutMapping("/updateProfilePicture/{id}")

	public ResponseEntity<?>updateProfilePicture(@RequestParam(required = true, value = AppConstants.PROFILE_FILE_PARAM) MultipartFile profile,@PathVariable Long id)
			throws JsonParseException, JsonMappingException, IOException{

		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long userId=((UserDetailsImpl)principal).getUserId();
		String username=((UserDetailsImpl)principal).getUsername();

		Optional<UsersAuthentication> user=userRepo.findById(id);
		UsersAuthentication u=user.get();
		ProfilePicture p=u.getProfile();
		Long profileId=p.getId();

		if(id==userId) {

			//To generate random alphanumeric characters.
			String randomChars = RandomString.make();

			String fileName=StringUtils.cleanPath(profile.getOriginalFilename());
			String newFileName=randomChars+"_"+username+"_"+fileName;

			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path(AppConstants.DOWNLOAD_PATH_FOR_PROFILE)
					.path(newFileName).toUriString();

			//make a call to the service,
			profilePictureService.updateProfilePicture(profile,username,profileId,fileDownloadUri, randomChars);

			return ResponseEntity.ok(new MessageResponse("Profile Picture updated"));

		}else {
			return ResponseEntity.ok(new MessageResponse("You are not eligible for this operation"));
		}


	}

	@DeleteMapping("deleteProfilePictureById/{id}")
	public ResponseEntity<?> deleteProfilePictureById(@PathVariable Long id) {
		Optional<ProfilePicture> u = profilePicRepo.findById(id);

		if (u.isPresent()) {

			profilePicRepo.deleteById(id);
			return ResponseEntity.ok(new MessageResponse("Profile Picture deleted Successfully"));

		} else {
			return ResponseEntity.ok(new MessageResponse("User with id " + id + " is not found."));
		}

	}

}
