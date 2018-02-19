package com.pankal.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.UUID;
import java.util.stream.Stream;

@CrossOrigin
@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private PersonRepository personRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

	private static ObjectMapper m = null;

	private static final Logger log = LoggerFactory.getLogger(PersonController.class);


	public PersonController(PersonRepository personRepository,
							BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.personRepository = personRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		m = new ObjectMapper();
    }


	@GetMapping("/filter/{criteria}")
	@Transactional(readOnly = true)
	public List<Person> load(@PathVariable String criteria) {
		log.info("in get filter for " + criteria);
		JsonNode crit;
		List<Person> results = new ArrayList<>();
		try {
			crit = m.readTree(criteria);
			if(crit.isArray()) {
				if(((ArrayNode)crit).size() > 0) {
					Iterator<JsonNode> it = ((ArrayNode) crit).elements();
//				int ind = 0;

					while (it.hasNext()) {
						ObjectNode tdata = (ObjectNode) it.next();
						log.info("filter: " + tdata);
						Stream<Person> stream = personRepository.findByCriteria(tdata.get("name").textValue());
						stream.forEach(c -> results.add(c));
					}
					return results;
				}else{
					PageRequest request = new PageRequest(0, 40);
					Page page = personRepository.findAll(request);
					return page.getContent();
				}


			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
		//return personRepository.findByCriteria(criteria);
	}

	@GetMapping("/{id}")
	public Person get(@PathVariable String id) {

		log.info("in getAffair with id " + id);
		try {
			UUID uuid = UUID.fromString(id);
			PageRequest request = new PageRequest(0,40);
//		Page page = affairRepository.findAll(request);

			Example<Person> example = Example.of(new Person(uuid));
			Person res = personRepository.findOne(example);
			if(res != null)
				return personRepository.findOne(example);
			else
				return example.getProbe();
		}catch (IllegalArgumentException e){
			Person res = new Person();
			return res;
		}

	}

	@PostMapping
	public User generateUUID(@RequestBody User affair) {
		log.info("Received POST with User " + affair);

		User res = new User(UUID.randomUUID());

//		Folder res = affairRepository.save(affair);
//		ObjectNode res = JsonNodeFactory.instance.objectNode().put("response", "test resp 34");
		return res;
	}

	@PutMapping("/{id}")
	public Person edit(@PathVariable String id, @RequestBody Person entity) {

//		Folder existingContact = (Folder) affairRepository.findOne(id);
//		Assert.notNull(existingContact, "Folder not found");
//		existingContact.setLegal_name(affair.getLegal_name());
//		affairRepository.save(existingContact);

		log.info("in put with id " + id);
		try {
			UUID uuid = UUID.fromString(id);
		}catch (IllegalArgumentException e){
			log.info("Saving new User.");
		}

		log.info("Received PUT with user " + entity);

		Person res = personRepository.save(entity);
		return res;
	}

	@DeleteMapping("/{id}")
	public JsonNode delete(@PathVariable UUID id) {

		Example<Person> example = Example.of(new Person(id));
		Person res = personRepository.findOne(example);
		ObjectNode response = JsonNodeFactory.instance.objectNode();
		if(res != null) {
			personRepository.delete(res);
			return response.put("response", "success");
		}else
			return response.put("response", "failure");


	}
}
