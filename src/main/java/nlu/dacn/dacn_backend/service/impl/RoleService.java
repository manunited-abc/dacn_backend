package nlu.dacn.dacn_backend.service.impl;

import lombok.AllArgsConstructor;
import nlu.dacn.dacn_backend.entity.Role;
import nlu.dacn.dacn_backend.repository.RoleRepository;
import nlu.dacn.dacn_backend.service.IRoleService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;

    @Override
    public Optional<Role> findByCode(String code) {
        return roleRepository.findByCode(code);
    }
}
