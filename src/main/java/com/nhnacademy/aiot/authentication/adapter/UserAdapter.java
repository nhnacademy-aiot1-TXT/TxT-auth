package com.nhnacademy.aiot.authentication.adapter;

import com.nhnacademy.aiot.authentication.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value="user-management", path="/api/user/myPage")
public interface UserAdapter {
    @GetMapping
    User getUser(@RequestHeader("X-USER_ID") String id);
}
