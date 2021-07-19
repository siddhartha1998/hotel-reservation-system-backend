package np.com.hotel.hotelreservationapp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import np.com.hotel.hotelreservationapp.exception.ResourceNotFoundException;
import np.com.hotel.hotelreservationapp.model.Customer;
import np.com.hotel.hotelreservationapp.model.Hotel;
import np.com.hotel.hotelreservationapp.model.ReservationDetails;
import np.com.hotel.hotelreservationapp.model.Room;
import np.com.hotel.hotelreservationapp.model.UsersAuthentication;
import np.com.hotel.hotelreservationapp.payload.response.MessageResponse;
import np.com.hotel.hotelreservationapp.repository.CustomerRepository;
import np.com.hotel.hotelreservationapp.repository.HotelRepository;
import np.com.hotel.hotelreservationapp.repository.ReservationRepository;
import np.com.hotel.hotelreservationapp.repository.RoomRepository;
import np.com.hotel.hotelreservationapp.repository.UsersAuthenticationRepository;
import np.com.hotel.hotelreservationapp.services.UserDetailsImpl;

@RestController
@RequestMapping("/api/hotel")
public class HotelController {
	
	@Autowired
	UsersAuthenticationRepository authRepo;
	
	@Autowired
	HotelRepository hotelRepo;
	
	@Autowired
	RoomRepository roomRepo;
	
	@Autowired
	ReservationRepository reservationRepo;
	
	@Autowired
	CustomerRepository customerRepo;

	@PostMapping("/addHotelDetail")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HOTEL')")
	public ResponseEntity<?>addHotelDetail(@RequestBody Hotel hotel){
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String hotelUsername=((UserDetailsImpl)principal).getUsername();
		Long id=((UserDetailsImpl)principal).getUserId();
		
		Optional<UsersAuthentication>obj=authRepo.findById(id);
	    UsersAuthentication hid=obj.get();
		
		//String email=((UserDetailsImpl)principal).getEmail();
		if(hotelRepo.existsByHotelUsername(hotelUsername)) {
			return ResponseEntity.ok("this hotel already exist.");
		}else {
		Hotel add=new Hotel(hotel.getHotelName(),
							hotelUsername,
							hotel.getHotelOwner(),
							hotel.getCity(),
							hotel.getHotelAddress(),
							hotel.getPhone(),
							hotel.getPanNumber(),
							hotel.getStatus(),
							hotel.getDescription(),
							hotel.isActive(),
							hotel.getLatitude(),
							hotel.getLongitude(),
							hotel.getRoom(),
							hotel.getReservation(),
							hotel.getTemporaryReservation(),
							hotel.getUser());
		add.setUser(hid);
		hotelRepo.save(add);
		
		
		return ResponseEntity.ok("Hotel details saved successfully.");
		}
	}
	
	  @GetMapping("/getAllHotel")
	   @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HOTEL')")
	   public List<Hotel>getAllHotel(){
		   return hotelRepo.findAllHotel();
	}
	   
	
	   @GetMapping("/getHotelDetail")
	   @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HOTEL') or hasRole('ROLE_CUSTOMER')")
	   public List<Hotel>getHotelDetail(){
		   return hotelRepo.findAll();
	}
	   
	   @GetMapping("/getInactiveHotelDetail")
	   @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HOTEL')")
	   public List<Hotel>getInactiveHotelDetail(){
		   return hotelRepo.findInactiveHotel();
	}
	   
		
		@GetMapping("/getHotelById/{id}")
		public Optional<Hotel>getHotelById(@PathVariable Long id){
			return hotelRepo.findById(id);
		
	}
		
		@GetMapping("/getMyHotelById/{id}")
		@PreAuthorize("hasRole('ROLE_HOTEL')")
		public Optional<Hotel>getMyHotelById(@PathVariable Long id){
			return hotelRepo.findByUserId(id);
		
	}
		
		   @GetMapping("/getRoomOfMyHotel/{id}")
		   @PreAuthorize("hasRole('ROLE_HOTEL')")
		   public List<Room>getRoomOfMyHotel(@PathVariable Long id){
			   return roomRepo.findRoomOfMyHotel(id);
		}	
		
		   @GetMapping("/getInactiveRoomOfMyHotel/{id}")
		   @PreAuthorize("hasRole('ROLE_HOTEL')")
		   public List<Room>getInactiveRoomOfMyHotel(@PathVariable Long id){
			   return roomRepo.findInactiveRoomOfMyHotel(id);
		}	
		   
		 @GetMapping("/getInactiveHotelById/{id}")
		   @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HOTEL')")
		   public Optional<Hotel>getInactiveHotelById(@PathVariable Long id){
			   return hotelRepo.findInactiveHotelById(id);
		}
		 
			@GetMapping("getRoomDetailById/{id}")
			@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL')")
			public Optional<Room>getRoomById(@PathVariable Long id){

				return 	roomRepo.findById(id);
			}
		
		@GetMapping("/getHotelByHotelUsername/{hotelUsername}")
		public Optional<Hotel>getHotelByUsername(@PathVariable String hotelUsername){
		 return hotelRepo.findByHotelUsername(hotelUsername);
		}
		
		@GetMapping("/searchHotelByAddress/{hotelAddress}")
		public ArrayList<?> searchHotelByAddress(@PathVariable String hotelAddress){

		
		return hotelRepo.findByHotelAddress(hotelAddress);
			
			
		}
		@GetMapping("/searchHotelByName/{hotelName}")
		public ArrayList<?> searchHotelByName(@PathVariable String hotelName){
			
			return hotelRepo.findByHotelName(hotelName);
		}
		
		@GetMapping("/getInactiveCustomerOfMyHotel/{id}")
		public List<ReservationDetails>getInactiveCustomerOfMyHotel(@PathVariable Long id){
			return reservationRepo.findByMyHotelId(id);
		}
		
		@GetMapping("/getCustomerDetailById/{id}")
		public Optional<Customer>getCustomerDetailById(@PathVariable Long id){
			return customerRepo.findById(id);
		}
			
		@GetMapping("/getDistinctCheckOutCustomerDetail/{id}")
		public List<ReservationDetails>getDistinctCheckoutCustomer(@PathVariable Long id){
			return reservationRepo.findDistinctCustomer(id);
		}
		
	@PutMapping("/updateHotelDetail/{id}")
	public ResponseEntity<?>updateHotelDetail(@PathVariable Long id,@RequestBody Hotel hotel){
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String hotelUsername=((UserDetailsImpl)principal).getUsername();
		Optional<Hotel>ho=hotelRepo.findById(id);
		if(ho.isPresent()) {
			Hotel update=ho.get();
			String chotelUsername=update.getHotelUsername();
			if(hotelUsername.equals(chotelUsername)) {
				
				 hotelRepo.findById(id).map(updateHotel->{
					 if(hotel.getHotelName()!=null) {
					updateHotel.setHotelName(hotel.getHotelName());
					 }
					 if(hotel.getHotelOwner()!=null) {
					updateHotel.setHotelOwner(hotel.getHotelOwner());
					 }
					 if(hotel.getCity()!=null) {
					updateHotel.setCity(hotel.getCity());
					 }
					 if(hotel.getHotelAddress()!=null) {
					updateHotel.setHotelAddress(hotel.getHotelAddress());
					 }
					 if(hotel.getPhone()!=null) {
					updateHotel.setPhone(hotel.getPhone());
					 }
					 if(hotel.getLatitude()!=null) {
						 updateHotel.setLatitude(hotel.getLatitude());
					 }
					 if(hotel.getLongitude()!=null) {
						 updateHotel.setLongitude(hotel.getLongitude());
					 }
					 if(hotel.getPanNumber()!=null) {
					updateHotel.setPanNumber(hotel.getPanNumber());
					 }
					 if(hotel.getDescription()!=null) {
					updateHotel.setDescription(hotel.getDescription());
					 }
				
				return hotelRepo.save(updateHotel);
				}).orElseThrow(() -> new ResourceNotFoundException("Id "+id+ " not found"));
				 return ResponseEntity.ok(new MessageResponse("Updated"));
			}
		else {
			
			return ResponseEntity.ok(new MessageResponse("No match Found"));
		      
		}
		}
			else {
				return ResponseEntity.ok(id+" Id Not Present");
			}
		
	}	
	
	@PutMapping("/deleteHotelDetail/{id}")
	public ResponseEntity<?>deleteHotelDetails(@PathVariable Long id){
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetailsImpl)principal).getUsername();
		Optional<Hotel>a=hotelRepo.findById(id);
		if(a.isPresent()) {
			Hotel hotel1= a.get();
			String cUsername=hotel1.getHotelUsername();
			if(username.equals(cUsername)) {
			hotelRepo.deleteById(id);
			return ResponseEntity.ok("Hotel Details deleted Successfully");
			}else {
				return ResponseEntity.ok("You cannot delete other users data.");
			}
			}else {
			return ResponseEntity.ok("Hotel with id "+id+" is not found.");
		}
	}

	
	
	
}
