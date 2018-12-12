package com.tradeshift.juliofalbo.challenge.tradeshift.service;

import com.tradeshift.juliofalbo.challenge.tradeshift.builder.TreeResponseBuilder;
import com.tradeshift.juliofalbo.challenge.tradeshift.dto.TreeRequest;
import com.tradeshift.juliofalbo.challenge.tradeshift.dto.TreeResponse;
import com.tradeshift.juliofalbo.challenge.tradeshift.entity.Tree;
import com.tradeshift.juliofalbo.challenge.tradeshift.exceptions.*;
import com.tradeshift.juliofalbo.challenge.tradeshift.mapper.TreeMapper;
import com.tradeshift.juliofalbo.challenge.tradeshift.repository.TreeRepository;
import com.tradeshift.juliofalbo.challenge.tradeshift.resource.TreeResource;
import com.tradeshift.juliofalbo.challenge.tradeshift.utils.PageUtils;
import lombok.NonNull;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@Service
public class TreeService {

    @Autowired
    private TreeRepository repository;

    @Autowired
    private TreeMapper treeMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Tree update(@NonNull TreeRequest tree, @NonNull String idTree) {

        Tree updateTree = findByIdWithValidation(idTree);
        Tree newParent = findByIdWithValidation(tree.getIdParent());

        validateIfTheParentIsAlreadyChildren(updateTree, newParent);
        validateIfTheParentIsYourself(updateTree, newParent);
        validateIfTheSameParent(updateTree, newParent);
        validateStackOverFlowError(updateTree, newParent);


        if (!validateIfTheParentIsFull(newParent)) {

            updateTree.getIdParent().ifPresent(idOldParent -> {
                Tree oldParent = findByIdWithValidation(idOldParent);
                oldParent.getIdRightNode().filter(right -> right.equals(updateTree.getId())).ifPresent(id -> oldParent.setIdRightNode(null));
                oldParent.getIdLeftNode().filter(left -> left.equals(updateTree.getId())).ifPresent(id -> oldParent.setIdLeftNode(null));
                repository.save(oldParent);
            });

            insertANodeOnParent(newParent, updateTree);
            repository.save(newParent);

            updateTree.setIdParent(newParent.getId());
        }

        return repository.save(updateTree);
    }

    private void validateIfTheParentIsAlreadyChildren(Tree updateTree, Tree newParent) {
        if(updateTree.getIdRightNode().filter(right -> right.equals(newParent.getId())).isPresent()){
            throw new TheParentIsAlreadyChildrenException("The parent choiced is already your children");
        }
        if(updateTree.getIdLeftNode().filter(left -> left.equals(newParent.getId())).isPresent()){
            throw new TheParentIsAlreadyChildrenException("The parent choiced is already your children");
        }
    }

    private void validateIfTheParentIsYourself(Tree updateTree, Tree newParent) {
        if(updateTree.getId().equals(newParent.getId())){
            throw new ParentIsYourselfException("You can not insert yourself as parent");
        }
    }

    private void validateIfTheSameParent(Tree updateTree, Tree newParent) {
        if(updateTree.getIdParent().filter(parent -> parent.equals(newParent.getId())).isPresent()){
            throw new SameParentException("The parent is already his parent");
        }
    }

    private void validateStackOverFlowError(Tree updateTree, Tree newParent) {
        if(TreeResponseBuilder.init(updateTree, this).safeBuild().getHeight() < TreeResponseBuilder.init(newParent, this).safeBuild().getHeight()){
            throw new StackOverflowException("Your change will genarate a StackOverFlowError");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Tree save(@NonNull TreeRequest tree) {
        boolean alreadyExistRoot = repository.findRoot().isPresent();
        validateIfARootAlreadyExists(tree, alreadyExistRoot);
        validateIfANewNodeHaveAParentAndIfTheParentExists(tree);

        Tree root = alreadyExistRoot ? createNodeWithParent(tree) : createRootNode();

        createChildrens(tree, root);

        repository.save(root);

        return root;
    }

    private Tree createNodeWithParent(@NonNull TreeRequest tree) {
        Tree parent = findByIdWithValidation(tree.getIdParent());
        Tree node = repository.insert(new Tree(getValidParent(parent), false));
        insertANodeOnParent(parent, node);
        repository.save(parent);

        return node;
    }

    private void insertANodeOnParent(Tree parent, Tree node) {
        if (parent.getIdLeftNode().isPresent()) {
            parent.setIdRightNode(node.getId());
        } else {
            parent.setIdLeftNode(node.getId());
        }
    }

    private Tree createRootNode() {
        return repository.save(new Tree(true));
    }

    private void validateIfARootAlreadyExists(@NonNull TreeRequest tree, boolean alreadyExistRoot) {
        if (StringUtils.isEmpty(tree.getIdParent()) && alreadyExistRoot) {
            throw new OnlyOneRootException("Only one root tree can be exists");
        }
    }

    private void validateIfANewNodeHaveAParentAndIfTheParentExists(@NonNull TreeRequest tree) {
        if (!StringUtils.isEmpty(tree.getIdParent())) {
            findByIdWithValidation(tree.getIdParent());
        }
    }

    private String getValidParent(@NonNull Tree parent) {
        return !validateIfTheParentIsFull(parent) ? parent.getId() : null;
    }

    private boolean validateIfTheParentIsFull(@NonNull Tree parent) {
        if (parent.getIdRightNode().isPresent() && parent.getIdLeftNode().isPresent()) {
            throw new FullTreeException("Parent Tree is Full");
        } else {
            return false;
        }
    }

    private void createChildrens(@NonNull TreeRequest tree, Tree root) {
        if (BooleanUtils.toBoolean(tree.getHasLeftNode())) {
            root.setIdLeftNode(save(TreeRequest.builder().idParent(root.getId()).build()).getId());
        }
        if (BooleanUtils.toBoolean(tree.getHasRightNode())) {
            root.setIdRightNode(save(TreeRequest.builder().idParent(root.getId()).build()).getId());
        }
    }

    @Transactional(readOnly = true)
    public Tree findByIdWithValidation(@NotNull String idTree) {
        return repository.findById(idTree).orElseThrow(() -> new TreeNotFoundException("No exist tree with id " + idTree));
    }

    @Transactional(readOnly = true)
    public Tree findById(@NotNull String idTree) {
        return repository.findById(idTree).orElse(null);
    }

    public Resource<TreeResponse> findByIdForResource(@NonNull String id, @NonNull Boolean safeMode) {
        Tree tree = findByIdWithValidation(id);
        Resource<TreeResponse> resource = new Resource<>(treeMapper.toRequest(tree, safeMode));
        resource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TreeResource.class).findOne(id, safeMode)).withSelfRel());

        tree.getIdParent().ifPresent(idParent -> resource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TreeResource.class).findOne(idParent, safeMode)).withRel("parent")));
        tree.getIdRightNode().ifPresent(ifRightNode -> resource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TreeResource.class).findOne(ifRightNode, safeMode)).withRel("right")));
        tree.getIdLeftNode().ifPresent(idLeftNode -> resource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TreeResource.class).findOne(idLeftNode, safeMode)).withRel("left")));

        return resource;
    }

    public Page<String> getAllChildrenNodesForResource(@NonNull String id, @NonNull Boolean safeMode, int page, int size) {
        List<Tree> allChildrenNodesRecursive = safeMode ? getAllChildrenNodesIterative(findByIdWithValidation(id)) : getAllChildrenNodesRecursive(findByIdWithValidation(id));
        List<String> childrens = allChildrenNodesRecursive.stream()
                .map(treeFE -> ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TreeResource.class).findOne(treeFE.getId(), safeMode)).withRel("children").getHref()).collect(Collectors.toList());

        return PageUtils.convertListToPage(childrens, page, size);
    }

    /**
     * The simplest way to do this, however, is susceptible to {@link StackOverflowError} if the tree structure is too large
     *
     * @param tree
     * @return List<Tree>
     */
    private List<Tree> getAllChildrenNodesRecursive(@NonNull Tree tree) {
        List<Tree> childrens = new ArrayList<>();

        tree.getIdLeftNode().ifPresent(idLeftNode -> {
            Tree left = findByIdWithValidation(idLeftNode);
            childrens.add(left);
            childrens.addAll(getAllChildrenNodesRecursive(left));
        });

        tree.getIdRightNode().ifPresent(idRightNode -> {
            Tree right = findByIdWithValidation(idRightNode);
            childrens.add(right);
            childrens.addAll(getAllChildrenNodesRecursive(right));
        });

        return childrens;
    }

    /**
     * Using the way of recursion, the algorithm is susceptible to a {@link StackOverflowError}, so I used the {@link Queue} interface to avoid this error
     *
     * @param tree
     * @return List<Tree>
     */
    private List<Tree> getAllChildrenNodesIterative(@NonNull Tree tree) {
        List<Tree> childrens = new ArrayList<>();
        final Queue<Tree> queue = new LinkedList<>();
        queue.offer(tree);
        while (!queue.isEmpty()) {
            final Tree currentNode = queue.poll();
            currentNode.getIdLeftNode().ifPresent(idLeftNode -> {
                Tree left = findByIdWithValidation(idLeftNode);
                childrens.add(left);
                queue.offer(left);
            });
            currentNode.getIdRightNode().ifPresent(idRightNode -> {
                Tree right = findByIdWithValidation(idRightNode);
                childrens.add(right);
                queue.offer(right);
            });
        }
        return childrens;
    }

}
