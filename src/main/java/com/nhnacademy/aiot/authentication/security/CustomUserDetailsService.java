package com.nhnacademy.aiot.authentication.security;

import com.nhnacademy.aiot.authentication.adapter.UserAdapter;
import com.nhnacademy.aiot.authentication.domain.User;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAdapter userAdapter;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
             user = userAdapter.getUser(username);
        } catch (FeignException e) {
            throw new UsernameNotFoundException("could not found user!");
        }

        return new CustomUserDetails(user);
    }
}
