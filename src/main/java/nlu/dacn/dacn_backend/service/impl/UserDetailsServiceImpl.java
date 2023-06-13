package nlu.dacn.dacn_backend.service.impl;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.entity.Account;
import nlu.dacn.dacn_backend.repository.AccountRepository;
import nlu.dacn.dacn_backend.security.useprincal.UserPrinciple;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> accountOptional = accountRepository.findByUserName(username);
        if (accountOptional.isPresent()) {
            return UserPrinciple.build(accountOptional.get());
        }
        throw new RuntimeException("Tài khoản hoặc mật khẩu không đúng!");
    }
}
