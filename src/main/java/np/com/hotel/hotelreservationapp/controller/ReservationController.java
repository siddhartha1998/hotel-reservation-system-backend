package np.com.hotel.hotelreservationapp.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.apache.catalina.User;
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
import np.com.hotel.hotelreservationapp.model.ReservationDetails;
import np.com.hotel.hotelreservationapp.model.Room;
import np.com.hotel.hotelreservationapp.repository.CustomerRepository;
import np.com.hotel.hotelreservationapp.repository.HotelRepository;
import np.com.hotel.hotelreservationapp.repository.ReservationRepository;
import np.com.hotel.hotelreservationapp.repository.RoomRepository;
import np.com.hotel.hotelreservationapp.services.UserDetailsImpl;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

	@Autowired
	CustomerRepository customerRepo;
	
	@Autowired
	RoomRepository roomRepo;
	
	@Autowired
	ReservationRepository reservationRepo;
	
	@Autowired
	HotelRepository hotelRepo;
	
	
	@PostMapping("/newReservation/hotel/{hotelId}/room/{roomId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('HOTEL') or hasRole('CUSTOMER')")
	public ResponseEntity<?>saveReservationDetails(@RequestBody ReservationDetails reservation,@RequestBody User user,@PathVariable Long hotelId,@PathVariable Long roomId ){
		
    Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username=((UserDetailsImpl)principal).getUsername();
		
	//	String username=user.getUsername();
	    
	    try{
	   Optional<Customer> customerDetail=customerRepo.findByUsername(username);
	    Customer c=customerDetail.get();
	    Optional<Hotel> hotelDetail=hotelRepo.findById(hotelId);
	    Hotel h=hotelDetail.get();
	    String hotelName=h.getHotelName();
	   // Long customerId=c.getId();
	    Optional<Room>roomDetail=roomRepo.findById(roomId);
	 //String=roomRepo.findById(id)
	    
	    Room r=roomDetail.get();
	    String roomType=r.getRoomType();
	    Long roomNumber=r.getRoomNumber();
	   
	    
	    DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	    LocalDateTime now=LocalDateTime.now();
	 
	    ReservationDetails newReservation=new ReservationDetails(hotelName,r,
	    												roomNumber,
	    												reservation.getCheckInDate(),
	    												reservation.getCheckOutDate(),
	    												reservation.getNoOfGuest(),
	    												c,
	    												roomType,
	    												dtf.format(now),
	    												h);
//	    newReservation.setHotelReservation(h);
	reservationRepo.save(newReservation);
	return ResponseEntity.ok("Reserved");
		}catch(Exception e){
			return ResponseEntity.ok(e.getMessage());
		}
	}

	@GetMapping("/getReservationDetail")
	public List<ReservationDetails>getReservationDetail(){
		return reservationRepo.findAll();
		
	}
	@GetMapping("/getReservationDetailById/{id}")	
	public Optional<ReservationDetails>getReservationDetailById(@PathVariable Long roomId){
			return reservationRepo.findById(roomId);
		}

	@PutMapping("/updateReservationDetail/{id}")
	public ReservationDetails updateReservationDetail(@PathVariable Long id,@RequestBody ReservationDetails reservation){
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetailsImpl)principal).getUsername();

		Optional<ReservationDetails>reservationDetail=reservationRepo.findById(id);
		ReservationDetails r=reservationDetail.get();
		Customer customerDetail=r.getCustomer();
		String cUsername=customerDetail.getUsername();

		if(username.equals(cUsername)){
			
			
			return reservationRepo.findById(id).map(updateReservation->{
				updateReservation.setNoOfGuest(reservation.getNoOfGuest());
				updateReservation.setCheckInDate(reservation.getCheckInDate());
			return reservationRepo.save(updateReservation);
			}).orElseThrow(() -> new ResourceNotFoundException("Id "+id+ " not found"));

		}else{
		return null;
		}	
	}	
	

	@PutMapping("/deleteReservationDetail/{id}")
	public ResponseEntity<?>deleteReservationDetails(@PathVariable Long id){
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetailsImpl)principal).getUsername();
		
		Optional<ReservationDetails>reservationDetail=reservationRepo.findById(id);
		ReservationDetails r=reservationDetail.get();
		Customer customerDetail=r.getCustomer();
		String cUsername=customerDetail.getUsername();

		if(username.equals(cUsername)){
			reservationRepo.deleteById(id);

			return ResponseEntity.ok("Reservation Canceled");
		}else{
			return ResponseEntity.ok("You cannot cancel others reservation");
		}
	}
	
	
}