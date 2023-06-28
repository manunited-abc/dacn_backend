package nlu.dacn.dacn_backend.repository;

import nlu.dacn.dacn_backend.entity.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LaptopRepository extends JpaRepository<Laptop, Long> {
    @Query("Select distinct l.brand from Laptop l")
    List<String> findAllBrand();

    @Query("Select distinct l.type from Laptop l")
    List<String> findAllType();

    @Query("Select distinct l.chipCpu from Laptop l")
    List<String> findAllChipCpu();
    List<Laptop> findByProductNameContainingIgnoreCase(String productName);
}
