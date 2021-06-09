package np.com.hotel.hotelreservationapp.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import np.com.hotel.hotelreservationapp.exception.ResourceNotFoundException;

import np.com.hotel.hotelreservationapp.model.Customer;
import np.com.hotel.hotelreservationapp.model.Hotel;
import np.com.hotel.hotelreservationapp.model.ReservationDetails;
import np.com.hotel.hotelreservationapp.model.Room;
import np.com.hotel.hotelreservationapp.model.TemporaryReservation;
import np.com.hotel.hotelreservationapp.model.UsersAuthentication;
import np.com.hotel.hotelreservationapp.payload.response.MessageResponse;
import np.com.hotel.hotelreservationapp.repository.CheckOutRepository;
import np.com.hotel.hotelreservationapp.repository.CustomerRepository;
import np.com.hotel.hotelreservationapp.repository.HotelRepository;
import np.com.hotel.hotelreservationapp.repository.ReservationRepository;
import np.com.hotel.hotelreservationapp.repository.RoomRepository;
import np.com.hotel.hotelreservationapp.repository.TemporaryReservationRepository;
import np.com.hotel.hotelreservationapp.repository.UsersAuthenticationRepository;
import np.com.hotel.hotelreservationapp.services.UserDetailsImpl;
import np.com.hotel.hotelreservationapp.utils.AppConstants;

@RestController
@RequestMapping("/api/adminController")
public class AdminController {

	@Autowired
	TemporaryReservationRepository temporaryreservationRepo;

	@Autowired
	ReservationRepository reservationRepo;

	@Autowired
	HotelRepository hotelRepo;
	
	@Autowired
	UsersAuthenticationRepository userAuthRepo;
	
	@Autowired
	CustomerRepository customerRepo;
	
	@Autowired
	RoomRepository roomRepo;
	
	@Autowired
	CheckOutRepository checkOutRepo;
	
	@Autowired
	UsersAuthenticationRepository authRepo;



	@PostMapping("/transfer/{temporaryReservationId}")
	@PreAuthorize(" hasRole('HOTEL') or hasRole('ADMIN')")
	public ResponseEntity<?>transferTemporaryReservationToReservationDetail(@PathVariable Long temporaryReservationId){

		Optional<TemporaryReservation>temp=temporaryreservationRepo.findById(temporaryReservationId);
		TemporaryReservation t=temp.get();
		
		
//		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		String hUsername=((UserDetailsImpl)principal).getUsername();
//		
//		Long hId=((UserDetailsImpl)principal).getUserId();
//		
//	
//		
//		Optional<Hotel>hotelDetail=hotelRepo.getHotelDetail(hId);
//		Hotel h=hotelDetail.get();
//		String hotelUsername=h.getHotelUsername();


		DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now=LocalDateTime.now();

		ReservationDetails transfer=new ReservationDetails(
				t.getHotelName(),
				t.getRoom(),
				t.getRoomNumber(),
				dtf.format(now),
				t.getCheckOutDate(),
				t.getNoOfGuest(),
				t.getCustomer(),
				t.getType(),
				t.getReservationTime(),
				t.getHotel()

				);
	
//		transfer.setHotelReservation(h);
		reservationRepo.save(transfer);
		
		temporaryreservationRepo.deleteById(temporaryReservationId);

		return ResponseEntity.ok(new MessageResponse("Customer has checked into the hotel."));

	// }
//		else {
//			return ResponseEntity.ok("Can not checked other hotel reservation");
//		}
	
	}
	
	@PostMapping("/newTemporaryReservation/hotel/{hotelId}/room/{roomId}/customer/{customerId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL') or hasRole('CUSTOMER')")
	public ResponseEntity<?>saveTemporaryReservationDetails(@RequestBody TemporaryReservation reservation,
		@PathVariable Long hotelId,@PathVariable Long roomId, @PathVariable Long customerId ){
//		
//		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//	    String username=((UserDetailsImpl)principal).getUsername();
    

	    try{
	    	
   
		
		    Optional<Hotel> hotelDetail=hotelRepo.findById(hotelId);
		    Hotel h=hotelDetail.get();
		    Long hId=h.getId();
		    String hotelName=h.getHotelName();
//		    boolean activeStatus=h.isActive();
				
			 Optional<Customer> customerDetail=customerRepo.findById(customerId);
	         Customer c=customerDetail.get();
  
	
	    
	
	    Optional<Room>roomDetail=roomRepo.findById(roomId);
   
	    Room r=roomDetail.get();
	    String roomType=r.getRoomType();
	    Long roomNumber=r.getRoomNumber();
			
			if(roomDetail.isPresent()) {
	   
	    
	    DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	    LocalDateTime now=LocalDateTime.now();
	 
	    TemporaryReservation newReservation=new TemporaryReservation(hotelName,h,r,
	    													roomNumber,
	    												
	    												dtf.format(now),
	    												reservation.getCheckInDate(),
	    												reservation.getCheckOutDate(),
	    												reservation.getNoOfGuest(),
	    												
	    												c,
	    												roomType
	    												);

	    temporaryreservationRepo.save(newReservation);
	    roomRepo.findById(roomId).map(updateActive->{
	    updateActive.setAvailability(false);
	    
	   return roomRepo.save(updateActive);
   });
   
	return ResponseEntity.ok("Room Reserved successfully!");
			
			
	    }else {
		return ResponseEntity.ok("Room cannot Reserved.");
	}

	    }
		catch(Exception e){
			return ResponseEntity.ok(e);
		}
		
	   
	}
	
	
	@PostMapping("/addCustomerDetails/{id}")
	  public ResponseEntity<?>saveCustomerDetail(@RequestBody Customer customer, @PathVariable Long id){
		
//			Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//			String username=((UserDetailsImpl)principal).getUsername();
//			String email=((UserDetailsImpl)principal).getEmail();
//			Long id=((UserDetailsImpl)principal).getUserId();
			
			Optional<UsersAuthentication>obj=authRepo.findById(id);
			UsersAuthentication uid=obj.get();
			String username=uid.getUsername();
			String email=uid.getEmail();
			if(customerRepo.existsByUsername(username)) {
				
				return ResponseEntity.ok(new MessageResponse("This user already exist."));
				}
				
			else {
			
			Customer add=new Customer(

										customer.getFullname(),
										username,
										customer.getAddress(),
										email,
										customer.getIdType(),
										customer.getIdNumber(),
										customer.getAge(),
										customer.getGender(),
										customer.getPhone(),
										customer.getUser(),
										customer.getReservation());
			
			add.setUser(uid);
			customerRepo.save(add);
			
			return ResponseEntity.ok(new MessageResponse("Customer Details Save Successfully"));
			}
			
	}
	
	@PostMapping("/addHotelDetail/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HOTEL')")
	public ResponseEntity<?>addHotelDetail(@RequestBody Hotel hotel, @PathVariable Long id){
//		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		String hotelUsername=((UserDetailsImpl)principal).getUsername();
//		Long id=((UserDetailsImpl)principal).getUserId();
		
		Optional<UsersAuthentication>obj=userAuthRepo.findById(id);
	    UsersAuthentication hid=obj.get();
	    String hotelUsername=hid.getUsername();
		
		//String email=((UserDetailsImpl)principal).getEmail();
		if(hotelRepo.existsByHotelUsername(hotelUsername)) {
			return ResponseEntity.ok(new MessageResponse("this hotel already exist."));
		}else {
		Hotel add=new Hotel(hotel.getHotelName(),
							hotel.getHotelUsername(),
							hotel.getHotelOwner(),
							hotel.getCity(),
							hotel.getHotelAddress(),
							hotel.getPhone(),
							hotel.getPanNumber(),
							hotel.getDocument(),
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
		
		
		return ResponseEntity.ok(new MessageResponse("Hotel details saved successfully."));
		}
	}
	

	@PostMapping("/addRoomDetail/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HOTEL')")
	public ResponseEntity<?>addRoomDetail(@RequestBody Room room, @PathVariable Long id)
	{
//		Optional<Room> demo = roomRepo.getRoomDetail(room.getRoomNumber(), id);
		
		if(roomRepo.getRoomDetail(room.getRoomNumber(), id).isPresent()) {
			return ResponseEntity.ok(new MessageResponse("This room is already exist"));
		}else {
			
		
//		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		String hotelUsername=((UserDetailsImpl)principal).getUsername();
//		Long id=((UserDetailsImpl)principal).getUserId();
//		

		Optional<Hotel>h=hotelRepo.findById(id);
		Hotel ho=h.get();
		Long hotelId=ho.getId();
		String hotelUsername = ho.getHotelUsername();
		
		//String email=((UserDetailsImpl)principal).getEmail();


//		Long roomNumber=room.getRoomNumber();

	
	
		Room add=new Room(hotelUsername, room.getRoomNumber(),
				room.getRoomType(),
				room.getDescription(),
				room.isActive(),
				room.isAvailability(),
				ho,
				room.getReservation()
				);
		
		roomRepo.save(add);
		
		
		return ResponseEntity.ok(new MessageResponse("Room details saved successfully."));
		}
	
	}
	
	@GetMapping("getRoomDetailByHotelId/{id}")
	public List<Room>getRoomDetailByHotelId(@PathVariable Long id){
		return roomRepo.findRoomByHotelId(id);
	}
		
	
	@GetMapping("/getTemporaryReservation")
	public ResponseEntity<?>getTemporaryReservation(){
		return ResponseEntity.ok(temporaryreservationRepo.findAll());
		
}
	
	@GetMapping("/getRegisteredHotelDetail")
	@PreAuthorize("hasRole('ADMIN')")
	 public List<UsersAuthentication>getRegisteredHotelDetail(){
		
		   return userAuthRepo.findRegisterdHotel();
	}
	
	@GetMapping("/getRegisteredHotelOnly")
	@PreAuthorize("hasRole('ADMIN')")
	 public List<UsersAuthentication>getRegisteredHotelOnly(){
		
		   return userAuthRepo.findRegisterdHotelOnly();
	}
	
	@GetMapping("/getRegisteredUserById/{id}")
	public Optional<UsersAuthentication> getRegisteredUserById(@PathVariable Long id){
		return userAuthRepo.findById(id);
	
}
	
	@GetMapping("/getCustomerById/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HOTEL') ")
	public Optional<Customer>getCustomerById(@PathVariable Long id){
		return customerRepo.findById(id);
	}
	
	
	
	
 @PutMapping("/updateHotelDetail/{id}")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<?>updateHotelDetail(@PathVariable Long id,@RequestBody Hotel hotel){
//	Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//	String hotelUsername=((UserDetailsImpl)principal).getUsername();
	Optional<Hotel>ho=hotelRepo.findById(id);
	if(ho.isPresent()) {
//		Hotel update=ho.get();
//		String chotelUsername=update.getHotelUsername();
		
			
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
				 if(hotel.getPanNumber()!=null) {
				updateHotel.setPanNumber(hotel.getPanNumber());
				 }
				 if(hotel.getDocument()!=null) {
				updateHotel.setDocument(hotel.getDocument());
				 }
				 if(hotel.getDescription()!=null) {
				updateHotel.setDescription(hotel.getDescription());
				 }
			
			return hotelRepo.save(updateHotel);
			}).orElseThrow(() -> new ResourceNotFoundException("Id "+id+ " not found"));
			 return ResponseEntity.ok(new MessageResponse("Updated"));
		}
	
	
		else {
			return ResponseEntity.ok(new MessageResponse(id+" Id Not Present"));
		}
}
 
	
 
 @PutMapping("/updateActiveStatusOfHotel/{hotelId}")
 public ResponseEntity<?>updateActiveStatusOfHotel(@RequestBody Hotel hotelDetail,@PathVariable Long hotelId){
	 
	 Optional<Hotel> hotel = hotelRepo.findById(hotelId);
	 Hotel h = hotel.get();
	 boolean activeStatusFromDb = h.isActive();
	 if (activeStatusFromDb==true) {
		 hotelRepo.findById(hotelId).map(updateActiveStatus ->{
			
				 updateActiveStatus.setActive(false);
			
			 return hotelRepo.save(updateActiveStatus);
		 }).orElseThrow(() -> new ResourceNotFoundException("Id "+hotelId+ " not found"));
		 return ResponseEntity.ok(new MessageResponse("Updated Successfully from if"));
		
	 }else{
		 
		 hotelRepo.findById(hotelId).map(updateActiveStatus ->{
	
				 updateActiveStatus.setActive(true);
			
			 return hotelRepo.save(updateActiveStatus);
		 }).orElseThrow(() -> new ResourceNotFoundException("Id "+hotelId+ " not found"));
		 return ResponseEntity.ok(new MessageResponse("Updated Successfully from else"));
	 }
 }
 
 
   @PutMapping("/updateActiveStatusOfCustomer/{id}")
  public ResponseEntity<?>updateActiveStatusOfCustomer(@RequestBody Customer customerDetail,@PathVariable long id ){
	   Optional<Customer> customer = customerRepo.findById(id);
	   Customer c= customer.get();
	   boolean activeStatusFromDb = c.isActive();
	   if(activeStatusFromDb==true) {
		   customerRepo.findById(id).map(updateActiveStatus ->{
		   
			   updateActiveStatus.setActive(false);
			 return customerRepo.save(updateActiveStatus);  
		   }).orElseThrow(() -> new ResourceNotFoundException("Id "+id+ "not Found"));
		   return ResponseEntity.ok(new MessageResponse("Updated Successfully from if"));
		   }else {
			   customerRepo.findById(id).map(updateActiveStatus ->{
			  updateActiveStatus.setActive(true);
			  return customerRepo.save(updateActiveStatus);
			    }).orElseThrow(() -> new ResourceNotFoundException("Id "+id+ " not found"));
			   return ResponseEntity.ok(new MessageResponse("Updated Successfully from else"));
	   }
   }


	@PutMapping("/updateCustomerDetail/{id}")
	public ResponseEntity<?>updateCustomerDetail(@PathVariable Long id,@RequestBody Customer customer){
		
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetailsImpl)principal).getUsername();
		
		Optional<Customer>cus=customerRepo.findById(id);
		if(cus.isPresent()) {
			Customer update=cus.get();
			String cUsername=update.getUsername();
//			if(username.equals(cUsername)) {
				
				 customerRepo.findById(id).map(updateCustomerDetail->{
					 
					 if(customer.getFullname()!=null) {
					updateCustomerDetail.setFullname(customer.getFullname());
					 }
					 if(customer.getAddress()!=null) {
					updateCustomerDetail.setAddress(customer.getAddress());
					 }
					 if(customer.getIdType()!=null) {
				
					
					updateCustomerDetail.setIdType(customer.getIdType());
					 }
					 if(customer.getIdNumber()!=null) {
					updateCustomerDetail.setIdNumber(customer.getIdNumber());
					 }
					 if(customer.getAge()!=null) {
					updateCustomerDetail.setAge(customer.getAge());
					 }if(customer.getGender()!=null) {
					updateCustomerDetail.setGender(customer.getGender());
					 }
					 if(customer.getPhone()!=null) {
					updateCustomerDetail.setPhone(customer.getPhone());
					 }
					
					return customerRepo.save(updateCustomerDetail);
				}).orElseThrow(() -> new ResourceNotFoundException("Id "+id+ " not found"));
			return ResponseEntity.ok(new MessageResponse("Customer Detail Update Successfully!"));	
//			}else {
//					return ResponseEntity.ok(new MessageResponse("Customer cannot updated. "));
//				}

	
			
		}else {
		
	      return ResponseEntity.ok(new MessageResponse("Customer with id "+id+" is not found"));	
		}
						
				
	}
	
	
	@PutMapping("/updateRoomDetail/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL')")
	public ResponseEntity<?>updateRoomDetail(@PathVariable Long id,@RequestBody Room room){
//		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		String hotelUsername=((UserDetailsImpl)principal).getUsername();
		Optional<Room>a=roomRepo.findById(id);
		if(a.isPresent()) {

				
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
			return ResponseEntity.ok(new MessageResponse("This Room can not be updated"));
		}

	}	
	
	
	@PutMapping("/updateReservationDetail/{id}")
	public ReservationDetails updateReservationDetail(@PathVariable Long id,@RequestBody ReservationDetails reservation){
		

		Optional<ReservationDetails>reservationDetail=reservationRepo.findById(id);

		if(reservationDetail.isPresent()){
			
			
			return reservationRepo.findById(id).map(updateReservation->{
				updateReservation.setNoOfGuest(reservation.getNoOfGuest());
				updateReservation.setCheckInDate(reservation.getCheckInDate());
				updateReservation.setCheckOutDate(reservation.getCheckOutDate());
			return reservationRepo.save(updateReservation);
			}).orElseThrow(() -> new ResourceNotFoundException("Id "+id+ " not found"));

		}else{
		return null;
		}	
	}	
	
	
	@PutMapping("/updateTemporaryReservation/{id}")
	public TemporaryReservation updateTemporaryReservation(@PathVariable Long id,@RequestBody TemporaryReservation reservation){
	
		Optional<TemporaryReservation>reservationDetail=temporaryreservationRepo.findById(id);
//		TemporaryReservation r=reservationDetail.get();
//		Customer customerDetail=r.getCustomer();
//		String cUsername=customerDetail.getUsername();

		if(reservationDetail.isPresent()){
			
			
			return temporaryreservationRepo.findById(id).map(updateReservation->{
				updateReservation.setNoOfGuest(reservation.getNoOfGuest());
				updateReservation.setCheckInDate(reservation.getCheckInDate());
				updateReservation.setCheckOutDate(reservation.getCheckOutDate());
			return temporaryreservationRepo.save(updateReservation);
			}).orElseThrow(() -> new ResourceNotFoundException("Id "+id+ " not found"));

		}else{
		return null;
		}	
	}	

	

	@PutMapping("/deleteHotelDetail/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?>deleteHotelDetails(@PathVariable Long id){
		Optional<Hotel>a=hotelRepo.findById(id);
		if(a.isPresent()) {
			
			hotelRepo.deleteById(id);
			return ResponseEntity.ok(new MessageResponse("Hotel Details deleted Successfully"));
			
			}else {
			return ResponseEntity.ok(new MessageResponse("Hotel with id "+id+" is not found."));
		}
	}
	
	@PutMapping("/deleteUserDetail/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?>deleteUserDetails(@PathVariable Long id){
		Optional<UsersAuthentication>u=userAuthRepo.findById(id);
			if(u.isPresent()) {
			
			userAuthRepo.deleteById(id);
			return ResponseEntity.ok(new MessageResponse("User Details deleted Successfully"));
			
			}else {
			return ResponseEntity.ok(new MessageResponse("User with id "+id+" is not found."));
		}
	}
	
	
	
	@PutMapping("/deleteCustomerDetail/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?>deleteCustomerDetails(@PathVariable Long id){
			
		Optional<Customer>c=customerRepo.findById(id);
		if(c.isPresent()) {
			
			customerRepo.deleteById(id);
			return ResponseEntity.ok(new MessageResponse("Customer Details deleted Successfully"));
			}
		  else{
			return ResponseEntity.ok(new MessageResponse("Customer with id "+id+" is not found."));
		}
	}
	
	@PutMapping("/deleteRoomDetail/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL')")
	public ResponseEntity<?>deleteRoomDetails(@PathVariable Long id){
	
		Optional<Room>a=roomRepo.findById(id);
		if(a.isPresent()) {

				roomRepo.deleteById(id);
		
				return ResponseEntity.ok(new MessageResponse("Room Details deleted Successfully"));

		}else {
			return ResponseEntity.ok(new MessageResponse("Room with id "+id+" is not found."));
		}
	}
	
	@GetMapping("/getReservedRoom")
 public List<ReservationDetails>getReservedRoom(){
	 return reservationRepo.findReservedRoom();	
	}
	
	@GetMapping("/getReservationDetailById/{id}")	
	public Optional<ReservationDetails>getReservationDetailById(@PathVariable Long id){
			return reservationRepo.findById(id);
		}
	
	@GetMapping("/getTemporaryReservationDetailById/{id}")	
	public Optional<TemporaryReservation>getTemporaryReservationDetailById(@PathVariable Long id){
			return temporaryreservationRepo.findById(id);
		}

	
	@GetMapping("/getNonReservedRoom")
 public List<Room>getNonReservedRoom(){
	 return roomRepo.findNonReservedRoom();	
	}
	
	@PutMapping("/deleteReservationDetail/{id}")
	public ResponseEntity<?>deleteReservationDetails(@PathVariable Long id){

		Optional<ReservationDetails>reservationDetail=reservationRepo.findById(id);

		if(reservationDetail.isPresent()){
			reservationRepo.deleteById(id);

			return ResponseEntity.ok(new MessageResponse("Reservation Canceled"));
		}else{
			return ResponseEntity.ok(new MessageResponse("You cannot cancel others reservation"));
		}
	}
	
	@PutMapping("/deleteTemporaryReservation/{id}")
	public ResponseEntity<?>deleteTemporaryReservationDetails(@PathVariable Long id){
		
		
		Optional<TemporaryReservation>reservationDetail=temporaryreservationRepo.findById(id);
	
		if(reservationDetail.isPresent()){
			temporaryreservationRepo.deleteById(id);

			return ResponseEntity.ok(new MessageResponse("Reservation Canceled"));
		}else{
			return ResponseEntity.ok(new MessageResponse("You cannot cancel others reservation"));
		}
	}
	
	
	
	   @PutMapping("/updateActiveStatusOfRoom/{id}")
	   public ResponseEntity<?>updateActiveStatusOfRoom(@RequestBody Room roomDetail,@PathVariable long id ){
	 	   Optional<Room> room = roomRepo.findById(id);
	 	   Room r= room.get();
	 	   boolean activeStatusFromDb = r.isActive();
	 	   if(activeStatusFromDb==true) {
	 		   roomRepo.findById(id).map(updateActiveStatus ->{
	 		   
	 			   updateActiveStatus.setActive(false);
	 			 return roomRepo.save(updateActiveStatus);  
	 		   }).orElseThrow(() -> new ResourceNotFoundException("Id "+id+ "not Found"));
	 		   return ResponseEntity.ok(new MessageResponse("Updated Successfully from if"));
	 		   }else {
	 			   roomRepo.findById(id).map(updateActiveStatus ->{
	 			  updateActiveStatus.setActive(true);
	 			  return roomRepo.save(updateActiveStatus);
	 			    }).orElseThrow(() -> new ResourceNotFoundException("Id "+id+ " not found"));
	 			   return ResponseEntity.ok(new MessageResponse("Updated Successfully from else"));
	 	   }
	    }
	   @PutMapping("/checkOut/{reservationId}")
	   public ResponseEntity<?>checkOutReservation(@RequestBody Hotel hotelDetail,
			   @PathVariable Long reservationId){
	  	 
	  	 Optional<ReservationDetails>res = reservationRepo.findById(reservationId);
	  	 ReservationDetails reservation = res.get();
	  	 Room r =reservation.getRoom();
	  	 Long id=r.getId();
	  	 boolean activeStatusFromDb = reservation.isActive();
	  	 if (activeStatusFromDb==true) {
	  		 
	  		 
	  		 reservationRepo.findById(reservationId).map(updateActiveStatus ->{
	  			
	  				 updateActiveStatus.setActive(false);
	  			   return reservationRepo.save(updateActiveStatus);
	  		   
	  			 
	  		 }).orElseThrow(() -> new ResourceNotFoundException("Id "+reservationId+ " not found"));
	  		 
	  		 roomRepo.findById(id).map(updateActive->{
		  		    updateActive.setAvailability(true);
		  		    
		  		   return roomRepo.save(updateActive);
		  	   }).orElseThrow(() -> new ResourceNotFoundException("Id "+id+ " not found"));;
		  	   
	  		 
	  		 return ResponseEntity.ok(new MessageResponse("Updated Successfully from if"));
	  		 
	  	   
	  		
	  	 }else{

	  		 return ResponseEntity.ok(new MessageResponse("sorry cannot update"));
	  	 }
	   }
	   
}
