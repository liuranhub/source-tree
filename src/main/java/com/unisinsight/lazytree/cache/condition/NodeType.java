package com.unisinsight.lazytree.cache.condition;

import com.unisinsight.lazytree.cache.tree.TreeNode;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum NodeType implements Condition{
    CHANNEL_1(6, 1),
    CHANNEL_4(6, 4),
    CHANNEL_5(6, 5),
    CHANNEL_8(6, 8),
    TOLLGATE_FACE(5, 1),
    TOLLGATE_CAR(5, 2),
    ENTRANCE_GUARD(13, 1),
    COLLECT_DEVICE(20, 1),
    DEVICE(1, 1),
    UNKNOW(-1),
    ORG(0) ;

    private Integer type;
    private Integer subType;

    private static Map<String, NodeType> map ;
    static {
        map = new HashMap<>(values().length);
        for (NodeType type : values()) {
            map.put(type.toString(), type);
        }
    }


    public Integer getType() {
        return type;
    }

    public Integer getSubType() {
        return subType;
    }

    private NodeType(int type){
        this.type = type;
    }

    private NodeType(int type, int subType) {
        this(type);
        this.subType = subType;
    }

    @Override
    public String toString(){
        if (subType == null) {
            return "" + type;
        }
        return "" + type + "_" + subType;
    }

    @Override
    public boolean accord(TreeNode treeNode) {
        return this.equals(treeNode.getNodeType());
    }

    public static NodeType get(String type, String subType) {
        String key;
        if (subType == null) {
            key = "" + type;
        } else {
            key = "" + type + "_" + subType;
        }

        NodeType result = map.get(key);
        if (result == null) {
            return UNKNOW;
        }
        return result;
    }

    public static List<NodeType> get(List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return new ArrayList<>();
        }
        List<NodeType> list = new ArrayList<>(keys.size());
        for (String key : keys) {
            list.add(map.get(key));
        }

        return list;
    }

}
