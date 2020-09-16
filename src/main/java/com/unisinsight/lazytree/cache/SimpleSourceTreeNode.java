package com.unisinsight.lazytree.cache;

import lombok.Data;

@Data
public class SimpleSourceTreeNode {
    private int id;
    private String name;
    private int type;
    // subType
    private int st;
    // code
    private String rc;
    private int status;
    // videoRecord
    private boolean vr;
    // haveTask
    private boolean ht;
}
