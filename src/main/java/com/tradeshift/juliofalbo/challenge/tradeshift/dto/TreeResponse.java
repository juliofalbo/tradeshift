package com.tradeshift.juliofalbo.challenge.tradeshift.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeResponse implements Serializable {
    private String id;
    private Integer height;
}
