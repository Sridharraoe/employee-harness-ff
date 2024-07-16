package com.trinet.harness.utils;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trinet.harness.service.FeatureFlagsService;

import io.harness.cf.client.api.CfClient;
import io.harness.cf.client.dto.Target;
 
@Component
public class HarnessProvider {
 
	
	@Autowired
	FeatureFlagsService featureFlagsService;
//	
//	@Autowired
//	CfClient cfClient;
 
    public boolean getFlagValues(String flagName) {
        final Target target = Target.builder()
		    .identifier(FeatureFlagConstants.IDENTIFIER)
		    .name(FeatureFlagConstants.NAME)
		    .attribute(FeatureFlagConstants.LOCATION, FeatureFlagConstants.LOCATION_VALUE)
		    .build();
        
		//return cfClient.boolVariation(flagName, null, false);
        return false;
    }
    
    public boolean getFlagValuesFromCache(String flagName) {
		try {
			return  featureFlagsService.getFlagById(flagName).getState().equals("on");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
    }
}