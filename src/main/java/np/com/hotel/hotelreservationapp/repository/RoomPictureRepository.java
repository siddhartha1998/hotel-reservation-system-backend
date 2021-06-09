package np.com.hotel.hotelreservationapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import np.com.hotel.hotelreservationapp.model.ProfilePicture;
import np.com.hotel.hotelreservationapp.model.RoomPicture;

public interface RoomPictureRepository extends JpaRepository<RoomPicture, Long> {

	@Modifying
	@Query(value="DELETE from tbl_room_picture  WHERE id=?1",nativeQuery = true)
	public void deleteById(Long id);
	
	
//	@Query(value="select * from tbl_room_picture where user_user_id=?1", nativeQuery = true)
//	public Optional<RoomPicture>findByUserId(Long id);

	
	@Query(value="select * from tbl_room_picture where room_id=?1",nativeQuery=true)
	public List<RoomPicture> findByRoomId(Long id);


	
	
}
