package com.unisinsight.lazytree.model;

import com.unisinsight.lazytree.cache.condition.BizType;
import lombok.Data;

import java.util.List;

@Data
public class RequestModel {

    private Include include;
    // dufault、video、image、video_record

    @Data
    public static class Include{
        List<Integer> ids;
        List<String> codes;
        BizType type = BizType.common;
    }
}
