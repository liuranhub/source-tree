package com.unisinsight.tree.service.impl;

import com.unisinsight.tree.service.FrameworkResourceService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service
public class FrameworkResourceServiceImpl implements FrameworkResourceService {

    @Resource
    private RestTemplate restTemplate;



}
