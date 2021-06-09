package np.com.hotel.hotelreservationapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import np.com.hotel.hotelreservationapp.model.UsersAuthentication;

@Repository
public interface UsersAuthenticationRepository extends JpaRepository<UsersAuthentication, Long>{
	
	Optional<UsersAuthentication> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	@Query(value="SELECT * FROM tbl_users_authentication",nativeQuery = true)
	List<UsersAuthentication> findRegisterdHotel();
	
	@Modifying
	@Query(value="update tbl_users_authentication set active=" +false+" where user_id=?1",nativeQuery=true)
	public void deleteById(Long id);

	@Query(value="select * from tbl_users_authentication where is_enabled= 1 and active= 1",nativeQuery=true)
	List<UsersAuthentication> findRegisterdHotelOnly();


}
