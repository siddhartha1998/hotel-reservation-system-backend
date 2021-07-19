package np.com.hotel.hotelreservationapp.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
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
import np.com.hotel.hotelreservationapp.model.Room;
import np.com.hotel.hotelreservationapp.model.TemporaryReservation;
import np.com.hotel.hotelreservationapp.payload.response.MessageResponse;
import np.com.hotel.hotelreservationapp.repository.CustomerRepository;
import np.com.hotel.hotelreservationapp.repository.HotelRepository;
import np.com.hotel.hotelreservationapp.repository.RoomRepository;
import np.com.hotel.hotelreservationapp.repository.TemporaryReservationRepository;
import np.com.hotel.hotelreservationapp.services.UserDetailsImpl;

@RestController
@RequestMapping("/api/temporaryReservation")
public class TemporaryReservationController {

	@Autowired
	CustomerRepository customerRepo;
	
	@Autowired
	RoomRepository roomRepo;
	
	@Autowired
	TemporaryReservationRepository temporaryreservationRepo;
	
	@Autowired
	HotelRepository hotelRepo;
	
	
	@PostMapping("/newTemporaryReservation/hotel/{hotelId}/room/{roomId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL') or hasRole('CUSTOMER')")
	public ResponseEntity<?>saveTemporaryReservationDetails(@RequestBody TemporaryReservation reservation,@PathVariable Long hotelId,@PathVariable Long roomId ){
		
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    String username=((UserDetailsImpl)principal).getUsername();
	    

	    try{
		
		    Optional<Hotel> hotelDetail=hotelRepo.findById(hotelId);
		    Hotel h=hotelDetail.get();
		    String hotelName=h.getHotelName();
		    boolean activeStatus=h.isActive();
		    if(activeStatus==true) {
		if(!temporaryreservationRepo.existsByRoomId(roomId)){
			
			 Optional<Customer> customerDetail=customerRepo.findByUsername(username);
	         Customer c=customerDetail.get();
	  
	
	    
	
	    Optional<Room>roomDetail=roomRepo.findById(roomId);
   
	    Room r=roomDetail.get();
	    String roomType=r.getRoomType();
	    Long roomNumber=r.getRoomNumber();
	   
	    
	    DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	    LocalDateTime now=LocalDateTime.now();
	 
	    TemporaryReservation newReservation=new TemporaryReservation(hotelName, h, r,
	    												roomNumber,
	    												dtf.format(now),
	    												reservation.getCheckInDate(),
	    												reservation.getCheckOutDate(),
	    												reservation.getNoOfGuest(),
	    												reservation.getIdType(),
	    												reservation.getIdNumber(),
	    												c
	    												);
//	    newReservation.setTemporaryReservation(h);
	    temporaryreservationRepo.save(newReservation);
	    roomRepo.findById(roomId).map(updateActive->{
	    updateActive.setAvailability(false);
	    
	   return roomRepo.save(updateActive);
   });
   
	return ResponseEntity.ok(new MessageResponse("Room Reserved successfully!"));

		}
		
		else{
			
			return ResponseEntity.ok(new MessageResponse("This room is already reserved."));
		}
		    }else {
		    	return ResponseEntity.ok(new MessageResponse("This hotel does not exist"));
		    }
		
			
		}
		catch(Exception e){
			return ResponseEntity.ok(e);
		}
		
	   
	}

	@GetMapping("/getTemporaryReservation")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL')")
	public List<TemporaryReservation>getTemporaryReservation(){
		
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String loggedInUsername=((UserDetailsImpl)principal).getUsername();
		
		
		return temporaryreservationRepo.findByReservedBy(loggedInUsername);
		
}

	@GetMapping("/getTemporaryReservationById/{id}")
	public List<TemporaryReservation>getTemporaryReservationById(@PathVariable Long id){
		return temporaryreservationRepo.findByHotelId(id);
	}
	
	@PutMapping("/updateTemporaryReservation/{id}")
	public TemporaryReservation updateTemporaryReservation(@PathVariable Long id,@RequestBody TemporaryReservation reservation){
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetailsImpl)principal).getUsername();

		Optional<TemporaryReservation>reservationDetail=temporaryreservationRepo.findById(id);
		TemporaryReservation r=reservationDetail.get();
		Customer customerDetail=r.getCustomer();
		String cUsername=customerDetail.getUsername();

		if(username.equals(cUsername)){
			
			
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
	

	@PutMapping("/deleteTemporaryReservation/{id}")
	public ResponseEntity<?>deleteReservationDetails(@PathVariable Long id){
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetailsImpl)principal).getUsername();
		
		Optional<TemporaryReservation>reservationDetail=temporaryreservationRepo.findById(id);
		TemporaryReservation r=reservationDetail.get();
		Customer customerDetail=r.getCustomer();
		String cUsername=customerDetail.getUsername();

		if(username.equals(cUsername)){
			temporaryreservationRepo.deleteById(id);

			return ResponseEntity.ok("Reservation Canceled");
		}else{
			return ResponseEntity.ok("You cannot cancel others reservation");
		}
	}
	
	
}