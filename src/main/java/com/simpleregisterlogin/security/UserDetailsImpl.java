package com.simpleregisterlogin.security;

import com.simpleregisterlogin.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private final String username;

    private final String password;

    private final List<GrantedAuthority> grantedAuthorityList;

    private final Boolean admin;

    public UserDetailsImpl(User user) {
        username = user.getUsername();
        password = user.getPassword();
        this.grantedAuthorityList = new ArrayList<>();
        this.admin = user.getAdmin();
        addRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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

    public boolean isAdmin() {
        return admin;
    }

    private void addRole() {
        if (admin) {
            grantedAuthorityList.add(new SimpleGrantedAuthority("ADMIN"));
        } else {
            grantedAuthorityList.add(new SimpleGrantedAuthority("USER"));
        }
    }
}