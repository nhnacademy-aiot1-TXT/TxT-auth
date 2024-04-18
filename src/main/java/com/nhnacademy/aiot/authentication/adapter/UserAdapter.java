package com.nhnacademy.aiot.authentication.adapter;

import com.nhnacademy.aiot.authentication.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * user api 서버로 회원 조회 요청을 보내는 adapter
 *
 * @author parksangwon
 * @version 1.0.0
 */
@FeignClient(value = "user-management", path = "/api/user/myPage")
public interface UserAdapter {
    /**
     * @param id 정보를 조회하고 싶은 회원 아이디
     * @return {@link User}
     */
    @GetMapping
    User getUser(@RequestHeader("X-USER-ID") String id);
}
