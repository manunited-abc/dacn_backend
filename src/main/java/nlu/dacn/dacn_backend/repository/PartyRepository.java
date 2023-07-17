package nlu.dacn.dacn_backend.repository;

import nlu.dacn.dacn_backend.entity.Party;
import nlu.dacn.dacn_backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyRepository extends JpaRepository<Party,Long> {
    public List<Party> findByType(String type);
}
