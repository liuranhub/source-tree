package com.unisinsight.lazytree.controller;

import com.unisinsight.lazytree.cache.TreeCache;
import com.unisinsight.lazytree.cache.condition.Condition;
import com.unisinsight.lazytree.cache.condition.TypeCondition;
import com.unisinsight.lazytree.cache.condition.VideoRecordCondition;
import com.unisinsight.lazytree.cache.tree.Tree;
import com.unisinsight.lazytree.cache.tree.TreeNode;
import com.unisinsight.lazytree.cache.tree.TreeNodeFactory;
import com.unisinsight.lazytree.model.RequestModel;
import com.unisinsight.lazytree.model.ResourceTreeModel;
import com.unisinsight.lazytree.service.FrameworkResourceService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lazy-tree")
public class LazyTreeController {

    @Resource
    private FrameworkResourceService frs;

    @GetMapping(value = "/{id}/children")
    public TreeNode getChildren(@PathVariable Integer id){
        return TreeCache.getChildren(id, 1);
    }

    @GetMapping(value = "/root/children")
    public TreeNode getChildren(){
        return TreeCache.getRoot();
    }

    @PostMapping(value = "/select-tree")
    public TreeNode build(@RequestBody RequestModel req){

        if (CollectionUtils.isEmpty(req.getInclude().getIds())) {
            return null;
        }

        List<Condition> conditions = new ArrayList<>();

        if (!CollectionUtils.isEmpty(req.getInclude().getTypes())) {
            conditions.addAll(TypeCondition.get(req.getInclude().getTypes()));
        }

        if (req.isVideoRecord()) {
            conditions.add(new VideoRecordCondition());
        }

//        if (!req.isHaveTask()) {
//            conditions.add(new HaveTaskCondition());
//        }

        Tree tree = TreeCache.buildSubTree(req.getInclude().getIds(), conditions);

        return tree == null ? new TreeNode() : tree.getRoot();
    }

    @PostMapping(value = "/{id}/videoRecord/{status}")
    public void updateVideoRecord(@PathVariable Integer id, @PathVariable Integer status){
        TreeCache.updateVideoRecord(id, status);
    }

    @PostMapping(value = "/{id}/haveTask/{status}")
    public void updateHaveTask(@PathVariable Integer id, @PathVariable Integer status){
        TreeCache.updateHaveTask(id, status);
    }

    @PostConstruct
    public void test(){
        ResourceTreeModel oldTree = frs.getFullTree();

        TreeNode root = TreeNodeFactory.create(oldTree.getData().get(0));
        TreeCache.init(root);

        build(oldTree.getData().get(0));
    }

    private void build(ResourceTreeModel.TreeNode node){

        if (CollectionUtils.isEmpty(node.getChild())) {
            return;
        }

        for (ResourceTreeModel.TreeNode child : node.getChild()) {
            TreeNode newNode = TreeNodeFactory.create(child);
            TreeCache.addNode(node.getId(), newNode);
            build(child);
        }
    }
}
