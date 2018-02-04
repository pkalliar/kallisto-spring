package com.pankal;

import com.fasterxml.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Created by pankal on 4/2/18.
 */

public class Skroutz {

	private static final Logger log = LoggerFactory.getLogger(Application.class);


	public static void doit() {
		RestTemplate restTemplate = new RestTemplate();


		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Accept", "application/vnd.skroutz+json; version=3");
		headers.add("Authorization", "Bearer κκκo6h9k-txp-YKy7-rIMTJmcVPad16a-aS6C_pmySghriSN4ne6boaKTr4ngUkt7Y-GhCy_iz7DPfXjX7obDMdfA==");
//


		String url1 = "https://api.skroutz.gr/api/search";
		String url2 = "http://api.worldbank.org/v2/en/sources/15/series/all/metadata?page=1&per_page=20000&format=json";

//		HttpEntity<String> entity = new HttpEntity<String>(postBodyJson ,headers);
//		restTemplate.get(url1, entity);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.set("Accept", "application/vnd.skroutz+json; version=3");
		params.set("Authorization", "Bearer κκκo6h9k-txp-YKy7-rIMTJmcVPad16a-aS6C_pmySghriSN4ne6boaKTr4ngUkt7Y-GhCy_iz7DPfXjX7obDMdfA==");

		JsonNode quote = restTemplate.getForObject(url2, JsonNode.class, params);
		log.info(quote.toString());
	}
}
