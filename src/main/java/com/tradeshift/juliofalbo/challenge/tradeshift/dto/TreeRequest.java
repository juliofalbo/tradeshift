package com.tradeshift.juliofalbo.challenge.tradeshift.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeRequest {

    public TreeRequest(String idParent){
        this.idParent = idParent;
    }

    private String idParent;
    private Boolean hasLeftNode;
    private Boolean hasRightNode;
}
