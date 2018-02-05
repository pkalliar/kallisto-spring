package com.pankal;

import com.fasterxml.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Created by pankal on 4/2/18.
 */

public class Skroutz {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	private static HttpEntity<?> prepareRequest(){

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Accept", "application/vnd.skroutz+json; version=3");
		headers.add("Authorization", "Bearer o6h9k-txp-YKy7-rIMTJmcVPad16a-aS6C_pmySghriSN4ne6boaKTr4ngUkt7Y-GhCy_iz7DPfXjX7obDMdfA==");

		return new HttpEntity<Object>(headers);
	}


	public static JsonNode search(String toSearch) {
		RestTemplate restTemplate = new RestTemplate();

		String url1 = "https://api.skroutz.gr/api/search?q=" + toSearch;

		ResponseEntity<JsonNode> quote =
				restTemplate.exchange(url1, HttpMethod.GET, prepareRequest(), JsonNode.class);

//		log.info("quote is " + quote.getBody());
		return quote.getBody();
	}
}
