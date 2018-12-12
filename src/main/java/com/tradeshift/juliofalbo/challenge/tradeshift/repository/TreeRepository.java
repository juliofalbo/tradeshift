package com.tradeshift.juliofalbo.challenge.tradeshift.repository;

import com.tradeshift.juliofalbo.challenge.tradeshift.entity.Tree;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TreeRepository extends MongoRepository<Tree, String> {

    @Query("{isRoot: {$eq: true}}")
    Optional<Tree> findRoot();

}
