package com.tradeshift.juliofalbo.challenge.tradeshift.builder;

import com.tradeshift.juliofalbo.challenge.tradeshift.dto.TreeResponse;
import com.tradeshift.juliofalbo.challenge.tradeshift.entity.Tree;
import com.tradeshift.juliofalbo.challenge.tradeshift.service.TreeService;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class TreeResponseBuilder {

    private Tree tree = null;

    private TreeService treeService;

    public static TreeResponseBuilder init(@NonNull Tree tree, @NonNull TreeService treeService) {
        return new TreeResponseBuilder(tree, treeService);
    }

    public TreeResponse build() {
        return new TreeResponse(this.tree.getId(), calculateHeightRecursive(this.tree));
    }

    public TreeResponse safeBuild() {
        return new TreeResponse(this.tree.getId(), calculateHeightIterative(this.tree));
    }

    /**
     * The simplest way to do this, however, is susceptible to {@link StackOverflowError} if the tree structure is too large
     *
     * @param tree
     * @return int
     */
    private int calculateHeightRecursive(@NonNull Tree tree) {
        if (!tree.getIdParent().isPresent()) {
            return 0;
        }
        int height = 1;
        if (tree.getIdParent().isPresent()) {
            height += calculateHeightRecursive(treeService.findById(tree.getIdParent().get()));
        }
        return height;
    }

    /**
     * Using the way of recursion, the algorithm is susceptible to a {@link StackOverflowError}, so I used the {@link Queue} interface to avoid this error
     *
     * @param tree
     * @return int
     */
    private int calculateHeightIterative(@NonNull Tree tree) {
        if (!tree.getIdParent().isPresent()) {
            return 0;
        }
        AtomicInteger height = new AtomicInteger(0);
        final Queue<Tree> queue = new LinkedList<>();
        queue.offer(tree);
        while (!queue.isEmpty()) {
            final Tree currentNode = queue.poll();
            currentNode.getIdParent().ifPresent(s -> {
                height.getAndIncrement();
                queue.offer(treeService.findById(currentNode.getIdParent().get()));
            });
        }
        return height.get();
    }

}
