package np.com.hotel.hotelreservationapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import np.com.hotel.hotelreservationapp.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
	
	
	@Modifying
	@Query(value="update tbl_room set active="+false+" where id=?1",nativeQuery=true)
	public void deleteById(Long id);

	@Query(value="SELECT * FROM tbl_room WHERE active='1'",nativeQuery = true)
	public List<Room> getAvailableRoom();

	@Query(value="SELECT * FROM tbl_room ORDER BY (roomPrice) ASC", nativeQuery=true)
    List<Room> getRoomByPriceInAscOrder();


    @Query(value="SELECT * FROM tbl_room ORDER BY (roomPrice) DESC", nativeQuery=true)
    List<Room> getRoomByPriceInDescOrder();

    @Query(value="SELECT * FROM tbl_room WHERE active='1' AND room_number=?1 AND hotel_id=?2",nativeQuery = true)
	public Optional<Room> getRoomDetail(Long roomNumber,Long hotlId);

	@Query(value="select * from tbl_room where active='1' and hotel_username=?1",nativeQuery = true)
	public List<Room> findByHoteUserName(String hotelUserName);

	@Query(value="select room_number from tbl_room where active='1' and hotel_id=?1",nativeQuery = true)
	public Long[] getDetailByhotelId(Long hotelId);

	@Query(value="select * from tbl_room where active='0'", nativeQuery = true)
	public List<Room> getUnavailableRoom();

	@Query(value="select * from tbl_room where active='1' and availability='1' and hotel_id=?1", nativeQuery = true)
	public List<Room> findRoomByHotelId(Long id);

	@Query(value="select * from tbl_room where active='1' and availability='1'", nativeQuery = true)
	public List<Room> findNonReservedRoom();

	@Query(value="select * from tbl_room where active = '1' and hotel_id=?1 and room_number=?2", nativeQuery = true)
	public boolean findByHotelIdAndRoomNumber(Long id, boolean b);

	@Query(value="select * from tbl_room where active='1' and hotel_id=?1",nativeQuery=true)
	public List<Room> findRoomOfMyHotel(Long id);

	@Query(value="select * from tbl_room where active='0' and hotel_id=?1", nativeQuery=true)
	public List<Room> findInactiveRoomOfMyHotel(Long id);

	@Query(value="select * from tbl_room where active='1' and availability='1' and hotel_id=?1", nativeQuery = true)
	public List<Room> getAvailableRoomOfMyHotel(Long id);

	@Query(value="select * from tbl_room where active ='1' and availability='1'", nativeQuery=true)
	public Optional<Room> findByRoomNumber(Long roomNumber);


}

