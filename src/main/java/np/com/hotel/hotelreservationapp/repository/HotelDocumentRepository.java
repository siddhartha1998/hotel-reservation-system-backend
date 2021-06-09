package np.com.hotel.hotelreservationapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import np.com.hotel.hotelreservationapp.model.HotelDocument;

public interface HotelDocumentRepository extends JpaRepository<HotelDocument, Long> {

	@Query(value="select * from tbl_hotel_document where hotel_id=?1",nativeQuery=true)
	List<HotelDocument> findByHotelId(Long id);

}
