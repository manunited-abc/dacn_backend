package nlu.dacn.dacn_backend.command;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.entity.Account;
import nlu.dacn.dacn_backend.entity.Role;
import nlu.dacn.dacn_backend.enumv1.RoleType;
import nlu.dacn.dacn_backend.enumv1.State;
import nlu.dacn.dacn_backend.evenlistener.AccountCreatedEvent;
import nlu.dacn.dacn_backend.repository.AccountRepository;
import nlu.dacn.dacn_backend.repository.RoleRepository;
import nlu.dacn.dacn_backend.service.IRoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final RoleRepository roleRepository;

    private final AccountRepository accountRepository;
    private final ApplicationContext applicationContext;
    private final PasswordEncoder passwordEncoder;
    private final IRoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        if (!hasDataRole()) {
            createDefaultRole();
        }
        if (!hasDataAccount()) {
            createDefaultAccount();
        }
//        if(!hasDataFacility()) {
//            createDefaultFacility();
//        }
    }

    public void createDefaultRole() {
        Role roleUser = new Role();
        roleUser.setCode("ADMIN");
        roleUser.setNameCode("QUẢN TRỊ VIÊN");
        roleUser.setCreateBy("Nguyễn Công Phúc");
        roleRepository.save(roleUser);

        Role roleAdmin = new Role();
        roleAdmin.setCode("USER");
        roleAdmin.setNameCode("NGƯỜI DÙNG");
        roleAdmin.setCreateBy("Nguyễn Công Phúc");
        roleRepository.save(roleAdmin);
    }

    public void createDefaultAccount() {
        Account admin = new Account();
        admin.setUserName("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setFullName("QUẢN TRỊ VIÊN");
        admin.setEmail("19130172@st.hcmuaf.edu.vn");
        admin.setState(State.ACTIVE);
        admin.setCreateBy("Nguyễn Công Phúc");
        Role adminRole = roleService.findByCode(RoleType.ADMIN.getCode()).get();
        Role userRole = roleService.findByCode(RoleType.USER.getCode()).get();
        List<Role> roles = new ArrayList<>();
        roles.add(adminRole);
        roles.add(userRole);
        admin.setRoles(roles);

//      Thêm tài khoản vào bảng account
        accountRepository.save(admin);
//      Thêm mới giỏ hàng cho tk admin
        applicationContext.publishEvent(new AccountCreatedEvent(admin));
    }

//    public void createDefaultFacility() {
//        Facility fac1 = new Facility();
//        Facility fac2 = new Facility();
//
//        fac1.setFacilityName("Nhà kho 001");
//        fac1.setFacilityName("Nhà kho 001");
//
//        facilityRepository.save(fac1);
//        facilityRepository.save(fac2);
//    }

    private boolean hasDataRole() {
        return !roleRepository.findAll().isEmpty();
    }

    private boolean hasDataAccount() {
        return !accountRepository.findAll().isEmpty();
    }

//    private boolean hasDataFacility() {
//        return !facilityRepository.findAll().isEmpty();
//    }
}