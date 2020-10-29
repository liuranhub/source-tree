package com.unisinsight.lazytree.controller;

import com.unisinsight.lazytree.cache.TreeCache;
import com.unisinsight.lazytree.cache.condition.BizType;
import com.unisinsight.lazytree.cache.tree.Tree;
import com.unisinsight.lazytree.cache.tree.TreeNode;
import com.unisinsight.lazytree.config.Constant;
import com.unisinsight.lazytree.exception.NotInitException;
import com.unisinsight.lazytree.model.PageResult;
import com.unisinsight.lazytree.model.RequestModel;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/lazy-tree")
public class LazyTreeController {

    @GetMapping(value = "/{id}/children")
    public TreeNode getChildren(@PathVariable Integer id,
                                @RequestParam(required = false, defaultValue = "common") BizType type){
        try {
            return TreeCache.getChildren(id, type);
        } catch (NotInitException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping(value = "/root/children")
    public TreeNode getChildren(@RequestParam(required = false, defaultValue = "common") BizType type){
        try {
            return TreeCache.getRoot(type);
        } catch (NotInitException e) {
            e.printStackTrace();
        }

        return null;
    }

    @PostMapping(value = "/select-tree")
    public TreeNode build(@RequestBody RequestModel req){

        Tree tree = null;
        if (!CollectionUtils.isEmpty(req.getInclude().getIds())){
            tree = TreeCache.buildSubTreeById(req.getInclude().getIds(), req.getInclude().getType());
        } else {
            tree = TreeCache.buildSubTreeByCode(req.getInclude().getCodes(), req.getInclude().getType());
        }

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

    @GetMapping(value = "/search")
    public PageResult search(@RequestParam(value = "word") String word,
                             @RequestParam(value = "page_num") Integer pageNum,
                             @RequestParam(value = "page_size") Integer pageSize,
                             @RequestParam(value = "type", defaultValue = "common") BizType type){
        List<TreeNode> data = TreeCache.searchCount(word, pageSize, (pageNum - 1) * pageSize,  type);
        int count = TreeCache.searchCount(word, type);

        return PageResult.of(data, pageSize, pageNum, count);
    }

    @GetMapping(value = "/{id}/parent")
    public TreeNode getParent(@PathVariable(value = "id") Integer id){
        return TreeCache.getParent(id);
    }


    @PostMapping(value = "refresh")
    public void refresh(){
        try {
            TreeCache.refresh();
        } catch (NotInitException e) {
            e.printStackTrace();
        }
    }

    @PostMapping(value = "testid/{id}")
    public void testId(@PathVariable Integer id){
        Constant.TEST_RESOURCE_ID = id;
    }


    @PostMapping(value = "test/init")
    public void test(){
        TreeCache.init(null, null);
    }
}
