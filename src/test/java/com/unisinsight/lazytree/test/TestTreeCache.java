package com.unisinsight.lazytree.test;

import com.unisinsight.lazytree.cache.TreeCache;
import com.unisinsight.lazytree.cache.condition.BizType;
import com.unisinsight.lazytree.cache.tree.TreeNode;
import com.unisinsight.lazytree.model.ResourceTreeModel;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TestTreeCache {
    /**
     * |-1(0_0)
     *     #设备下挂载通道
     *     |-11(0_0)
     *         |-111(1_1)
     *             |-1111(6_1)
     *             |-1112(6_4)
     *             |-1113(6_8)
     *     #设备下挂载通道
     *     |-12(1_1)
     *         |-121(6_1)
     *     #级联设备
     *     |-13(0_0)*
     *             |-131(1_1)*
     *                 |-1311(6_1)*
     *                 |-1312(6_4)*
     *                 |-1313(6_8)*
     *     |-14(0_0)
     *     #卡扣下挂载设备、通道
     *     |-15(0_0)
     *             |-151(5_2)
     *                 |-1511(1_1)
     *                     |-15111(6_1)
     *                 |-1512(1_1)
     *                     |-15121(6_4)
     *                 |-1513(1_1)
     *                     |-15131(6_8)
     *             |-152(5_3)
     *                 |-1521(1_1)
     *                     |-15211(6_1)
     *             #门禁设备
     *             |-153(13_1)
     *                 |-1531(6_5)
     *     # 采集器
     *     |-16(0_0)
     *         |-161(20_1)
     *             |-1611(6_5)
     * */
    private List<ResourceTreeModel.TreeNode> initTree(){
        List<ResourceTreeModel.TreeNode> nodes = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource("source");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (StringUtils.isEmpty(line) || line.startsWith("#")){
                    continue;
                }

                String num = line.substring(line.indexOf("|-") + 2, line.indexOf("("));
                String type =line.substring(line.indexOf("(") + 1, line.indexOf(")"));

                Integer id = Integer.parseInt(num);
                Integer pid = id / 10;
                String t1 = type.split("_")[0];
                String t2 = type.split("_")[1];

                ResourceTreeModel.TreeNode node = new ResourceTreeModel.TreeNode();
                node.setId(id);
                node.setParentId(pid);
                node.setType(t1);
                node.setSubType(t2);
                node.setResourceName(line);
                node.setResourceCode(UUID.randomUUID().toString());
                if (line.endsWith("*")) {
                    node.setCascadedId("1");
                } else {
                    node.setCascadedId("0");
                }

                node.setStatus("1");
                nodes.add(node);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ResourceTreeModel.TreeNode root = nodes.get(0);
        nodes.remove(root);

        TreeCache._init(root, nodes);

        return nodes;
    }

//    @Before
    public void before(){
        List<ResourceTreeModel.TreeNode> nodes = initTree();

        for (ResourceTreeModel.TreeNode node : nodes) {
            assert TreeCache.get(node.getId()) != null;
        }
    }

//    @Test
    public void testGetChildrenCommon(){
//        List<Integer> target = target(11, 12, 13, 14, 15, 16);
//
////        assert TreeCache.getRoot(BizType.common) != null;
//
//        //测试获取空节点
//        assert TreeCache.get(0) == null;
//
//        //测试获取根节点
////        TreeNode node = TreeCache.getChildren(1, BizType.common);
//        assert node != null;
//
//        assert equal(target, getAllIds(node.getChildren()));
    }

//    @Test
    public void testGetChildrenImage(){
//        List<Integer> target = target( 13, 15, 16);
//        TreeNode node = TreeCache.getChildren(TreeCache.getRoot(BizType.image).getId(), BizType.image);
//        assert node != null;
//        assert equal(target, getAllIds(node.getChildren()));
    }

    public void testGetChildrenVideo(){

    }

    public void testGetChildrenVideoRecord(){

    }

    private List<Integer> getAllIds(List<TreeNode> nodes) {
        return nodes.stream().map(node -> node.getId()).collect(Collectors.toList());
    }


    private boolean equal(List<Integer> source, List<Integer> target){
        return source.containsAll(target) && target.containsAll(source);
    }

    private List<Integer> target(Integer ... val){
        List<Integer> list = new ArrayList<>();
        Collections.addAll(list, val);
        return list;
    }


}
