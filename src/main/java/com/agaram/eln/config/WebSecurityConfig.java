package com.agaram.eln.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

//import antlr.collections.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements ApplicationContextAware {

	@Autowired
	private UserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// configure AuthenticationManager so that it knows from where to load
		// user for matching credentials
		// Use BCryptPasswordEncoder
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
//	
//	
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//    	// We don't need CSRF for this example
//    	http.csrf().disable()
//    					// dont authenticate this particular request
//    					.authorizeRequests().antMatchers("/authenticate", "/register").permitAll().
//    					// all other requests need to be authenticated
//    					anyRequest().authenticated().and().sessionManagement()
//    					// make sure we use stateless session; session won't be used to
//    					// store user's state.
////    					exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
//    					.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//    			// Add a filter to validate the tokens with every request
//    	http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//    	    }
//
////    @Override
////    protected void configure(HttpSecurity http) throws Exception {
////        http
////            // ...
////            .csrf().disable();
////    }
//    
    
//    @Autowired
//	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//
//	@Autowired
//	private UserDetailsService jwtUserDetailsService;
//
//	@Autowired
//	private JwtRequestFilter jwtRequestFilter;

//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//		// configure AuthenticationManager so that it knows from where to load
//		// user for matching credentials
//		// Use BCryptPasswordEncoder
//		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
//	}

//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//
//	@Bean
//	@Override
//	public AuthenticationManager authenticationManagerBean() throws Exception {
//		return super.authenticationManagerBean();
//	}

//	@Override
//	protected void configure(HttpSecurity httpSecurity) throws Exception {
//		// We don't need CSRF for this example
//		httpSecurity.csrf().disable()
//				// dont authenticate this particular request
//				.authorizeRequests().antMatchers("/authenticate").permitAll().
//				antMatchers("/**").permitAll().
//				// all other requests need to be authenticated
//				anyRequest().authenticated().and().
//				// make sure we use stateless session; session won't be used to
//				// store user's state.
//				exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
//				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//		// Add a filter to validate the tokens with every request
//		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//	}
	
//	 @Override
//	    protected void configure(HttpSecurity http) throws Exception {
//	        http
//	        .csrf().disable()   
//	        .authorizeRequests()
//	        .antMatchers(HttpMethod.POST,"/**").permitAll()
//	                .anyRequest().authenticated()
//	                .and()
//	            //.formLogin().and()
//	            .httpBasic();
//	    }
	
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
        // ...
        .csrf().disable();
}

    
    
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
	   List<String> lstheader = new ArrayList<String>();
	   lstheader.add("X-TenantID");
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowedOrigins(Arrays.asList("https://example.com"));
      configuration.setAllowedMethods(Arrays.asList("GET","POST"));
      configuration.setExposedHeaders(lstheader);
      configuration.setAllowedHeaders(lstheader);
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
      
    
  }
  
  
  
}
