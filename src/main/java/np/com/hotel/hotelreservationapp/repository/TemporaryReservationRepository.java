package np.com.hotel.hotelreservationapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import np.com.hotel.hotelreservationapp.model.TemporaryReservation;

@Repository
public interface TemporaryReservationRepository extends JpaRepository<TemporaryReservation, Long> {
	  
	boolean  existsByRoomId(Long roomId);
	
	@Query(value="SELECT * FROM tbl_temporary_reservation WHERE active='1' AND reserved_by=?1",nativeQuery = true)
	public List<TemporaryReservation>findByReservedBy(String username);
	

	Object findByRoomId(Long roomId);

	Optional<TemporaryReservation> findByRoomNumber(Long roomId);
	
	//@Query(value="SELECT * FROM tbl_temporary_reservation WHERE active='1' AND reserved_by=?1",nativeQuery = true)
	//public List<?>findAll(String username);

}
