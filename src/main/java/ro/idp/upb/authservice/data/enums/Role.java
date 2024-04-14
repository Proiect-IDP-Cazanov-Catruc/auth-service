package ro.idp.upb.authservice.data.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static ro.idp.upb.authservice.data.enums.Permission.*;

@RequiredArgsConstructor
@Getter
public enum Role {

    USER(Collections.emptySet(), "USER"),
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            ),
            "ADMIN"
    ),
    MANAGER(
            Set.of(
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            ),
            "MANAGER"
    )

    ;

    private final Set<Permission> permissions;
    @Getter
    private final String name;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = new ArrayList<>(getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .toList());
        authorities.add(new SimpleGrantedAuthority(this.name()));
        return authorities;
    }

}