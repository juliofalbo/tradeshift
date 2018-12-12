package com.tradeshift.juliofalbo.challenge.tradeshift.resource;

import com.tradeshift.juliofalbo.challenge.tradeshift.dto.TreeRequest;
import com.tradeshift.juliofalbo.challenge.tradeshift.dto.TreeResponse;
import com.tradeshift.juliofalbo.challenge.tradeshift.service.TreeService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/trees")
public class TreeResource {

    @Autowired
    private TreeService service;

    @GetMapping("/{id}")
    public Resource<TreeResponse> findOne(@PathVariable String id, @RequestParam(required = false) Boolean safeMode){
        return service.findByIdForResource(id, BooleanUtils.toBoolean(safeMode));
    }

    @GetMapping("/{id}/childrens")
    public Page<String> getAllChildrenNodes(@PathVariable String id, @RequestParam(required = false) Boolean safeMode, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "5") int size){
        return service.getAllChildrenNodesForResource(id, BooleanUtils.toBoolean(safeMode), page, size);
    }

    @PatchMapping("/{id}")
    public ResponseEntity patch(@PathVariable String id, @RequestBody TreeRequest tree) {
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(service.update(tree, id).getId()).toUri();

        return ResponseEntity.ok(uri);
    }

    @PostMapping
    public ResponseEntity insert(@RequestBody TreeRequest tree) {
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(service.save(tree).getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

}
