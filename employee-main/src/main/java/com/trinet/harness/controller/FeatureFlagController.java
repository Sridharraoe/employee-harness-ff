package com.trinet.harness.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trinet.harness.domain.FFRedisDto;

import dev.openfeature.sdk.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/feature_flag")
public class FeatureFlagController {
    Logger logger = LoggerFactory.getLogger(FeatureFlagController.class);
    
    static FeatureFlagService featureFlagsService = new FeatureFlagService("6f0af4a2-172c-4f10-b7b0-59fd32e7cdd3");
    
    FeatureFlagController(){
    	
    }

//    @GetMapping("/fetchAll")
//    public ResponseEntity<List<FFRedisDto>> featureFlag() throws JsonProcessingException {
//
//        return ResponseEntity.ok(featureFlagsService.getAllFlags());
//    }

    @GetMapping("/findById")
    public ResponseEntity<Boolean> findById(@RequestParam("flagId") String flagName) throws JsonProcessingException {
    	boolean flagValue = featureFlagsService.getBooleanDeatils(flagName, false, null);
    	logger.info("--- flag value "+ flagValue);
    	
        return ResponseEntity.ok(flagValue);
    }


}
