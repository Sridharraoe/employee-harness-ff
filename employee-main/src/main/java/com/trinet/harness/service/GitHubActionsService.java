package com.trinet.harness.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GitHubActionsService {

	private static final Logger log = LoggerFactory.getLogger(GitHubActionsService.class);

	@Value("${github.actions.trigger.url}")
	String workflowUrl;

	@Value("${github.actions.token}")
	String accessToken;

	public void triggerGitHubActionWorkflow() {
			log.info("triggerGitHubActionWorkflow() -- Start");

		    String repositoryOwner = "ashfaq11";
	        String repositoryName = "employee";
	        String workflowName = "trigger-github-actions";
	        
	        String url = "https://api.github.com/repos/ashfaq11/employee/dispatches";

	        log.info(url);
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
	        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
	        headers.set(HttpHeaders.USER_AGENT, "Java HTTP Client");

	        String jsonBody = "{\"event_type\": \"" + workflowName + "\"}";

	        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

	        RestTemplate restTemplate = new RestTemplate();

	        try {
	            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
	            log.info("Response status code: " + response.getStatusCode());
	        } catch (Exception e) {
	        	log.info("Error: " + e.getMessage());
	        }
	        log.info("triggerGitHubActionWorkflow() -- End");
	}

}
