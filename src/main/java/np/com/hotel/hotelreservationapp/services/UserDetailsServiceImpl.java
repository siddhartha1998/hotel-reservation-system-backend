package np.com.hotel.hotelreservationapp.services;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import np.com.hotel.hotelreservationapp.model.UsersAuthentication;
import np.com.hotel.hotelreservationapp.repository.UsersAuthenticationRepository;




@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UsersAuthenticationRepository userAuthenticationRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UsersAuthentication user = userAuthenticationRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

		return UserDetailsImpl.build(user);
	}

}
