package np.com.hotel.hotelreservationapp.controller;

import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import np.com.hotel.hotelreservationapp.exception.ResourceNotFoundException;
import np.com.hotel.hotelreservationapp.model.UsersAuthentication;
import np.com.hotel.hotelreservationapp.repository.UsersAuthenticationRepository;
import np.com.hotel.hotelreservationapp.services.EmailService;
import np.com.hotel.hotelreservationapp.services.OtpService;
import np.com.hotel.hotelreservationapp.utils.AppConstants;

@RestController
@RequestMapping("/api/email")
public class EmailVerificationController {

	@Autowired
	public OtpService otpService;

	@Autowired
	public EmailService emailService;
	
	@Autowired
	UsersAuthenticationRepository authRepo;
	
	
	@PostMapping("/generateOtp/{username}")
	public String generateOTP(@PathVariable String username, @RequestBody UsersAuthentication user){
		
		//getting username from user manually
		//String username=user.getUsername();
		
		if(authRepo.existsByUsername(username)){
			
			//getting email from user manually
			String email=user.getEmail();
			
			Optional<UsersAuthentication>userDetail=authRepo.findByUsername(username);
			UsersAuthentication u=userDetail.get();
			String validEmail=u.getEmail();
			
			if(email.equals(validEmail)) {
				
				
				int otp = otpService.generateOTP(username);

				String message="Hello "+username+",use this otp to validate your email: "+String.valueOf(otp);

				try {
					emailService.sendOtpMessage(email, "OTP to validate email",message);

				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					 e.printStackTrace();
				}
				
				String clickme = ServletUriComponentsBuilder.fromCurrentContextPath().path(AppConstants.PATH_FOR_EMAIL_VERIFICATION)
						.path(username).toUriString();
				
				return clickme;
	
			}else {
				return "The email you provided for registration did not match this email.";
			}
		}else {
			return "This username does not exist";
		}
	}
	
	@PostMapping("/emailVerification/{username}")
	public String emailVerification(@RequestParam("otpnum") int otpnum,@PathVariable String username){
		
		//Validate the Otp 
		if(otpnum >= 0){

			int serverOtp = otpService.getOtp(username);
			if(serverOtp > 0){
				if(otpnum == serverOtp){
					otpService.clearOTP(username);

						 authRepo.findByUsername(username).map(enableUser->{
							enableUser.setEnabled(true);
							return authRepo.save(enableUser);
						}).orElseThrow(() -> new ResourceNotFoundException("Username " + username + " not found"));

					return ("Email verified you can log into the system now.");
				} 
				else {
					return "Otp did not match";
				}
			}else {
				return "No otp found in the server for "+username;
			}
		}else {
			return "No otp passed by user.";
		}
	}
}
