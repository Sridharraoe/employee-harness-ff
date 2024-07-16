package com.trinet.harness.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "FeatureFlagsNew")
public class FeatureFlagDto {

	@Id
	private String key;
	@Indexed
	private String value;

	public FeatureFlagDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FeatureFlagDto(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
