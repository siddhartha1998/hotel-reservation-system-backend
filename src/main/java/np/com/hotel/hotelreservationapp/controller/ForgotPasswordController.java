package np.com.hotel.hotelreservationapp.controller;

import java.util.Optional;


import javax.mail.MessagingException;
import javax.validation.constraints.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import net.bytebuddy.utility.RandomString;
import np.com.hotel.hotelreservationapp.exception.ResourceNotFoundException;
import np.com.hotel.hotelreservationapp.model.UsersAuthentication;
import np.com.hotel.hotelreservationapp.payload.request.ChangePasswordRequest;
import np.com.hotel.hotelreservationapp.payload.response.MessageResponse;
import np.com.hotel.hotelreservationapp.repository.UsersAuthenticationRepository;
import np.com.hotel.hotelreservationapp.services.EmailService;
import np.com.hotel.hotelreservationapp.services.OtpService;

@RestController
@RequestMapping("/api/ForgetPassword")
public class ForgotPasswordController {
	
	
	@Autowired
	private UsersAuthenticationRepository userRepository;
	
	@Autowired
	public OtpService otpService;

	@Autowired
	public EmailService emailService;

	@Autowired
	PasswordEncoder encoder;
	
	@PutMapping("/changePassword/{username}")
	public ResponseEntity<?>forgotPassword(String clickme,@PathVariable String username,@RequestBody ChangePasswordRequest changePassword){
		
		String newPassword=changePassword.getNewPassword();
		String confirmPassword=changePassword.getConfirmPassword();
		try {
		if(newPassword.equals(confirmPassword)) {
			
			userRepository.findByUsername(username).map(changePass->{
				changePass.setPassword(encoder.encode(newPassword));
				return userRepository.save(changePass);
				
			}).orElseThrow(() -> new ResourceNotFoundException("Username " + username + " not found"));
			return ResponseEntity.ok(new MessageResponse("Password Changed Successfully"));
		}
		else {
		return ResponseEntity.ok(new MessageResponse("Cannot change"));
		}
		}catch(Exception e) {
			return ResponseEntity.ok(new MessageResponse(e.getMessage()));
		}
		
	}
	
	
	@PostMapping("/generateOtp")
	public ResponseEntity<?> generateOTP(@RequestBody UsersAuthentication user){
		
		//getting username from user manually
		String username=user.getUsername();
		
	
		//getting email from user manually
		String email=user.getEmail();
		
		if(userRepository.existsByUsername(username)) {
			Optional<UsersAuthentication>u=userRepository.findByUsername(username);
			UsersAuthentication userDetail=u.get();
			//String validUsername=userDetail.getUsername();
			String validEmail=userDetail.getEmail();
			
			if(email.equals(validEmail)) {
				
				int otp = otpService.generateOTP(username);

				String message="Hello "+username+",use this otp to validate your email: "+String.valueOf(otp);

				try {
					emailService.sendOtpMessage(email, "OTP to validate email",message);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String clickme = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/ForgetPassword/validateOtp/")
						.path(username).toUriString();
				return ResponseEntity.ok(new MessageResponse("Otp is generated check your email"));
			}else {
				return ResponseEntity.ok(new MessageResponse("This email is not associated with your username"));
			}
		}else {
			return ResponseEntity.ok(new MessageResponse("This username does not exist."));
		}
		
	}
	

@PostMapping("/validateOtp/{username}")
public ResponseEntity<?> validateOtp(@RequestParam("otpnum") int otpnum,@PathVariable String username){



    final String fail = "Entered Otp is NOT valid. Please Retry!";
    
    Optional<UsersAuthentication> userDetail=userRepository.findByUsername(username);
    UsersAuthentication u=userDetail.get();
    String email=u.getEmail();


    //Validate the Otp 
    if(otpnum >= 0){


       int serverOtp = otpService.getOtp(username);
        if(serverOtp > 0){
            if(otpnum == serverOtp){
                otpService.clearOTP(username);
                
                String randomPassword=RandomString.make();
                
                String message="Hello "+username+"! This is your new password: "+randomPassword+". Change this password as soon you log into your account. Thank You!";
                

                try {
                    
                    userRepository.findByUsername(username).map(updatePassword->{
                        updatePassword.setPassword(encoder.encode(randomPassword));
                        return userRepository.save(updatePassword);
                    });
                    emailService.sendOtpMessage(email, "New Password",message);
                    
                } catch (MessagingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                return ResponseEntity.ok(new MessageResponse("Check your email. We have sent you a new password. Use that password to login into the system and update it after login."));
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



