package com.pankal.security;

import com.pankal.user.User;
import com.pankal.user.UserRepository;
import com.pankal.utilities.Utilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class LogInterceptor extends HandlerInterceptorAdapter {

	private UserRepository userRepository;

	public LogInterceptor(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		long startTime = System.currentTimeMillis();
		System.out.println("\n-------- LogInterception.preHandle --- ");
		System.out.println("Request URL: " + request.getRequestURL() + ".." + request.getServletPath());
		System.out.println("Start Time: " + System.currentTimeMillis());

		String apikey = request.getHeader("apikey");
		System.out.println("apikey: " + apikey);

		User res = userRepository.findByApikey(apikey);
		if( res == null){
			response.addHeader("result", "app-error");
//			return false;
		}else {
			ZonedDateTime expDate = ZonedDateTime.now().plusMinutes(45);
//			long apikey_expires = expDate.toInstant().toEpochMilli();
			response.addHeader("access-control-expose-headers", "apikey_expires");
			response.addHeader("apikey_expires", expDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
			res.setApikey_expires(expDate);
			userRepository.save(res);
//			return true;
		}

		request.setAttribute("startTime", startTime);


		return true;
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
		System.out.println("\n-------- LogInterception.afterCompletion --- ");

		long startTime = (Long) request.getAttribute("startTime");
		long endTime = System.currentTimeMillis();
		System.out.println("Request URL: " + request.getRequestURL());
		System.out.println("End Time: " + endTime);

		System.out.println("Time Taken: " + (endTime - startTime));
	}

}
