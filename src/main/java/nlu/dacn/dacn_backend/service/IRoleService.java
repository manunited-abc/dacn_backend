package nlu.dacn.dacn_backend.service;

import nlu.dacn.dacn_backend.entity.Role;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface IRoleService {
    Optional<Role> findByCode(String code);
}
