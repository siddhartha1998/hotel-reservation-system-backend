package np.com.hotel.hotelreservationapp.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import np.com.hotel.hotelreservationapp.model.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
	
	@Query(value="select * from tbl_hotel where active='1' and hotel_username=?1",nativeQuery = true)
	Optional<Hotel> findByHotelUsername(String username);

	Boolean existsByHotelUsername(String username);


	@Query(value="SELECT id, hotel_name,hotel_address,phone, description FROM tbl_hotel WHERE active='1' AND hotel_address LIKE %:hotelAddress%",nativeQuery = true)
	public ArrayList<?> findByHotelAddress(String hotelAddress);
	
	@Query(value="SELECT id, hotel_name,hotel_address,phone,description FROM tbl_hotel WHERE active='1' AND hotel_name LIKE %:hotelName%",nativeQuery = true)
	public ArrayList<?> findByHotelName(String hotelName);
	
	@Modifying
	@Query(value="update tbl_hotel set active="+false+" where id=?1",nativeQuery = true)
	public void deleteById(Long id);
	
	@Query(value="select *from tbl_hotel where active='1' and id=?1",nativeQuery = true )
	public Optional<Hotel>findById();
	
	@Query(value="select * from tbl_hotel where active='1'",nativeQuery = true)
	public List<Hotel>findAll();

	//@Query(value="select * from tbl_hotel where active='1' and user_user_id=?1",nativeQuery = true)
	@Query(value="SELECT * FROM tbl_hotel WHERE active='1' AND user_user_id = ?1 ",nativeQuery = true)
	public Optional<Hotel> getHotelDetail(Long hId);

	@Query(value="select * from tbl_hotel where active='0'",nativeQuery = true)
	List<Hotel> findInactiveHotel();

	@Query(value="select * from tbl_hotel where active='0' and id=?1", nativeQuery=true)
	public Optional<Hotel> findInactiveHotelById(Long id);
}
