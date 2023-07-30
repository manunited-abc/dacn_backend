package nlu.dacn.dacn_backend.repository;

import nlu.dacn.dacn_backend.entity.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LaptopRepository extends JpaRepository<Laptop, Long> {
    @Query("Select distinct l.brand from Laptop l")
    List<String> findAllBrand();

    @Query("Select distinct l.type from Laptop l")
    List<String> findAllType();

    @Query("Select distinct l.chipCpu from Laptop l")
    List<String> findAllChipCpu();
    List<Laptop> findByProductNameContainingIgnoreCase(String productName);
    Laptop findFirstByUrlSeo(String urlSeo);
    @Query(value = "SELECT * FROM laptop WHERE MATCH (product_name) AGAINST (:keyword) or MATCH (type) AGAINST (:keyword)" +
            "or MATCH (type) AGAINST (:keyword)  ", nativeQuery = true)
    List<Laptop> findByKeyword(@Param("keyword") String keyword);
    @Query(value = "SELECT * FROM laptop WHERE MATCH (product_name) AGAINST (:keyword)", nativeQuery = true)
    List<Laptop> findByName(@Param("keyword") String keyword);
    @Query(value = "SELECT * FROM laptop WHERE MATCH (type) AGAINST (:keyword)", nativeQuery = true)
    List<Laptop> findByType(@Param("keyword") String keyword);
    @Query("SELECT l FROM Laptop l WHERE l.brand LIKE CONCAT('%', :keyword, '%')")
    List<Laptop> findByBrand(@Param("keyword") String keyword);
    @Query("SELECT l FROM Laptop l WHERE l.chipCpu LIKE CONCAT('%', :keyword, '%')")
    List<Laptop> findByChipCpu(@Param("keyword") String keyword);
    @Query("SELECT l FROM Laptop l WHERE l.cpu LIKE CONCAT('%', :keyword, '%')")
    List<Laptop> findByCpu(@Param("keyword") String keyword);
    @Query("SELECT l FROM Laptop l WHERE l.ram LIKE :keyword%")
    List<Laptop> findByRam(@Param("keyword") String keyword);
    @Query("SELECT l FROM Laptop l WHERE l.storage LIKE CONCAT('%', :keyword, '%')")
    List<Laptop> findByStorage(@Param("keyword") String keyword);
    @Query("SELECT l FROM Laptop l WHERE l.battery LIKE CONCAT('%', :keyword, '%')")
    List<Laptop> findByBattery(@Param("keyword") String keyword);
    @Query("SELECT l FROM Laptop l WHERE l.graphics LIKE CONCAT('%', :keyword, '%')")
    List<Laptop> findByGraphics(@Param("keyword") String keyword);
    @Query(value = "SELECT * FROM laptop WHERE MATCH (color) AGAINST (:keyword)", nativeQuery = true)
    List<Laptop> findByColor(@Param("keyword") String keyword);
    @Query("SELECT l.quantity FROM Laptop l WHERE l.id = (:id)")
    int getQuantityById(@Param("id") Long id);
}
