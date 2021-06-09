package np.com.hotel.hotelreservationapp.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import np.com.hotel.hotelreservationapp.model.CheckOut;

import np.com.hotel.hotelreservationapp.model.Hotel;
import np.com.hotel.hotelreservationapp.repository.CheckOutRepository;
import np.com.hotel.hotelreservationapp.repository.CustomerRepository;
import np.com.hotel.hotelreservationapp.repository.HotelRepository;
import np.com.hotel.hotelreservationapp.repository.ReservationRepository;
import np.com.hotel.hotelreservationapp.repository.RoomRepository;
import np.com.hotel.hotelreservationapp.services.UserDetailsImpl;

@RestController
@RequestMapping("api/checkOutReservation")
public class CheckOutController {
	
	@Autowired
	CheckOutRepository checkOutRepo;
	/*
	@Autowired
	OfflineReservationRepository offlineReservationRepo;
	*/
	@Autowired
	ReservationRepository reservationRepo;
	
	@Autowired
	CustomerRepository customerRepo;
	
	@Autowired
	HotelRepository hotelRepo;
	
	@Autowired
	RoomRepository roomRepo;
	
	
	
	@PostMapping("/checkOutFromOnlineReservation/{hotelId}/{roomNumber}/{customerId}")
	public ResponseEntity<?>checkOut(@PathVariable Long hotelId,@PathVariable Long roomNumber,@PathVariable Long customerId,@RequestBody CheckOut check){
		
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String hUsername=((UserDetailsImpl)principal).getUsername();

		
		Optional<Hotel>hos=hotelRepo.findById(hotelId);
		Hotel h=hos.get();
		String hotelUsername=h.getHotelUsername();
		
		DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now=LocalDateTime.now();
		
		if(hUsername.equals(hotelUsername)) {
		
		
		if(reservationRepo.getByCustomer(hotelId,roomNumber,customerId)!=null) {
			
			
		    //Optional<Customer>cus=customerRepo.findById(customerId);
			//Customer c=cus.get();
			//String cUsername=c.getUsername();
			
		
			
			
			
			CheckOut checkOut=new CheckOut();
			
		//	checkOut.setCheckOutBy(cUsername);
			checkOut.setCheckOutDate(dtf.format(now));
			checkOut.setHotel(hotelUsername);
			checkOut.setRoom(roomNumber);
			checkOut.setTypeOfPayment(check.getTypeOfPayment());
			checkOut.setTypeOfPayment(check.getTotalPayment());
			
			checkOutRepo.save(checkOut);
			
			roomRepo.getRoomDetail(roomNumber,hotelId).map(updateAvailablity->{
				updateAvailablity.setActive(true);
				return roomRepo.save(updateAvailablity);
			});
			
			return ResponseEntity.ok("Checked out");
			
		}else {
			return ResponseEntity.ok("You have not reserved this room or hotel");
		}
		}else {
			return ResponseEntity.ok("You are not eligible to perform this task.");
		}
		
	}

/*	
	@PostMapping("/checkOutFromOfflineReservation/{hotelId}/{roomNumber}")
	public ResponseEntity<?>checkOutFromOfflineReservation(@PathVariable Long hotelId,@PathVariable Long roomNumber,@RequestBody CheckOut check){
		
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String hUsername=((UserDetailsImpl)principal).getUsername();

		
		Optional<Hotel>hos=hotelRepo.findById(hotelId);
		Hotel h=hos.get();
		String hotelUsername=h.getHotelUsername();
		
		DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now=LocalDateTime.now();
		
		if(hUsername.equals(hotelUsername)) {
		
		
		if(offlineReservationRepo.getByCustomer(hotelId,roomNumber)!=null) {
			
			
			
			
			
			
			CheckOut checkOut=new CheckOut();
			
		//	checkOut.setCheckOutBy(cUsername);
			checkOut.setCheckOutDate(dtf.format(now));
			checkOut.setHotel(hotelUsername);
			checkOut.setRoom(roomNumber);
			checkOut.setTypeOfPayment(check.getTypeOfPayment());
			checkOut.setTypeOfPayment(check.getTotalPayment());
			
			checkOutRepo.save(checkOut);
			
			roomRepo.getRoomDetail(roomNumber,hotelId).map(updateAvailablity->{
				updateAvailablity.setActive(true);
				return roomRepo.save(updateAvailablity);
			});
			
			return ResponseEntity.ok("Checked out");
			
		}else {
			return ResponseEntity.ok("You have not reserved this room or hotel");
		}
		}else {
			return ResponseEntity.ok("You are not eligible to perform this task.");
		}
		
	} */

}
