package com.mtran.common.IdentityClient;

import com.mtran.common.config.FeignClientInterceptor;
import com.mtran.common.dto.RoleDto;
import com.mtran.common.dto.UserDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;

@FeignClient(name = "iam-service", url = "http://localhost:8080/api/users",configuration = FeignClientInterceptor.class)
public interface IamFeignClient {
    @GetMapping("/{keycloakId}")
    @CircuitBreaker(name = "iamCircuitBreaker", fallbackMethod = "fallbackGetUser")
    UserDTO getUser(@PathVariable("keycloakId") String keycloakId);

    @GetMapping("/{keycloakId}/roles")
    @CircuitBreaker(name = "iamCircuitBreaker", fallbackMethod = "fallbackGetUserRoles")
    List<RoleDto> getUserRoles(@PathVariable("keycloakId") String keycloakId);

    default UserDTO fallbackGetUser(String keycloakId, Throwable t) {
        System.out.println("Fallback triggered for getUser, keycloakId: " + keycloakId + ", error: " + t.getMessage());
        return new UserDTO();
    }
    default List<RoleDto> fallbackGetUserRoles(String keycloakId, Throwable t) {
        System.out.println("Fallback triggered for getUserRoles, keycloakId: " + keycloakId + ", error: " + t.getMessage());
        return Collections.emptyList();
    }
}
