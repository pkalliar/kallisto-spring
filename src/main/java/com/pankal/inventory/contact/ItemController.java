package com.pankal.inventory.contact;

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

@RestController
@RequestMapping("/api/items")
@CrossOrigin("*")
public class ItemController {

	private ItemRepository itemRepository;
	private static ObjectMapper m = null;

	private static final Logger log = LoggerFactory.getLogger(ItemController.class);

	public ItemController(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
		m = new ObjectMapper();
	}



	@GetMapping
	public List<Item> getItems() {
		log.info("in getItems");
		PageRequest request = new PageRequest(0,40);
		Page page = itemRepository.findAll(request);

		return page.getContent();

//		return itemRepository.findAll(request);
	}

	@GetMapping("/search/{name}")
	public List<Item> searchContacts(@PathVariable String name) {
		log.info("in getContacts search for " + name);

//		return null;
		return itemRepository.findByNameOrCode(name);

	}

	@GetMapping("/filter/{criteria}")
	@Transactional(readOnly = true)
	public List<Item> loadContacts(@PathVariable String criteria) {
		log.info("in getContacts filter for " + criteria);
		JsonNode crit;
		List<Item> results = new ArrayList<>();
		try {
			crit = m.readTree(criteria);
			if(crit.isArray()) {
				if(((ArrayNode)crit).size() > 0) {
					Iterator<JsonNode> it = ((ArrayNode) crit).elements();
//				int ind = 0;

					while (it.hasNext()) {
						ObjectNode tdata = (ObjectNode) it.next();
						log.info("filter: " + tdata);
						Stream<Item> stream = itemRepository.findByCriteria(tdata.get("name").textValue());
						stream.forEach(c -> results.add(c));
					}
					return results;
				}else{
					PageRequest request = new PageRequest(0, 40);
					Page page = itemRepository.findAll(request);
					return page.getContent();
				}


			}

		} catch (IOException e) {
			e.printStackTrace();
		}



//		return null;
		return itemRepository.findByNameOrCode(criteria);
	}

	@GetMapping("/{id}")
	public Item getContact(@PathVariable String id) {

		log.info("in getContact with id " + id);
		try {
			UUID uuid = UUID.fromString(id);
			PageRequest request = new PageRequest(0,40);
//		Page page = itemRepository.findAll(request);

			Example<Item> example = Example.of(new Item(uuid));
			Item res = itemRepository.findOne(example);
			if(res != null)
				return itemRepository.findOne(example);
			else
				return example.getProbe();
		}catch (IllegalArgumentException e){
			Item res = new Item();
			return res;
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
	public Item editContact(@PathVariable String id, @RequestBody Item item) {

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

		Item res = itemRepository.save(item);
		return res;
	}

	@DeleteMapping("/{id}")
	public JsonNode deleteTask(@PathVariable UUID id) {

		Example<Item> example = Example.of(new Item(id));
		Item res = itemRepository.findOne(example);
		ObjectNode response = JsonNodeFactory.instance.objectNode();
		if(res != null) {
			itemRepository.delete(res);
			return response.put("response", "success");
		}else
			return response.put("response", "failure");


	}
}
