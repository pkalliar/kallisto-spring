package com.pankal.priority;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pankal.contact.Contact;
import com.pankal.contact.ContactRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/priorities")
@CrossOrigin("*")
public class PriorityController {

	private PriorityRepository priorityRepository;

	private static final Logger log = LoggerFactory.getLogger(PriorityController.class);

	public PriorityController(PriorityRepository priorityRepository) {
		this.priorityRepository = priorityRepository;
	}



	@GetMapping
	public List<Priority> getPriorities() {
		return priorityRepository.findAll();
	}

}
