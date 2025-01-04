package com.friendgithub.api.repository;

import com.friendgithub.api.entity.Version;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VersionRepository extends MongoRepository<Version, String> {
}
