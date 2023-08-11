package nlu.dacn.dacn_backend.repository;

import nlu.dacn.dacn_backend.entity.OrderTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderTest, Long> {
    Optional<OrderTest> findOrderTestById(Long id);
}
