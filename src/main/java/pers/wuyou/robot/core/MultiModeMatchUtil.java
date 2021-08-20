package pers.wuyou.robot.core;

import java.util.*;

/**
 * @author wuyou
 */
public class MultiModeMatchUtil {
    /**
     * 是否建立了failure表
     */
    private Boolean failureStatesConstructed = false;
    /**
     * 根结点
     */
    private final Node root;

    int pos = 0;
    int lan = 0;

    private MultiModeMatchUtil() {
        this.root = new Node(true);
    }

    public MultiModeMatchUtil(String... keywords) {
        this.root = new Node(true);
        for (String keyword : keywords) {
            addKeyword(keyword);
        }
    }

    /**
     * 添加一个模式串
     *
     * @param keyword 模式串
     */
    public void addKeyword(String keyword) {
        if (keyword == null || keyword.length() == 0) {
            return;
        }
        Node currentState = this.root;
        for (Character character : keyword.toCharArray()) {
            currentState = currentState.insert(character);
        }
        currentState.addEmit(keyword);
    }

    /**
     * 模式匹配
     *
     * @param text 待匹配的文本
     * @return 匹配到的模式串
     */
    public Map<String, String> parseText(String text) {
        checkForConstructedFailureStates();
        Node currentState = this.root;
        Map<String, String> results = new HashMap<>(8);
        for (int position = 0; position < text.length(); position++) {
            Character character = text.charAt(position);
            currentState = currentState.nextState(character);
            Collection<String> emits = currentState.emit();
            if (emits == null || emits.isEmpty()) {
                continue;
            }
            for (String emit : emits) {
                if (pos + lan >= position) {
                    continue;
                }
                String value = text.substring(pos + lan, position - emit.length() + 1);
                pos = position - emit.length() + 1;
                lan = emit.length();
                results.put(emit, value);
            }
        }
        return results;
    }

    /**
     * 检查是否建立了failure表
     */
    private void checkForConstructedFailureStates() {
        if (!this.failureStatesConstructed) {
            constructFailureStates();
        }
    }

    /**
     * 建立failure表
     */
    private void constructFailureStates() {
        Queue<Node> queue = new LinkedList<>();
        // 第一步，将深度为1的节点的failure设为根节点
        //特殊处理：第二层要特殊处理，将这层中的节点的失败路径直接指向父节点(也就是根节点)。
        for (Node depthOneState : this.root.children()) {
            depthOneState.setFailure(this.root);
            queue.add(depthOneState);
        }
        this.failureStatesConstructed = true;
        // 第二步，为深度 > 1 的节点建立failure表，这是一个bfs 广度优先遍历
        while (!queue.isEmpty()) {
            Node parentNode = queue.poll();
            for (Character transition : parentNode.getTransitions()) {
                Node childNode = parentNode.find(transition);
                queue.add(childNode);
                Node failNode = parentNode.getFailure().nextState(transition);
                childNode.setFailure(failNode);
                childNode.addEmit(failNode.emit());
            }
        }
    }

    private static class Node {
        private final Map<Character, Node> map;
        private final List<String> emits;
        //输出
        private Node failure;
        //失败中转
        private Boolean isRoot = false;

        //是否为根结点
        public Node() {
            map = new HashMap<>();
            emits = new ArrayList<>();
        }

        public Node(Boolean isRoot) {
            this();
            this.isRoot = isRoot;
        }

        public Node insert(Character character) {
            Node node = this.map.get(character);
            if (node == null) {
                node = new Node();
                map.put(character, node);
            }
            return node;
        }

        public void addEmit(String keyword) {
            emits.add(keyword);
        }

        public void addEmit(Collection<String> keywords) {
            emits.addAll(keywords);
        }

        public Node find(Character character) {
            return map.get(character);
        }

        /**
         * 跳转到下一个状态
         *
         * @param transition 接受字符
         * @return 跳转结果
         */
        private Node nextState(Character transition) {
            Node state = this.find(transition);
            // 先按success跳转
            if (state != null) {
                return state;
            }
            //如果跳转到根结点还是失败，则返回根结点
            if (this.isRoot) {
                return this;
            }
            // 跳转失败的话，按failure跳转
            return this.failure.nextState(transition);
        }

        public Collection<Node> children() {
            return this.map.values();
        }

        public void setFailure(Node node) {
            failure = node;
        }

        public Node getFailure() {
            return failure;
        }

        public Set<Character> getTransitions() {
            return map.keySet();
        }

        public Collection<String> emit() {
            return this.emits == null ? Collections.emptyList() : this.emits;
        }
    }

}