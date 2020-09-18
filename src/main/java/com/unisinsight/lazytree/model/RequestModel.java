package com.unisinsight.lazytree.model;

import com.unisinsight.lazytree.cache.condition.BizType;
import lombok.Data;

import java.util.List;

@Data
public class RequestModel {

    private Include include;
    // dufault、video、image、video_record
    private BizType type = BizType.common;

    @Data
    public static class Include{
        List<Integer> ids;
        List<String> types;
    }
}
