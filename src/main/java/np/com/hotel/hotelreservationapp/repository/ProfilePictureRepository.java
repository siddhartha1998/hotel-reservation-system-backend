package np.com.hotel.hotelreservationapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import np.com.hotel.hotelreservationapp.model.ProfilePicture;

public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, Long> {
	
	@Modifying
	@Query(value="DELETE from tbl_profile_picture  WHERE id=?1",nativeQuery = true)
	public void deleteById(Long id);
	
	
	@Query(value="select * from tbl_profile_picture where user_user_id=?1", nativeQuery = true)
	public Optional<ProfilePicture>findByUserId(Long id);

	
}
