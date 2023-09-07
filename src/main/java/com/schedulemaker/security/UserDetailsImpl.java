package com.schedulemaker.security;

import com.schedulemaker.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private String username;

    private String password;

    private List<GrantedAuthority> grantedAuthorityList;

    private Boolean isAdmin;

    public UserDetailsImpl(User user) {
        username = user.getUsername();
        password = user.getPassword();
        this.grantedAuthorityList = new ArrayList<>();
        this.isAdmin = user.isAdmin();
        addRole();
    }

    public UserDetailsImpl(String username, String password, Boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
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
        return isAdmin;
    }

    private void addRole() {
        if (isAdmin) {
            grantedAuthorityList.add(new SimpleGrantedAuthority("ADMIN"));
        } else {
            grantedAuthorityList.add(new SimpleGrantedAuthority("USER"));
        }
    }
}