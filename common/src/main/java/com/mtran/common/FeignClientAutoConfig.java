package com.mtran.common;

import com.mtran.common.IdentityClient.IamFeignClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {IamFeignClient.class})
public class FeignClientAutoConfig { }
