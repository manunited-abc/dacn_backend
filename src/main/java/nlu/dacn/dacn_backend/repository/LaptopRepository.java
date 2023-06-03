package nlu.dacn.dacn_backend.repository;

import nlu.dacn.dacn_backend.entity.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaptopRepository extends JpaRepository<Laptop, Long> {
}
