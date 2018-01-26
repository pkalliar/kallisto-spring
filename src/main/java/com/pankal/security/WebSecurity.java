package com.pankal.security;

import org.springframework.boot.autoconfigure.security.IgnoredRequestCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

@CrossOrigin
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	private UserDetailsService userDetailsService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	public WebSecurity(UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
				.csrf().disable()
//				.authorizeRequests()
//				.requestMatchers(CorsUtils::isCorsRequest).permitAll()
//				.anyRequest().authenticated()
//				.and().httpBasic()
//				.and().addFilterBefore(new WebSecurityCorsFilter(), ChannelProcessingFilter.class)
//				.addFilter(new JWTAuthenticationFilter(authenticationManager()))
//				.addFilter(new JWTAuthorizationFilter(authenticationManager()))
				// this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

//		http.requiresChannel().antMatchers("/*").requires(ANY_CHANNEL).and()
//				.authorizeRequests()
//				.antMatchers("/*").permitAll()
//				.antMatchers(org.springframework.http.HttpMethod.OPTIONS, "/api/**").permitAll()
//				.and()
//				.addFilterBefore(corsFilter(), ChannelProcessingFilter.class);


//		http.cors().and()
//		.csrf().disable()
////				.authorizeRequests()
////				.antMatchers("/login*").anonymous()
////				.antMatchers("/contacts*").anonymous()
////				.antMatchers(HttpMethod.OPTIONS, "/login").permitAll()
////				.antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
////				.anyRequest().authenticated()
////				.and()
//				.addFilterBefore(corsFilter(), ChannelProcessingFilter.class)
//				.addFilter(new JWTAuthenticationFilter(authenticationManager()))
//				.addFilter(new JWTAuthorizationFilter(authenticationManager()));
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

//	@Bean
//	CorsConfigurationSource corsConfigurationSource() {
//		CorsConfiguration configuration = new CorsConfiguration();
//		configuration.setAllowedOrigins(Arrays.asList("*"));
////		configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:4200"));
//		configuration.setAllowedMethods(Arrays.asList( "GET","POST", "OPTIONS"));
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/**", configuration);
//		return source;
//	}

	@Bean
	public IgnoredRequestCustomizer optionsIgnoredRequestsCustomizer() {
		return configurer -> {
			List<RequestMatcher> matchers = new ArrayList<>();
			matchers.add(new AntPathRequestMatcher("/**", "OPTIONS"));
			configurer.requestMatchers(new OrRequestMatcher(matchers));
		};
	}

	public Filter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.setAllowedHeaders(getAllowedHeaders());
		config.setAllowedOrigins(getAllowedOrigins());
		config.setAllowedMethods(getAllowedMethods());
		config.setExposedHeaders(getAllowedHeaders());
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	List getAllowedHeaders() {
		List allowedHeaders = new ArrayList();
		allowedHeaders.add(MediaType.APPLICATION_JSON_VALUE);
		return allowedHeaders;
	}

	List getAllowedOrigins() {
		List allowedHeaders = new ArrayList();
		allowedHeaders.add("*");
		return allowedHeaders;
	}

	List getAllowedMethods() {
		List allowedMethods = new ArrayList();
		allowedMethods.add(RequestMethod.GET.name());
		allowedMethods.add(RequestMethod.POST.name());
		allowedMethods.add(RequestMethod.PUT.name());
		allowedMethods.add(RequestMethod.OPTIONS.name());
		return allowedMethods;
	}
}
