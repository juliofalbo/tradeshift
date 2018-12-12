package com.tradeshift.juliofalbo.challenge.tradeshift.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Tree {

    public Tree(String idParent, Boolean isRoot) {
        this.idParent = idParent;
        this.isRoot = isRoot;
    }

    public Tree(String idParent) {
        this.idParent = idParent;
    }

    public Tree(Boolean isRoot) {
        this.isRoot = isRoot;
    }

    @Id
    private String id;

    private Boolean isRoot;

    private String idParent;

    private String idLeftNode;

    private String idRightNode;

    private transient Integer height;

    public Optional<String> getIdLeftNode() {
        return Optional.ofNullable(this.idLeftNode);
    }

    public Optional<String> getIdRightNode() {
        return Optional.ofNullable(idRightNode);
    }

    public Optional<String> getIdParent() {
        return Optional.ofNullable(idParent);
    }


}
