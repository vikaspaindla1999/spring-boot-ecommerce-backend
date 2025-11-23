package com.example.Ecommerce.controller;

import com.example.Ecommerce.model.AppRole;
import com.example.Ecommerce.model.Role;
import com.example.Ecommerce.model.User;
import com.example.Ecommerce.repository.RoleRepository;
import com.example.Ecommerce.repository.UserRepository;
import com.example.Ecommerce.security.Response.MessageResponse;
import com.example.Ecommerce.security.jwt.JwtUtils;
import com.example.Ecommerce.security.Request.LoginRequest;
import com.example.Ecommerce.security.Response.LoginResponse;
import com.example.Ecommerce.security.Request.SignUpRequest;
import com.example.Ecommerce.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;
    @Tag(name = "Auth APIs",description = "API for managing Auth")
    @Operation(summary = "Sign in",description = "API to Sign in")
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try {
            authentication=authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword())
            );

        }catch (AuthenticationException e){

            Map<String,Object> map=new HashMap<>();
            map.put("message","Bad Credentials");
            map.put("status",false);
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails= (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtToken= jwtUtils.generateJwtCookie(userDetails);
        List<String> roles=userDetails.getAuthorities().stream().map(item->item.getAuthority()).collect(Collectors.toList());
        LoginResponse response=new LoginResponse(userDetails.getId(), loginRequest.getUsername(), roles);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,jwtToken.toString()).body(response);
    }
    @Tag(name = "Auth APIs",description = "API for managing Auth")
    @Operation(summary = "Sign up",description = "API to Sign up")
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest request){
       if(userRepository.existsByUsername(request.getUsername())){
           return new ResponseEntity<>(new MessageResponse("Error: Username is already taken!"),HttpStatus.BAD_REQUEST);
       }
        if(userRepository.existsByEmail(request.getEmail())){
            return new ResponseEntity<>(new MessageResponse("Error: Email is already taken!"),HttpStatus.BAD_REQUEST);
        }

        User user=new User(
                request.getUsername(),
                request.getEmail(),
                encoder.encode(request.getPassword()));

        Set<String> strRoles=request.getRoles();
        Set<Role> roles=new HashSet<>();
        if(strRoles==null){
            Role userRole=roleRepository.findByRole(AppRole.ROLE_USER).orElseThrow(()->new RuntimeException("Error: Role not found!"));
            roles.add(userRole);
        }
        else{
            strRoles.forEach(role->{
                 switch (role){
                     case "admin":
                         Role adminRole=roleRepository.findByRole(AppRole.ROLE_ADMIN).orElseThrow(()->new RuntimeException("Error: Role not found!"));
                         roles.add(adminRole);
                         break;
                     case "seller":
                         Role sellerRole=roleRepository.findByRole(AppRole.ROLE_SELLER).orElseThrow(()->new RuntimeException("Error: Role not found!"));
                         roles.add(sellerRole);
                         break;
                     default:
                         Role userRole=roleRepository.findByRole(AppRole.ROLE_USER).orElseThrow(()->new RuntimeException("Error: Role not found!"));
                         roles.add(userRole);
                         break;
                 }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);

        return new ResponseEntity<>(new MessageResponse("User Registered Successfully!"),HttpStatus.OK);
    }
    @Tag(name = "Auth APIs",description = "API for managing Auth")
    @Operation(summary = "Get user details",description = "API to get user details")
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        UserDetailsImpl userDetails= (UserDetailsImpl) authentication.getPrincipal();

       List<String> roles= userDetails.getAuthorities().stream().map(item->item.getAuthority()).collect(Collectors.toList());
       LoginResponse response=new LoginResponse(userDetails.getId(), userDetails.getUsername(), roles);

       return new ResponseEntity<>(response,HttpStatus.OK);

    }
    @Tag(name = "Auth APIs",description = "API for managing Auth")
    @Operation(summary = "get username",description = "API to get username")
    @GetMapping("/username")
    public ResponseEntity<?> currentUsername(Authentication authentication){

        if(authentication!=null)
            return new ResponseEntity<>(authentication.getName(),HttpStatus.OK);
        else
            return new ResponseEntity<>("Please Login",HttpStatus.BAD_REQUEST);
    }
    @Tag(name = "Auth APIs",description = "API for managing Auth")
    @Operation(summary = "Sign out",description = "API to Sign out")
    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser(){
       ResponseCookie cookie= jwtUtils.getCleanCookie();

       return  ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(new MessageResponse("You've been Signed out!"));
    }
}
