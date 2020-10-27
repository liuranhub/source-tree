package com.unisinsight.lazytree;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);

        TestVo test = new TestVo();

        List<String> list = new ArrayList<>();
        list.add("yyyy");
        test.setList(list);

        TestVo tt = convert(test, TestVo.class);
        Integer[] arr;

        return;
    }

    public static <T> T convert(Object target , Class<T> t) {
        T result = null;
        try {
            result = t.newInstance();
            BeanUtils.copyProperties(target, result);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> List<T> convert(List<Object> targets, Class<T> t){
        List<T> result = new ArrayList<>();
        for (Object target : targets) {
            result.add(convert(target, t));
        }
        return result;
    }

    public static class TestVo{
        private List<String> list;

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
    }
}
