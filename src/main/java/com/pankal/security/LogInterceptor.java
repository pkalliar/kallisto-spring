package com.pankal.security;

import com.pankal.user.User;
import com.pankal.user.UserRepository;
import com.pankal.utilities.Utilities;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Enumeration;

@Component
@CrossOrigin("*")
public class LogInterceptor extends HandlerInterceptorAdapter {

	private UserRepository userRepository;
	private static final Logger log = LoggerFactory.getLogger(LogInterceptor.class);

	public LogInterceptor(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		long startTime = System.currentTimeMillis();
		System.out.println("\n-------- LogInterception.preHandle --- " + request.getMethod());
//		System.out.println("Request URL: " + request.getRequestURL() + ".." + request.getServletPath());
//		System.out.println("Start Time: " + System.currentTimeMillis());
//
//		request.setAttribute("startTime", startTime);


		if(request.getMethod().equals("OPTIONS")){
			response.setStatus(HttpServletResponse.SC_OK);
			return true;
		}


		Enumeration<String> h2 = request.getHeaderNames();
		while(h2.hasMoreElements()){
			String header = h2.nextElement();
			String val = request.getHeader(header);
			log.info(header + " --> " + val);
		}


		String intercept = request.getHeader("intercept");
		System.out.println("intercept: " + intercept);
		String apikey = request.getHeader("apikey");
		System.out.println("apikey: " + apikey);
		if(request.getServletPath().equals("/api/authenticate/login")){
			return true;
		}else if(apikey == null || apikey.length() == 1) {
			response.addHeader("access-control-expose-headers", "result");
			response.addHeader("result", "app-error");
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "no valid apikey");
			return true;
		}

		User res = userRepository.findByApikey(apikey);
		if( res == null){
			response.addHeader("access-control-expose-headers", "result");
			response.addHeader("result", "app-error");
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "no valid apikey");
			return true;
		}else{
			ZonedDateTime expDate = ZonedDateTime.now().plusMinutes(45);
//			long apikey_expires = expDate.toInstant().toEpochMilli();
			response.addHeader("access-control-expose-headers", "apikey_expires");
			response.addHeader("apikey_expires", expDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
			res.setApikey_expires(expDate);
			userRepository.save(res);
			return true;
		}

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, //
						   Object handler, ModelAndView modelAndView) throws Exception {

		System.out.println("\n-------- LogInterception.postHandle --- ");
		System.out.println("Request URL: " + request.getRequestURL());

//		response.addHeader("apikey", "here goes the apikey");

		// You can add attributes in the modelAndView
		// and use that in the view page
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, //
								Object handler, Exception ex) throws Exception {
//		System.out.println("\n-------- LogInterception.afterCompletion --- ");
//
//		long startTime = (Long) request.getAttribute("startTime");
//		long endTime = System.currentTimeMillis();
//		System.out.println("Request URL: " + request.getRequestURL());
//		System.out.println("End Time: " + endTime);
//
//		System.out.println("Time Taken: " + (endTime - startTime));
//
//		response.addCookie(new Cookie("anme", "aval"));

	}

}
