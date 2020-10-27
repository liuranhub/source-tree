package com.unisinsight.lazytree.cache;

import com.unisinsight.lazytree.cache.condition.*;
import com.unisinsight.lazytree.cache.tree.*;
import com.unisinsight.lazytree.config.Constant;
import com.unisinsight.lazytree.exception.NotInitException;
import com.unisinsight.lazytree.exception.OutOfMaxsizeException;
import com.unisinsight.lazytree.model.ResourceTreeModel;
import com.unisinsight.lazytree.service.FrameworkResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.unisinsight.lazytree.config.Constant.TIMER_DELAY_TIME;

public class TreeCache {

    private static Logger LOG = LoggerFactory.getLogger(TreeCache.class);

    private static ThreadLocal<Integer> currentThreadLeafCount = new ThreadLocal<>();
    private static Tree TREE;
    private static Map<String, Set<Integer>> TASK = new HashMap<>();
    private static Map<String, Integer> VIDEO_RECORD = new HashMap<>();
    private static AtomicBoolean refreshing = new AtomicBoolean(false);
    private static AtomicBoolean needRefresh = new AtomicBoolean(false);
    private static Timer refreshTimer;

    public static void _init(ResourceTreeModel.TreeNode root, List<ResourceTreeModel.TreeNode> nodes) {
        Tree tree = new Tree(TreeNodeFactory.create(root));
        for (ResourceTreeModel.TreeNode node : nodes) {
            tree.addNode(node.getParentId(), TreeNodeFactory.create(node), true);
        }

        TREE = tree;
    }

    public synchronized static void init(Map<String, Set<Integer>> taskStatus, Map<String, Integer> videoRecord){
        // 初始化之前的清理工作
        clear();

        // 初始化默认数据
        if (!CollectionUtils.isEmpty(taskStatus)) {
            TASK = taskStatus;
        }
        if (!CollectionUtils.isEmpty(videoRecord)){
            VIDEO_RECORD = videoRecord;
        }

        // 初始化定时器
        refreshTimer = new Timer();

        // 初始化数据(不可以使用定时器异步方式代替同步方式做初始化,避免出现资源树其它数据更新问题)
        _refresh();

        // 启动定时刷新任务
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (TreeCache.class) {
                    LOG.info("timer定时任务开始执行 needRefresh:{}", needRefresh.get());
                    if (needRefresh.get()) {
                        needRefresh.set(false);
                        _refresh();
                    }
                }
            }
        }, TIMER_DELAY_TIME, TIMER_DELAY_TIME);
    }

    public static void refresh() throws NotInitException{
        if (refreshTimer == null) {
            throw new NotInitException();
        }
        needRefresh.set(true);
    }

    public static TreeNode get(Integer id){
        if (TREE == null) {
            LOG.info("资源树未初始化");
            return null;
        }

        return TREE.get(id);
    }

    public static TreeNode get(String code){
        if (TREE == null) {
            LOG.info("资源树未初始化");
            return null;
        }

        return TREE.get(code);
    }

    public static void updateVideoRecord(String code, Integer status) {
        if (TREE == null) {
            LOG.info("资源树未初始化");
            return;
        }

        TREE.updateVideoRecordStatus(code, status);
        VIDEO_RECORD.put(code, status);
    }

    public static void updateTaskStatus(String code, Set<Integer> taskStatus){
        if (TREE == null) {
            LOG.info("资源树未初始化");
            return;
        }

        TREE.updateTaskStatus(code, taskStatus);
        TASK.put(code, taskStatus);
    }

    public static TreeNode getRoot(BizType condition) throws NotInitException {
        if (TREE == null) {
            throw new NotInitException();
        }

        return getChildren(TREE.getRoot().getId(), condition);
    }

    public static TreeNode getChildren(Integer id, BizType condition) throws NotInitException {
        if (TREE == null) {
            throw new NotInitException();
        }

        TreeNode node = TREE.get(id);

        if (node == null) {
            return null;
        }

        TreeNode result = TreeNodeFactory.create(node);
        List<TreeNode> children = new ArrayList<>();

        if (!CollectionUtils.isEmpty(node.getChildren())) {
            for (TreeNode child : node.getChildren()) {
                if (child.getBizType().contains(condition)) {
                    TreeNode target = TreeNodeFactory.create(child);

                    int sum = 0;
                    if (!CollectionUtils.isEmpty(child.getChildren())) {
                        for (TreeNode n : child.getChildren()) {
                            if (n.getBizType().contains(condition)) {
                                sum ++;
                            }
                        }
                    }
                    target.setSum(sum);
                    target.setTotal(leafCount(target.getId(), condition));
                    children.add(target);
                }
            }

            result.setSum(children.size());
            result.setChildren(children);
            result.setTotal(leafCount(result.getId(), condition));
        }
        return result;
    }

    private static Integer leafCount(Integer id, BizType condition) {
        int count = 0;
        TreeNode currentNode = TREE.get(id);
        if (!currentNode.getBizType().contains(condition)){
            return 0;
        }
        if (CollectionUtils.isEmpty(currentNode.getChildren())) {
            return 1;
        }

        for (TreeNode node : currentNode.getChildren()) {
            count += leafCount(node.getId(), condition);
        }
        return count;
    }

    public static List<TreeNode> searchCount(String word, Integer limit, Integer offset, BizType type){
        return TREE.searchLeaf(word, limit, offset, type);
    }

    public static int searchCount(String word, BizType type){
        return TREE.searchCount(word, type);
    }

    public static TreeNode getParent(Integer id){
        return TREE.getParent(id);
    }

    public static Tree buildSubTreeByCode(List<String> codes, BizType condition){
        if (TREE == null) {
            LOG.info("资源树为初始化");
            return null;
        }

        if (CollectionUtils.isEmpty(codes)){
            return null;
        }
        List<Integer> ids = new ArrayList<>(codes.size());
        for(String code : codes){
            TreeNode node = TREE.get(code);
            if (node != null) {
                ids.add(node.getId());
            }
        }

        return buildSubTree(ids, condition, true);
    }

    public static Tree buildSubTreeById(List<Integer> nodeIds, BizType condition){
        if (TREE == null) {
            LOG.info("资源树为初始化");
            return null;
        }

        return buildSubTree(nodeIds, condition, false);
    }

    /**
     * 通过给定节点ID生成子树，且字数包含根节点
     * */
    public static Tree buildSubTree(List<Integer> nodeIds, BizType condition, boolean reshow){
        if (CollectionUtils.isEmpty(nodeIds)) {
            return null;
        }
        currentThreadLeafCount.set(0);
        Tree newTree = null;
        for (Integer nodeId : nodeIds) {
            TreeNode currentNode = TREE.get(nodeId);

            if (newTree != null  && (newTree.contain(nodeId) || currentNode == null)) {
                continue;
            }
            Tree subTree = new Tree(TreeNodeFactory.createSimpleNode(currentNode));
            // 非叶子节点，判断是否可以生成子树
            if (new OrgNodeCondition().accord(currentNode)) {
                // 向下生成子孙节点
                boolean haveChild = downBuildSubTree(subTree, currentNode, condition, reshow);
                if (!haveChild) {
                    continue;
                }
            }
            // 叶子节点，判断是否满足条件
            else if (!accordCondition(condition, subTree.getRoot(), reshow)){
                continue;
            }
            // 向上生成父节点
            while (true) {
                TreeNode parent = currentNode.getParent();
                if (parent == null) {
                    break;
                }
                TreeNode newRoot;
                // 父节点在子树中已存在
                if (newTree != null && (newRoot = newTree.get(parent.getId())) != null) {
                    newTree.linkTree(newRoot.getId(), subTree, false);
                    break;
                } else {
                    // 创建父节点
                    newRoot = TreeNodeFactory.createSimpleNode(parent);
                    subTree.exchangeRoot(newRoot, false);
                }
                currentNode = parent;
            }

            // 初始化root节点
            // 第一个棵子树必定会初始化到根节点结束
            if (newTree == null) {
                newTree = subTree;
            }
        }
        currentThreadLeafCount.remove();
        return newTree;
    }

    private synchronized static void _refresh(){
        if (!startRefreshing()){
            LOG.info("已有线程正在刷新任务");
            return;
        }
        try {
            TREE = load();
            LOG.info("refresh资源树成功 treeId:{}", TREE.getTreeId());
        } catch (Exception e) {
            LOG.info("refresh资源树失败");
            e.printStackTrace();
        } finally {
            endRefreshing();
        }
    }

    private synchronized static boolean startRefreshing(){
        if (refreshing.get()) {
            return false;
        }
        refreshing.set(true);

        return refreshing.get();
    }

    private synchronized static void endRefreshing(){
        if (refreshing.get()) {
            refreshing.set(false);
        }
    }

    private static Tree load(){
        long start  = System.currentTimeMillis();
        ResourceTreeModel oldTree = FrameworkResourceUtils.getFullTree();

        TreeNode root = TreeNodeFactory.create(oldTree.getData().get(0));
        Tree tree = new Tree(root);

        build(oldTree.getData().get(0), tree);
        LOG.info("资源树加载时长:{}", System.currentTimeMillis() - start);

        for (Map.Entry<String, Set<Integer>> entry : TASK.entrySet()) {
            tree.updateTaskStatus(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Integer> entry : VIDEO_RECORD.entrySet()) {
            tree.updateVideoRecordStatus(entry.getKey(), entry.getValue());
        }

        return tree;
    }

    private static void build(ResourceTreeModel.TreeNode node, Tree tree){

        Constant.testResourceCode(node.getId());

        if (CollectionUtils.isEmpty(node.getChild())) {
            return;
        }

        for (ResourceTreeModel.TreeNode child : node.getChild()) {
            TreeNode newNode = TreeNodeFactory.create(child);
            tree.addNode(node.getId(), newNode, true);
            build(child, tree);
        }
    }

    private static boolean accordCondition(BizType condition, TreeNode target, boolean reshow) {
        if (!(target instanceof ChannelTreeNode)) {
            return false;
        }

        if (reshow) {
            return true;
        }

        if (currentThreadLeafCount.get() > Constant.LAZY_TREE_MAXSIZE) {
            throw new OutOfMaxsizeException("超过最大限制个数");
        }

        // 有实时图片流任务返回false
        if (condition.equals(BizType.image) && TaskTypeCondition.image.accord(target)){
            return false;
        }

        // 有实时视频流任务返回false
        if (condition.equals(BizType.video) && TaskTypeCondition.video.accord(target)){
            return false;
        }

        // 没有录像任务返回false
        if (condition.equals(BizType.video_record) && !TaskTypeCondition.video_record.accord(target)){
            return false;
        }

        // condition为空默认不做条件限制
        if (target.getBizType().contains(condition)) {
            currentThreadLeafCount.set(currentThreadLeafCount.get() + 1);
            return true;
        }

        return false;
    }

    private static boolean downBuildSubTree(Tree tree, TreeNode currentNode, BizType condition, boolean reshow){
        if (!CollectionUtils.isEmpty(currentNode.getChildren())) {
            boolean haveChild = false;
            for (TreeNode node : currentNode.getChildren()) {
                Tree subTree = new Tree(TreeNodeFactory.createSimpleNode(node));
                boolean success = downBuildSubTree(subTree,  node, condition, reshow);
                if (success) {
                    tree.linkTree(currentNode.getId(),  subTree, false);
                    haveChild = true;
                }
            }
            return haveChild;
        } else {
            return accordCondition(condition, currentNode, reshow);
        }
    }

    private static void clear(){
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }
        TREE = null;

        VIDEO_RECORD.clear();
        TASK.clear();
    }

}
