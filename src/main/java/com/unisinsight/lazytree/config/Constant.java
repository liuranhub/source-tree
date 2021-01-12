package com.unisinsight.lazytree.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Properties;

public class Constant {

    private static Logger LOG = LoggerFactory.getLogger(Constant.class);

    public static String RESOURCE_TREE = "/api/infra-uuv/v0.1/resources/tree" +
            "?identity=UDM&type=6_1,6_4,6_8,13_1,20_1";
//    public static String RESOURCE_TREE = "/api/infra-uuv/v0.1/resources/lazytree?identity=UDM&nodeType=6_1,6_4,6_8,13_1,20";

    public static String UUV_URL;

    public static String USER_ADMIN = "admin";

    public static Integer LAZY_TREE_MAXSIZE = 3000;

    public static Integer TIMER_DELAY_TIME = 30000;

    public static Integer TEST_RESOURCE_ID = null;

    public static void testResourceCode(Integer id){
        if (TEST_RESOURCE_ID == null) {
            return;
        }

        if (TEST_RESOURCE_ID.equals(id)) {
            LOG.info("{} id 存在", id);
        }
    }

    static {
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(
                    new ClassPathResource("application.properties"));
            String maxSize = properties.getProperty("lazytree.maxsize");
            if(maxSize != null) {
                LAZY_TREE_MAXSIZE = Integer.parseInt(maxSize);
            }


            String sourceApi = properties.getProperty("lazytree.sourceapi");
            if (!StringUtils.isEmpty(sourceApi)){
                RESOURCE_TREE = sourceApi;
            }

            String uuvUrl = properties.getProperty("framework.uuv.url");
            if (!StringUtils.isEmpty(uuvUrl)) {
                UUV_URL = uuvUrl;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
