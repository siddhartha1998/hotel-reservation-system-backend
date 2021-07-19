package np.com.hotel.hotelreservationapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import np.com.hotel.hotelreservationapp.model.ReservationDetails;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationDetails, Long> {
 
	@Modifying
	@Query(value="update tbl_reservation_details set active= "+false+" where id=?1",nativeQuery=true)
	public void deleteById(Long id);

	//@Query(value="select * from tbl_reservation where customer_id=?1",nativeQuery = true)
	//public Optional<ReservationDetails> getByCustomerId(Long customerId);
	@Query(value="select * from tbl_reservation_details where hotel_id=?1 AND room_number=?2 AND customer_id=?3",nativeQuery = true)
	public Optional<ReservationDetails> getByCustomer(Long hotelId, Long roomNumber, Long customerId);

//	@Query(value="select * from tbl_reservation_details where active = 0 ", nativeQuery=true)
//	public List<ReservationDetails> findNonReservedRoom();

	@Query(value="select * from tbl_reservation_details where active = 1 ", nativeQuery = true)
	public List<ReservationDetails> findReservedRoom();

	@Query(value="select * from tbl_reservation_details where active = '1' and hotel_id=?1", nativeQuery=true)
	public List<ReservationDetails> findByHotelId(Long id);

	@Query(value="select * from tbl_reservation_details where active = '0' and hotel_id=?1", nativeQuery=true)
	public List<ReservationDetails> findByMyHotelId(Long id);

	@Query(value="select * from tbl_reservation_details where customer_id IN(select distinct(customer_id) from tbl_reservation_details) "
			+ "and hotel_id=?1 and active='0'", nativeQuery=true)
	public List<ReservationDetails> findDistinctCustomer(Long id);
}
