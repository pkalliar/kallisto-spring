package com.pankal.security;

import com.pankal.contact.ContactRepository;
import com.pankal.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@CrossOrigin("*")
public class AuthenticationController {

	private ContactRepository contactRepository;
	private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

	public AuthenticationController(ContactRepository contactRepository) {
		this.contactRepository = contactRepository;
	}

//	@CrossOrigin("*")
	@PostMapping
	public void doLogin(@RequestBody User user) {
		log.info("do login for " + user.getUsername());

//		return contactRepository.findAll(request);
	}

}
