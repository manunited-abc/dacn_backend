package nlu.dacn.dacn_backend.repository;

import nlu.dacn.dacn_backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserName(String usename);

    Optional<Account> findByEmail(String email);

    @Query(value = "SELECT MAX(id) FROM account", nativeQuery = true)
    Long findMaxId();

}
