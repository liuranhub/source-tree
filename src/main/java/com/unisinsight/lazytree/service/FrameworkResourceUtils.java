package com.unisinsight.lazytree.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.unisinsight.lazytree.config.Constant;
import com.unisinsight.lazytree.model.ResourceTreeModel;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FrameworkResourceUtils {

    private static RestTemplate restTemplate = new RestTemplate();

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(getDefaultInterceptor(Constant.USER_ADMIN, Constant.USER_ADMIN));
        restTemplate.setInterceptors(interceptors);

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000);
        factory.setReadTimeout(60000);

        restTemplate.setRequestFactory(factory);
    }

    public static ResourceTreeModel getFullTree(){
        String path = Constant.UUV_URL + Constant.RESOURCE_TREE;
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

    private static ClientHttpRequestInterceptor getDefaultInterceptor(String userCode, String userName){
        return (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User", "usercode:" + userCode + "&username:" + userName);
            return execution.execute(request, body);
        };
    }

}
