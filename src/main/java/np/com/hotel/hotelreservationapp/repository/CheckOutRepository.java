package np.com.hotel.hotelreservationapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import np.com.hotel.hotelreservationapp.model.CheckOut;

@Repository
public interface CheckOutRepository extends JpaRepository<CheckOut, Long> {

}
