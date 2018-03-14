package com.pankal.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pankal.contact.ContactRepository;
import com.pankal.inventory.contact.Item;
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
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/authenticate")
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
	@PostMapping("/login")
	public String doLogin(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
		log.info("do login for " + user.getUsername() + " -- " +  user.getPassword() + " from " + request.getRemoteHost());

		Example<User> example = Example.of(user);
		long apikey_expires = 0;
		String apikey = null;

		ObjectNode clientResult = JsonNodeFactory.instance.objectNode();
		User res = userRepository.findByUsernamePassword(user.getUsername(), user.getPassword());
		if( res == null){
			response.addHeader("result", "app-error");

			clientResult.put("result", "No user found");
		}else {
			apikey = Utilities.digest(msgDigestSHA256, (user.getUsername() + user.getPassword() + new Date().getTime()));
			ZonedDateTime expDate = ZonedDateTime.now().plusMinutes(45);
//				apikey_expires = expDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			res.setApikey(apikey);
			res.setApikey_expires(expDate);
			userRepository.save(res);

			clientResult.put("apikey", apikey)
					.put("apikey_expires", expDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
					.put("username", res.getUsername());


		}
		return clientResult.toString();

//		return contactRepository.findAll(request);
	}

	@GetMapping("/refresh")
	public String refresh(HttpServletRequest request, HttpServletResponse response) {

		String apikey = request.getHeader("apikey");
		log.info("refreshing for " + apikey);

		ObjectNode res = JsonNodeFactory.instance.objectNode().put("response", "test resp 34");

		return res.toString();
//		return itemRepository.findByNameOrCode(name);

	}


	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {

		String apikey = request.getHeader("apikey");
		log.info("logging out for " + apikey);

		User res = userRepository.findByApikey(apikey);
		if( res == null){
			response.addHeader("access-control-expose-headers", "result");
			response.addHeader("result", "app-error");
			return "";
		}else {
			ZonedDateTime expDate = ZonedDateTime.now().minusSeconds(1);
//			long apikey_expires = expDate.toInstant().toEpochMilli();
			response.addHeader("access-control-expose-headers", "apikey_expires, apikey");
			response.setHeader("apikey_expires", expDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
			response.addHeader("apikey", "-");
			res.setApikey_expires(expDate);
			res.setApikey("-");
			userRepository.save(res);
			return "";
		}

//		ObjectNode res = JsonNodeFactory.instance.objectNode().put("response", "test resp 34");

//		return res.toString();
//		return itemRepository.findByNameOrCode(name);

	}


}
