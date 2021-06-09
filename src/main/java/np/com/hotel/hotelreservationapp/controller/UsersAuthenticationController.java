package np.com.hotel.hotelreservationapp.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import net.bytebuddy.utility.RandomString;
import np.com.hotel.hotelreservationapp.jwt.JwtUtils;
import np.com.hotel.hotelreservationapp.model.ERole;
import np.com.hotel.hotelreservationapp.model.Role;
import np.com.hotel.hotelreservationapp.model.UsersAuthentication;
import np.com.hotel.hotelreservationapp.payload.request.AddUserRequest;
import np.com.hotel.hotelreservationapp.payload.request.LoginRequest;
import np.com.hotel.hotelreservationapp.payload.response.JwtResponse;
import np.com.hotel.hotelreservationapp.payload.response.MessageResponse;
import np.com.hotel.hotelreservationapp.repository.RoleRepository;
import np.com.hotel.hotelreservationapp.repository.UsersAuthenticationRepository;
import np.com.hotel.hotelreservationapp.services.EmailService;
import np.com.hotel.hotelreservationapp.services.OtpService;
import np.com.hotel.hotelreservationapp.services.UserDetailsImpl;



@RestController
@RequestMapping("/api/auth")
public class UsersAuthenticationController {
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UsersAuthenticationRepository authRepo;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	public OtpService otpService;

	@Autowired
	public EmailService emailService;
	
	@PostMapping("/signup")
	public ResponseEntity<?> addUser(@RequestBody AddUserRequest add)
	{
		
		
		if (authRepo.existsByUsername(add.getUsername())) {
			return ResponseEntity.ok(new MessageResponse("Error: Username is already taken!"));
		}

		if (authRepo.existsByEmail(add.getEmail())) {
			return ResponseEntity.ok(new MessageResponse("Error: Email is already in use!"));
			
		}
		
			UsersAuthentication u1= new UsersAuthentication();
			u1.setUsername(add.getUsername());
			u1.setEmail(add.getEmail());
			u1.setPassword(encoder.encode(add.getPassword()));
				
			
			Set<String> strRoles=add.getRole();
			Set<Role> roles=new HashSet<>();
			
			
			if (strRoles == null) {
				Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
				roles.add(customerRole);
			} else {
				strRoles.forEach(roless -> {
					switch (roless) {
					case "admin":
						Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(adminRole);
						
					

						break;
					case "hotel":
						Role hotelRole = roleRepository.findByName(ERole.ROLE_HOTEL)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(hotelRole);

						break;
					default:
						Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(customerRole);
					}
				});
			}
					
					u1.setRoles(roles);
					
					String username=add.getUsername();
					String email=add.getEmail();
					int otp = otpService.generateOTP(username);

					String message="Hello "+username+",use this otp to validate your email: "+String.valueOf(otp);

					try {
						emailService.sendOtpMessage(email, "OTP to validate email",message);
						 authRepo.save(u1);
						
						 
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return ResponseEntity.ok(new MessageResponse("New User Registered Successfully"));
	
				
}
	
	@PostMapping("/signin")	
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetailsImpl)principal).getUsername();
		
		Optional<UsersAuthentication>user=authRepo.findByUsername(username);
		UsersAuthentication u=user.get();
		String email=u.getEmail();
		
		boolean isEnabled=u.isEnabled();
		if(isEnabled==true) {
			//return ResponseEntity.ok(new MessageResponse("Login successful.............."));
			
			return ResponseEntity.ok(new JwtResponse(jwt,
													userDetails.getUserId(),
													userDetails.getUsername(),
													userDetails.getEmail(),
													roles
													));
												
		//return ("Login successfully and jwt token for this user is====   "+jwt);
		}else {
//			String clickme = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/email/generateOtp/")
//					.path(loginRequest.getUsername()).toUriString();
			
			int otp = otpService.generateOTP(username);

			String message="Hello "+username+",use this otp to validate your email: "+String.valueOf(otp);

			try {
				emailService.sendOtpMessage(email, "OTP to validate email",message);

			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				 e.printStackTrace();
			}
			
			return ResponseEntity.ok(new MessageResponse("your email is not verfied"));
		}
	}
	

	@PostMapping("/validateOtp/{username}")
	public ResponseEntity<?> validateOtp(@RequestParam("otpnum") int otpnum,@PathVariable String username){



	    final String fail = "Entered Otp is NOT valid. Please Retry!";
	    
	    Optional<UsersAuthentication> userDetail=authRepo.findByUsername(username);
	    UsersAuthentication u=userDetail.get();
	    String email=u.getEmail();


	    //Validate the Otp 
	    if(otpnum > 0){


	       int serverOtp = otpService.getOtp(username);
	        if(serverOtp > 0){
	            if(otpnum == serverOtp){
	                otpService.clearOTP(username);
	                
	                
	                String message="Hello"+username+". Your Username is verified now you can loggedIn.";
	                
	                
	                authRepo.findByUsername(username).map(toEnable ->{
	                	toEnable.setEnabled(true);
	                	
	                	return authRepo.save(toEnable);
	                });
	                
	                return ResponseEntity.ok(new MessageResponse(message));
	            } 
	            else {
	                return ResponseEntity.ok(new MessageResponse("Otp did not match"));
	            }
	        }else {
	            return ResponseEntity.ok(new MessageResponse("Otp associated with your username not found"));
	        }
	    }else {
	        return ResponseEntity.ok(new MessageResponse(fail));
	    }
	}


}
				
	
