package com.pankal.shop.classifiedAd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pankal.inventory.contact.Item;

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
@RequestMapping("/api/classifiedAds")
@CrossOrigin("*")
public class ClassifiedAdsController {

	private ClassifiedAdsRepository classifiedAdsRepository;
	private static ObjectMapper m = null;

	private static final Logger log = LoggerFactory.getLogger(ClassifiedAdsController.class);

	public ClassifiedAdsController(ClassifiedAdsRepository repository) {
		this.classifiedAdsRepository = repository;
		m = new ObjectMapper();
	}



	@GetMapping
	public List<Item> getItems() {
		log.info("in getItems");
		PageRequest request = new PageRequest(0,40);
		Page page = classifiedAdsRepository.findAll(request);

		return page.getContent();

//		return itemRepository.findAll(request);
	}

	@GetMapping("/search/{name}")
	public List<Item> searchContacts(@PathVariable String name) {
		log.info("in getContacts search for " + name);

		return null;
//		return itemRepository.findByNameOrCode(name);

	}


	@GetMapping("/{id}")
	public ClassifiedAds getContact(@PathVariable String id) {

		log.info("in getContact with id " + id);
		try {
			UUID uuid = UUID.fromString(id);
			PageRequest request = new PageRequest(0,40);
//		Page page = itemRepository.findAll(request);

			Example<ClassifiedAds> example = Example.of(new ClassifiedAds(uuid));
			ClassifiedAds res = classifiedAdsRepository.findOne(example);
			if(res != null)
				return classifiedAdsRepository.findOne(example);
			else
				return example.getProbe();
		}catch (IllegalArgumentException e){
			return null;
		}

	}

	@PostMapping
	public Item generateContactUUID(@RequestBody Item item) {
		log.info("Received POST with item " + item);

		Item res = new Item(UUID.randomUUID());

//		Item res = itemRepository.save(item);
//		ObjectNode res = JsonNodeFactory.instance.objectNode().put("response", "test resp 34");
		return res;
	}

	@PutMapping("/{id}")
	public ClassifiedAds editContact(@PathVariable String id, @RequestBody ClassifiedAds item) {

//		Item existingContact = (Item) itemRepository.findOne(id);
//		Assert.notNull(existingContact, "Item not found");
//		existingContact.setLegal_name(item.getLegal_name());
//		itemRepository.save(existingContact);

		log.info("in putContact with id " + id);
		try {
			UUID uuid = UUID.fromString(id);
		}catch (IllegalArgumentException e){
			log.info("Saving new item.");
		}

		log.info("Received PUT with item " + item);

		ClassifiedAds res = classifiedAdsRepository.save(item);
		return res;
	}

	@DeleteMapping("/{id}")
	public JsonNode deleteTask(@PathVariable UUID id) {

		Example<ClassifiedAds> example = Example.of(new ClassifiedAds(id));
		ClassifiedAds res = classifiedAdsRepository.findOne(example);
		ObjectNode response = JsonNodeFactory.instance.objectNode();
		if(res != null) {
			classifiedAdsRepository.delete(res);
			return response.put("response", "success");
		}else
			return response.put("response", "failure");


	}
}
