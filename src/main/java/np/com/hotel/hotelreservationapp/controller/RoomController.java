package np.com.hotel.hotelreservationapp.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.bytebuddy.utility.RandomString;
import np.com.hotel.hotelreservationapp.model.Hotel;
import np.com.hotel.hotelreservationapp.model.Room;
import np.com.hotel.hotelreservationapp.payload.response.MessageResponse;
import np.com.hotel.hotelreservationapp.repository.HotelRepository;
import np.com.hotel.hotelreservationapp.repository.RoomRepository;
import np.com.hotel.hotelreservationapp.repository.UsersAuthenticationRepository;
import np.com.hotel.hotelreservationapp.services.RoomService;
import np.com.hotel.hotelreservationapp.services.UserDetailsImpl;
import np.com.hotel.hotelreservationapp.utils.AppConstants;

@RestController
@RequestMapping("/api/room")
public class RoomController {

	@Autowired
	UsersAuthenticationRepository authRepo;

	@Autowired
	RoomRepository roomRepo;

	@Autowired
	RoomService roomService;

	@Autowired
	HotelRepository hotelRepository;


	ObjectMapper objectMapper=new ObjectMapper();

	


	@PostMapping("/addRoomDetail")
	@PreAuthorize("hasRole('ROLE_HOTEL')")
	public ResponseEntity<?>addRoomDetail(@RequestParam MultipartFile file,@RequestParam String roomJson)
			throws JsonParseException, JsonMappingException, IOException{

		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String hotelUsername=((UserDetailsImpl)principal).getUsername();
		

		Optional<Hotel>h=hotelRepository.findByHotelUsername(hotelUsername);
		Hotel ho=h.get();
		Long hotelId=ho.getId();
		
		
		
		/*
		  Optional<Room>roomDetail=roomRepo.findById(roomId);
		   
		    Room r=roomDetail.get();
		    Long roomNumber=r.getRoomNumber();
		   
*/

		//To generate random alphanumeric characters.
		String randomChars = RandomString.make();

		//This module will get executed if both file and content are provided by an user.
		String fileName=StringUtils.cleanPath(file.getOriginalFilename());
		String newFileName= randomChars+"_"+hotelUsername+"_"+fileName;

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path(AppConstants.DOWNLOAD_PATH_FOR_ROOM_PICTURE)
				.path(newFileName).toUriString();

		Room roomDetail = objectMapper.readValue(roomJson, Room.class);

		Long roomNumber=roomDetail.getRoomNumber();
		Long[] savedRoomNumbers=roomRepo.getDetailByhotelId(hotelId);
		boolean roomNumberExists=false;
		for(int i =0; i<savedRoomNumbers.length;i++) {
			
			if(roomNumber.equals(savedRoomNumbers[i])) {
			roomNumberExists=true;
			break;
			}
		}
		if(roomNumberExists==true) {
			return ResponseEntity.ok("Room number Already exists.");
		}
		else {
		roomService.storeRoom(file,roomDetail,newFileName,fileDownloadUri,hotelUsername,ho);

		return ResponseEntity.ok("Room detail added Successfully");
		
		}
		

	}
	@GetMapping("/getRoomDetail")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL')")
	public List<Room>getRoomDetail(){
		return roomRepo.findAll();
	} 


	@GetMapping("/findHotelRoomByHotel")
	@PreAuthorize("hasRole('HOTEL')")
	public List<Room>findHotelRoomByHotel(){

		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String	hotelUserName=((UserDetailsImpl)principal).getUsername();

		return roomRepo.findByHoteUserName(hotelUserName);
	}

	@GetMapping("getRoomDetailById/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL')")
	public Optional<Room>getRoomById(@PathVariable Long id){

		return 	roomRepo.findById(id);
	}

	@GetMapping("getActiveRoom")
	public List<Room>getActiveRoom(){
	return roomRepo.getAvailableRoom();}
	
	
	@GetMapping("getInactiveRoom")
	public List<Room>getReservationRoom(){
		return roomRepo.getUnavailableRoom();
	}
	
	@GetMapping("/getNonReservedRoomOfMyHotel/{id}")
	public List<Room>getNonReservedRoomOfMyHotel(@PathVariable Long id){
		return roomRepo.getAvailableRoomOfMyHotel(id);
	}
	
	@PutMapping("/updateRoomDetail/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL')")
	public ResponseEntity<?>updateRoomDetail(@PathVariable Long id,@RequestBody Room room){
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String hotelUsername=((UserDetailsImpl)principal).getUsername();
		Optional<Room>a=roomRepo.findById(id);
		if(a.isPresent()) {
			Room update=a.get();
			String chotelUsername=update.getHotelUsername();
			if(hotelUsername.equals(chotelUsername)) {

				
					roomRepo.findById(id).map(updateDetail->{
						
					if(room.getRoomNumber()!=null) {
					updateDetail.setRoomNumber(room.getRoomNumber());
					}
					if(room.getRoomType()!=null) {
					updateDetail.setRoomType(room.getRoomType());
					}
					if(room.getDescription()!=null) {
					updateDetail.setDescription(room.getDescription());
					}
					return roomRepo.save(updateDetail);
				});
				
				return ResponseEntity.ok(new MessageResponse("Room Updated Successfully"));
			}
			else {
				return ResponseEntity.ok("No match found");
			}
		}
		else {
			return ResponseEntity.ok(new MessageResponse("This Room can not be updated"));
		}

	}	


	@PutMapping("/deleteRoomDetail/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL')")
	public ResponseEntity<?>deleteRoomDetails(@PathVariable Long id){
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String hotelUsername=((UserDetailsImpl)principal).getUsername();
		Optional<Room>a=roomRepo.findById(id);
		if(a.isPresent()) {
			Room room1= a.get();
			String cHotelUsername=room1.getHotelUsername();
			if(hotelUsername.equals(cHotelUsername)) {
				roomRepo.deleteById(id);
		
				return ResponseEntity.ok(new MessageResponse("Room Details deleted Successfully"));
			}else {
				return ResponseEntity.ok(new MessageResponse("You cannot delete other users data."));
			}
		}else {
			return ResponseEntity.ok(new MessageResponse("Room with id "+id+" is not found."));
		}
	}
}
