package com.trinet.harness.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trinet.harness.domain.FFRedisDto;
import com.trinet.harness.domain.FeatureFlagDto;
import com.trinet.harness.repo.CacheDataRepo;

@Service
public class FeatureFlagsService {

	@Autowired
	CacheDataRepo cacheDataRepo;

	@Autowired
	ObjectMapper objectMapper;

	public List<FFRedisDto> getAllFlags() throws JsonProcessingException {
		return this.getCacheData();
	}

	public FFRedisDto getFlagById(String flagName) throws JsonProcessingException {
		return this.getCacheData().stream().filter(x -> x.getName().equalsIgnoreCase(flagName)).findFirst()
				.orElse(null);
	}

	private List<FFRedisDto> getCacheData() throws JsonProcessingException {
		Optional<FeatureFlagDto> optionalCacheData = cacheDataRepo.findById("allFlags");
		List<FFRedisDto> flagsList = new ArrayList<>();
		if (optionalCacheData.isPresent()) {
			String flagsAsString = optionalCacheData.get().getValue();
			TypeReference<List<FFRedisDto>> mapType = new TypeReference<List<FFRedisDto>>() {
			};
			flagsList = objectMapper.readValue(flagsAsString, mapType);
		}
		return flagsList;
	}

}
