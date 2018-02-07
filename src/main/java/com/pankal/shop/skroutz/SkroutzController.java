package com.pankal.shop.skroutz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.pankal.priority.Priority;
import com.pankal.priority.PriorityRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/skroutz")
@CrossOrigin("*")
public class SkroutzController {

	private PriorityRepository priorityRepository;

	private static final Logger log = LoggerFactory.getLogger(SkroutzController.class);

	public SkroutzController(PriorityRepository priorityRepository) {
		this.priorityRepository = priorityRepository;
	}



	@GetMapping("/search/{toSearch}")
	public JsonNode search(@PathVariable String toSearch) {
		log.info("in getContact with id " + toSearch);
		JsonNode res = Skroutz.search(toSearch);
		return res;
	}

}
