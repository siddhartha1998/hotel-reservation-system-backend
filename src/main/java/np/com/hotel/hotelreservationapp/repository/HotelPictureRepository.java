package np.com.hotel.hotelreservationapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import np.com.hotel.hotelreservationapp.model.HotelPicture;

public interface HotelPictureRepository extends JpaRepository<HotelPicture, Long> {

	@Query(value="select * from tbl_hotel_picture where hotel_id=?1", nativeQuery=true)
	List<HotelPicture> findByHotelId(Long id);

}
