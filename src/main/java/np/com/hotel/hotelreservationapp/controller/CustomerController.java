package np.com.hotel.hotelreservationapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import np.com.hotel.hotelreservationapp.exception.ResourceNotFoundException;
import np.com.hotel.hotelreservationapp.model.Customer;
import np.com.hotel.hotelreservationapp.model.UsersAuthentication;
import np.com.hotel.hotelreservationapp.repository.CustomerRepository;
import np.com.hotel.hotelreservationapp.repository.UsersAuthenticationRepository;
import np.com.hotel.hotelreservationapp.services.UserDetailsImpl;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UsersAuthenticationRepository authRepo;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	CustomerRepository customerRepo;
	
	@PostMapping("/saveCustomerDetails")
  public ResponseEntity<?>saveCustomerDetail(@RequestBody Customer customer){
	
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetailsImpl)principal).getUsername();
		String email=((UserDetailsImpl)principal).getEmail();
		Long id=((UserDetailsImpl)principal).getUserId();
		
		Optional<UsersAuthentication>obj=authRepo.findById(id);
		UsersAuthentication uid=obj.get();
		if(customerRepo.existsByUsername(username)) {
			
			return ResponseEntity.ok("This user already exist.");}
			
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
		
		return ResponseEntity.ok("Customer Details Save Successfully");}
		
}
	@GetMapping("/getCustomerDetail")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HOTEL')")
	public List<Customer>getCustomerDetail(){
		return customerRepo.findAll();
	}
	
	
	@GetMapping("/getInactiveCustomerDetail")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<Customer>getInactiveCustomerDetail(){
		return customerRepo.findInactiveCustomer();
	}
	
	
//	@GetMapping("/getCustomerById/{id}")
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HOTEL') ")
//	public Optional<Customer>getCustomerById(@PathVariable Long id){
//		return customerRepo.findById(id);
//	}
	
	@GetMapping("/getPersonalDataOfCustomer")
	public Optional<Customer>getPersonalDataOfCustomer(){
		
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String cUsername=((UserDetailsImpl)principal).getUsername();
		return customerRepo.findByUsername(cUsername);
		
		
	}
	
	
	@GetMapping("/getCustomerByUsername/{username}")
	public Optional<Customer>getCustomerByUsername(@PathVariable String username){
		return customerRepo.findByUsername(username);
	}
	
	
//	
//	@PutMapping("/updateCustomerDetail/{id}")
//	public ResponseEntity<?>updateCustomerDetail(@PathVariable Long id,@RequestBody Customer customer){
//		
//		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		String username=((UserDetailsImpl)principal).getUsername();
//		
//		Optional<Customer>cus=customerRepo.findById(id);
//		if(cus.isPresent()) {
//			Customer update=cus.get();
//			String cUsername=update.getUsername();
//			if(username.equals(cUsername)) {
//				
//				 customerRepo.findById(id).map(updateCustomerDetail->{
//					 
//					 if(customer.getFullname()!=null) {
//					updateCustomerDetail.setFullname(customer.getFullname());
//					 }
//					 if(customer.getAddress()!=null) {
//					updateCustomerDetail.setAddress(customer.getAddress());
//					 }
//					 if(customer.getIdType()!=null) {
//				
//					
//					updateCustomerDetail.setIdType(customer.getIdType());
//					 }
//					 if(customer.getIdNumber()!=null) {
//					updateCustomerDetail.setIdNumber(customer.getIdNumber());
//					 }
//					 if(customer.getAge()!=null) {
//					updateCustomerDetail.setAge(customer.getAge());
//					 }if(customer.getGender()!=null) {
//					updateCustomerDetail.setGender(customer.getGender());
//					 }
//					 if(customer.getPhone()!=null) {
//					updateCustomerDetail.setPhone(customer.getPhone());
//					 }
//					
//					return customerRepo.save(updateCustomerDetail);
//				}).orElseThrow(() -> new ResourceNotFoundException("Id "+id+ " not found"));
//			return ResponseEntity.ok("Customer Detail Update Successfully!");	
//			}else {
//					return ResponseEntity.ok("Customer cannot updated. ");
//				}
//
//	
//			
//		}else {
//		
//	      return ResponseEntity.ok("Customer with id "+id+" is not found");	
//		}
//						
//				
//	}
		
		
	@PutMapping("/deleteCustomerDetail/{id}")
	public ResponseEntity<?>deleteCustomerDetails(@PathVariable Long id){
		
	
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetailsImpl)principal).getUsername();
		
		Optional<Customer>a=customerRepo.findById(id);
		if(a.isPresent()) {
			Customer customer1= a.get();
			String cUsername=customer1.getUsername();
			if(username.equals(cUsername)) {
			customerRepo.deleteById(id);
			return ResponseEntity.ok("Customer Details deleted Successfully");
			}else {
				return ResponseEntity.ok("You cannot delete other users data.");
			}
			}else {
			return ResponseEntity.ok("Customer with id "+id+" is not found.");
		}
	}
}
