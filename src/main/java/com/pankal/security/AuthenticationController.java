package com.pankal.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.pankal.contact.ContactRepository;
import com.pankal.user.Person;
import com.pankal.user.User;
import com.pankal.user.UserRepository;
import com.pankal.utilities.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@RestController
@RequestMapping("/api/login")
@CrossOrigin("*")
public class AuthenticationController {

	private UserRepository userRepository;
	private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
	MessageDigest msgDigestSHA256 = null;

	public AuthenticationController(UserRepository userRepository) throws NoSuchAlgorithmException {
		this.userRepository = userRepository;
		msgDigestSHA256 = MessageDigest.getInstance("SHA-256");
	}

//	@CrossOrigin("*")
	@PostMapping
	public User doLogin(@RequestBody User user, HttpServletRequest request, HttpServletResponse response)


	{
		log.info("do login for " + user.getUsername() + " -- " +  user.getPassword() + " from " + request.getRemoteHost());

		Example<User> example = Example.of(user);
		long apikey_expires = 0;
		String apikey = null;


			User res = userRepository.findByUsername(user.getUsername());
			if( res == null){
				response.addHeader("result", "app-error");
				return example.getProbe();
			}else {
				apikey = Utilities.digest(msgDigestSHA256, (user.getUsername() + user.getPassword() + new Date().getTime()));
				ZonedDateTime expDate = ZonedDateTime.now().plusMinutes(45);
//				apikey_expires = expDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
				res.setApikey(apikey);
				res.setApikey_expires(expDate);
				userRepository.save(res);
				return res;
			}

//		return contactRepository.findAll(request);
	}

}
