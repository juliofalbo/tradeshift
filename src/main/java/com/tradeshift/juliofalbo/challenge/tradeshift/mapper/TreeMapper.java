package com.tradeshift.juliofalbo.challenge.tradeshift.mapper;

import com.tradeshift.juliofalbo.challenge.tradeshift.dto.TreeRequest;
import com.tradeshift.juliofalbo.challenge.tradeshift.dto.TreeResponse;
import com.tradeshift.juliofalbo.challenge.tradeshift.entity.Tree;
import com.tradeshift.juliofalbo.challenge.tradeshift.builder.TreeResponseBuilder;
import com.tradeshift.juliofalbo.challenge.tradeshift.service.TreeService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TreeMapper {

    @Autowired
    private TreeService treeService;

    public TreeResponse toRequest(@NonNull Tree tree, @NonNull Boolean safeWay){
        return safeWay ? TreeResponseBuilder.init(tree, treeService).safeBuild() : TreeResponseBuilder.init(tree, treeService).build();
    }

    public Tree toEntity(@NonNull TreeRequest treeRequest){
        return new Tree(treeRequest.getIdParent());
    }

}
