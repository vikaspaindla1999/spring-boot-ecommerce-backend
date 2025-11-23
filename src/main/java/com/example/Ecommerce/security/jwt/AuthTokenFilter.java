package com.example.Ecommerce.security.jwt;

import com.example.Ecommerce.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private static final Logger logger= LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
          logger.debug("AuthTokenFilter called URI: {}",request.getRequestURI());
          try{
              String jwt=getJwt(request);
              if(jwt!=null && jwtUtils.validateJwtToken(jwt)){
                  String username=jwtUtils.getUsernameFromJwtToken(jwt);
                  UserDetails userDetails=userDetailsService.loadUserByUsername(username);
                  UsernamePasswordAuthenticationToken authentication= new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                  authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                  SecurityContextHolder.getContext().setAuthentication(authentication);
                  logger.debug("Roles from JWT: {}",userDetails.getAuthorities());
              }
          }catch (Exception e){
               logger.error("Cannot set authentication: {} ",e);
          }

          filterChain.doFilter(request,response);
    }

//    private String getJwt(HttpServletRequest request) {
//        String token=jwtUtils.getJwtFromCookie(request);
//        return token;
//    }

    private String getJwt(HttpServletRequest request) {
        String cookieToken=jwtUtils.getJwtFromCookie(request);
        if(cookieToken!=null){
            return cookieToken;
        }
        String jwtToken=jwtUtils.getJwtFromHeader(request);
        if (jwtToken!=null){
            return jwtToken;
        }
        return null;
    }
}
