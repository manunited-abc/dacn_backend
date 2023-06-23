package nlu.dacn.dacn_backend.repository;

import nlu.dacn.dacn_backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
