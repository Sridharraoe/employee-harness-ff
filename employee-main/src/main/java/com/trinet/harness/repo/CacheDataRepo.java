package com.trinet.harness.repo;

import com.trinet.harness.domain.FFRedisDto;
import com.trinet.harness.domain.FeatureFlagDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CacheDataRepo extends CrudRepository<FeatureFlagDto, String> {

    List<FFRedisDto> findByIdContainingIgnoreCase(String name);
}

