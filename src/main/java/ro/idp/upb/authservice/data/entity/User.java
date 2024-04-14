package ro.idp.upb.authservice.data.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ro.idp.upb.authservice.data.enums.Role;

import java.util.Collection;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User implements UserDetails {

    private UUID id;
    private String firstname;
    private String lastname;
    private String email;
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
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