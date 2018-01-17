package com.pankal.security;

import com.pankal.contact.Contact;
import com.pankal.contact.ContactRepository;
import com.pankal.user.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/babadoo")
@CrossOrigin("*")
public class AuthenticationController {

	private ContactRepository contactRepository;
	private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

	public AuthenticationController(ContactRepository contactRepository) {
		this.contactRepository = contactRepository;
	}

//	@CrossOrigin("*")
	@PostMapping
	public void doLogin(@RequestBody ApplicationUser user) {
		log.info("do login for " + user.getUsername());

//		return contactRepository.findAll(request);
	}

}
