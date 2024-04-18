package com.nhnacademy.aiot.authentication.security;

import com.nhnacademy.aiot.authentication.adapter.UserAdapter;
import com.nhnacademy.aiot.authentication.domain.User;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * custom한 UserDetailsService 구현체입니다.
 *
 * @author jongsikk
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAdapter userAdapter;

    /**
     * @param username the username identifying the user whose data is required.
     * @return user management 서버에서 받은 user를 {@link CustomUserDetails}로 만들어 반환합니다.
     * @throws UsernameNotFoundException DB에 저장된 user가 없을 경우에 발생합니다.
     */
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
