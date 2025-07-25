 package com.example.demo.jjwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.security.MyUserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private ApplicationContext context;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
//		 String authHeader = request.getHeader("Authorization");
//		    String token = null;
//		    String username = null;
//		    if (authHeader != null && authHeader.startsWith("Bearer ")) {
//		        token = authHeader.substring(7);
//		      username=jwtUtil.extractUsername(token);
//		    }
		String authHeader = request.getHeader("Authorization");
	    System.out.println("---- JWT Filter ----");
	    System.out.println("Auth Header: " + authHeader);

	    String token = null, username = null;
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        token = authHeader.substring(7);
	        System.out.println("Extracted Token: " + token);

	        try {
	            username = jwtUtil.extractUsername(token);
	            System.out.println("Username from token: " + username);
	        } catch (Exception e) {
	            System.err.println("Failed to extract username: " + e.getMessage());
	        }
	    }


		    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
		        UserDetails userDetails =  context.getBean(MyUserService.class).loadUserByUsername(username);
		        if(jwtUtil.valideToken(token, userDetails)) {
		        	UsernamePasswordAuthenticationToken token1=new UsernamePasswordAuthenticationToken( userDetails,null,userDetails.getAuthorities());
		        	token1.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		        	SecurityContextHolder.getContext().setAuthentication(token1);
		        }
		    }

		    filterChain.doFilter(request, response);
		    
		
	}

}