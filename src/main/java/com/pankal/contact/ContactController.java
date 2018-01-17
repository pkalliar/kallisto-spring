package com.pankal.contact;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pankal.security.AuthenticationController;
import com.pankal.task.Task;
import com.pankal.task.TaskRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin("*")
public class ContactController {

	private ContactRepository contactRepository;
	private static ObjectMapper m = null;

	private static final Logger log = LoggerFactory.getLogger(ContactController.class);

	public ContactController(ContactRepository contactRepository) {
		this.contactRepository = contactRepository;
		m = new ObjectMapper();
	}



	@GetMapping
	public List<Contact> getContacts() {
		log.info("in getContacts");
		PageRequest request = new PageRequest(0,40);
		Page page = contactRepository.findAll(request);

		return page.getContent();

//		return contactRepository.findAll(request);
	}

	@GetMapping("/search/{name}")
	public List<Contact> searchContacts(@PathVariable String name) {
		log.info("in getContacts search for " + name);

//		return null;
		return contactRepository.findByNameOrCode(name);

	}

	@GetMapping("/filter/{criteria}")
	@Transactional(readOnly = true)
	public List<Contact> loadContacts(@PathVariable String criteria) {
		log.info("in getContacts filter for " + criteria);
		JsonNode crit;
		List<Contact> results = new ArrayList<>();
		try {
			crit = m.readTree(criteria);
			if(crit.isArray()) {
				if(((ArrayNode)crit).size() > 0) {
					Iterator<JsonNode> it = ((ArrayNode) crit).elements();
//				int ind = 0;

					while (it.hasNext()) {
						ObjectNode tdata = (ObjectNode) it.next();
						log.info("filter: " + tdata);
						Stream<Contact> stream = contactRepository.findByCriteria(tdata.get("name").textValue());
						stream.forEach(c -> results.add(c));
					}
					return results;
				}else{
					PageRequest request = new PageRequest(0, 40);
					Page page = contactRepository.findAll(request);
					return page.getContent();
				}


			}

		} catch (IOException e) {
			e.printStackTrace();
		}



//		return null;
		return contactRepository.findByNameOrCode(criteria);
	}

	@GetMapping("/{id}")
	public Contact getContact(@PathVariable String id) {

		log.info("in getContact with id " + id);
		try {
			UUID uuid = UUID.fromString(id);
			PageRequest request = new PageRequest(0,40);
//		Page page = contactRepository.findAll(request);

			Example<Contact> example = Example.of(new Contact(uuid));
			Contact res = contactRepository.findOne(example);
			if(res != null)
				return contactRepository.findOne(example);
			else
				return example.getProbe();
		}catch (IllegalArgumentException e){
			Contact res = new Contact();
			return res;
		}

	}

	@PostMapping
	public Contact generateContactUUID(@RequestBody Contact contact) {
		log.info("Received POST with contact " + contact);

		Contact res = new Contact(UUID.randomUUID());

//		Contact res = contactRepository.save(contact);
//		ObjectNode res = JsonNodeFactory.instance.objectNode().put("response", "test resp 34");
		return res;
	}

	@PutMapping("/{id}")
	public Contact editContact(@PathVariable String id, @RequestBody Contact contact) {

//		Contact existingContact = (Contact) contactRepository.findOne(id);
//		Assert.notNull(existingContact, "Contact not found");
//		existingContact.setLegal_name(contact.getLegal_name());
//		contactRepository.save(existingContact);

		log.info("in putContact with id " + id);
		try {
			UUID uuid = UUID.fromString(id);
		}catch (IllegalArgumentException e){
			log.info("Saving new contact.");
		}

		log.info("Received PUT with contact " + contact);

		Contact res = contactRepository.save(contact);
		return res;
	}

	@DeleteMapping("/{id}")
	public JsonNode deleteTask(@PathVariable UUID id) {

		Example<Contact> example = Example.of(new Contact(id));
		Contact res = contactRepository.findOne(example);
		ObjectNode response = JsonNodeFactory.instance.objectNode();
		if(res != null) {
			contactRepository.delete(res);
			return response.put("response", "success");
		}else
			return response.put("response", "failure");


	}
}
