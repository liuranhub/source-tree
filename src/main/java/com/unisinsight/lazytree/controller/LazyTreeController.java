package com.unisinsight.lazytree.controller;

import com.unisinsight.lazytree.cache.TreeCache;
import com.unisinsight.lazytree.cache.condition.BizType;
import com.unisinsight.lazytree.cache.tree.Tree;
import com.unisinsight.lazytree.cache.tree.TreeNode;
import com.unisinsight.lazytree.model.RequestModel;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/lazy-tree")
public class LazyTreeController {

    @GetMapping(value = "/{id}/children")
    public TreeNode getChildren(@PathVariable Integer id,
                                @RequestParam(required = false, defaultValue = "common") BizType type){
        return TreeCache.getChildren(id, type);
    }

    @GetMapping(value = "/root/children")
    public TreeNode getChildren(@RequestParam(required = false, defaultValue = "common") BizType type){
        return TreeCache.getRoot(type);
    }

    @PostMapping(value = "/select-tree")
    public TreeNode build(@RequestBody RequestModel req){

        if (CollectionUtils.isEmpty(req.getInclude().getIds())) {
            return null;
        }
        Tree tree = TreeCache.buildSubTree(req.getInclude().getIds(), req.getType());

        return tree == null ? new TreeNode() : tree.getRoot();
    }

    @PostMapping(value = "/{code}/videoRecord/{status}")
    public void updateVideoRecord(@PathVariable String code, @PathVariable Integer status){
        TreeCache.updateVideoRecord(code, status);
    }

    @PostMapping(value = "/{code}/haveTask/{status}")
    public void updateHaveTask(@PathVariable String code, @PathVariable Integer status){
        Set<Integer> tasks = new HashSet<>();
        tasks.add(status);
        TreeCache.updateTaskStatus(code, tasks);
    }

    @PostMapping(value = "refresh")
    public void refresh(){
        TreeCache.refresh();
    }

    @PostConstruct
    public void test(){
        TreeCache.init(null, null);
    }
}
