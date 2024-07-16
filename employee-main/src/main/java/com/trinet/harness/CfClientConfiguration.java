package com.trinet.harness;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.trinet.harness.domain.FFRedisDto;
import com.trinet.harness.domain.FeatureFlagDto;
import com.trinet.harness.repo.CacheDataRepo;
import com.trinet.harness.service.GitHubActionsService;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.trinet.harness.utils.FeatureFlagConstants;
import com.trinet.harness.utils.HarnessUtils;

import io.harness.cf.client.api.BaseConfig;
import io.harness.cf.client.api.CfClient;
import io.harness.cf.client.api.Event;
import io.harness.cf.client.api.FeatureFlagInitializeException;
import io.harness.cf.client.connector.HarnessConfig;
import io.harness.cf.client.connector.HarnessConnector;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
public class CfClientConfiguration {
	Logger logger = LoggerFactory.getLogger(CfClientConfiguration.class);
	private final CacheDataRepo cacheDataRepository;

	@Autowired
	GitHubActionsService gitHubActionsService;

	private final ObjectMapper objectMapper;
	private String apiKey = FeatureFlagConstants.API_KEY;
//	CfClient cfClient;

	public CfClientConfiguration(CacheDataRepo cacheDataRepository, ObjectMapper objectMapper) {
		this.cacheDataRepository = cacheDataRepository;
		this.objectMapper = objectMapper;
	}

//	@SneakyThrows
//	@Bean
//	CfClient cfClient() throws FeatureFlagInitializeException, InterruptedException, JsonProcessingException {
//
//		HarnessConfig connectorConfig = HarnessConfig.builder().configUrl("https://config.ff.harness.io/api/1.0")
//				.eventUrl("https://events.ff.harness.io/api/1.0").build();
//
//		BaseConfig options = BaseConfig.builder().pollIntervalInSeconds(60).streamEnabled(true).analyticsEnabled(true)
//				.build();
//
//		// Create the client
//		cfClient = new CfClient(new HarnessConnector(apiKey, connectorConfig), options);
//		// CfClient cfClient = new CfClient(apiKey);
////		cfClient.waitForInitialization();
//		
//		cfClient.on(Event.READY, result -> logger.info("Harness client initialized."));
//		cfClient.on(Event.CHANGED, this::getSSEvents);
//		// Cache Data
//		Optional<FeatureFlagDto> optionalCacheData = cacheDataRepository.findById("allFlags");
//		if (optionalCacheData.isEmpty()) {
//			logger.info("===== updating redis from app start");
//			this.getFFValues();
//		}
//		return cfClient;
//	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*");
			}
		};
	}

	private void getSSEvents(String flag) {
		logger.info("--->Triggering github actions workflow<---");
		
		gitHubActionsService.triggerGitHubActionWorkflow();
	}

	public void getFFValues() throws JsonProcessingException {
		// fetch all feature flag values
		String featureFlagString =getFeatureFlagValues();
		JsonParser parser = new JsonParser();
		// Creating JSONObject from String using parser
		JsonObject featureFlagJson = parser.parse(featureFlagString).getAsJsonObject();
		JsonArray featureFlags = featureFlagJson.getAsJsonArray("features");

		// stale , status , name , identifier ,kind , project , env
		List<FFRedisDto> ffList = new ArrayList<>();
		FFRedisDto flag = new FFRedisDto();
		for (JsonElement element : featureFlags) {
			flag.setIdentifier(element.getAsJsonObject().get("identifier").getAsString());
			flag.setState(element.getAsJsonObject().get("envProperties").getAsJsonObject().get("state").getAsString());
			flag.setName(element.getAsJsonObject().get("name").getAsString());
			ffList.add(flag);
			flag = new FFRedisDto();
		}

		String flagsAsJsonString = objectMapper.writeValueAsString(ffList);
		FeatureFlagDto cacheData = new FeatureFlagDto("allFlags", flagsAsJsonString);
		logger.info("---->CACHE UPDATED<-----");
		cacheDataRepository.save(cacheData);
	}
	
	private  String getFeatureFlagValues() {
        var httpClient = HttpClient.newBuilder().build();

        logger.info("Updating redis from HarnessUtils");
        HashMap<String, String> params = new HashMap<>();
        params.put("accountIdentifier", FeatureFlagConstants.ACCOUNT_IDENTIFIER);
        params.put("orgIdentifier", FeatureFlagConstants.ORG_IDENTIFIER);
        params.put("projectIdentifier", FeatureFlagConstants.PROJECT_IDENTIFIER);
        params.put("environmentIdentifier", FeatureFlagConstants.ENVIRONMENT_IDENTIFIER);


        var query = params.keySet().stream()
          .map(key -> key + "=" + URLEncoder.encode(params.get(key), StandardCharsets.UTF_8))
          .collect(Collectors.joining("&"));

        var host = "https://app.harness.io";
        var pathname = "/cf/admin/features";
        var request = HttpRequest.newBuilder()
          .GET()
          .uri(URI.create(host + pathname + '?' + query))
          .header("x-api-key", FeatureFlagConstants.DEV_X_API_KEY)
          .build();

        HttpResponse<String> response;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			e.printStackTrace();
			return "Exception Occurred".concat(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			return "Exception Occurred".concat(e.getMessage());
		}

        return response.body();
      }

}
