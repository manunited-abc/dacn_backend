package nlu.dacn.dacn_backend.security.useprincal;

import lombok.ToString;
import nlu.dacn.dacn_backend.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ToString
public class UserPrinciple implements UserDetails {
    private final long serialVersionUID = 1L;
    private final Account account;
    private Collection<? extends GrantedAuthority> roles;

    public UserPrinciple(Account account, Collection roles) {
        this.account = account;
        this.roles = roles;
    }

    public static UserPrinciple build(Account account) {
        List<GrantedAuthority> authorities = account.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getCode())).collect(Collectors.toList());
        return new UserPrinciple(account, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getUserName();
    }

    public String getFullName() {
        return account.getFullName();
    }

    public Account getAccount() {
        return account;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
