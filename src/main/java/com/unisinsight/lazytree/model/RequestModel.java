package com.unisinsight.lazytree.model;

import lombok.Data;

import java.util.List;

@Data
public class RequestModel {

    private Include include;
    private boolean videoRecord;
    private boolean haveTask;

    @Data
    public static class Include{
        List<Integer> ids;
        List<String> types;
    }
}
