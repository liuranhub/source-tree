package com.unisinsight.lazytree.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.unisinsight.lazytree.config.Constant;
import com.unisinsight.lazytree.model.ResourceTreeModel;
import com.unisinsight.lazytree.service.FrameworkResourceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FrameworkResourceServiceImpl implements FrameworkResourceService {

    @Value("${framework.uuv.url}")
    private String uuv;

    private RestTemplate restTemplate = new RestTemplate();

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    @PostConstruct
    public void init(){
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(getDefaultInterceptor(Constant.USER_ADMIN, Constant.USER_ADMIN));
        restTemplate.setInterceptors(interceptors);
    }

    public ResourceTreeModel getFullTree(){
        String path = uuv + Constant.RESOURCE_TREE;
        ResponseEntity<String> entity = restTemplate.getForEntity(path, String.class);
        ResourceTreeModel result = null;
        try {
            result = mapper.readValue(entity.getBody(), new TypeReference<ResourceTreeModel>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private ClientHttpRequestInterceptor getDefaultInterceptor(String userCode, String userName){
        return (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User", "usercode:" + userCode + "&username:" + userName);
            return execution.execute(request, body);
        };
    }

}
