package com.example.Ecommerce.controller;

import com.example.Ecommerce.model.User;
import com.example.Ecommerce.payload.AddressDTO;
import com.example.Ecommerce.service.AddressService;
import com.example.Ecommerce.service.AddressServiceImpl;
import com.example.Ecommerce.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthUtil authUtil;
    @Tag(name = "Address APIs",description = "API for managing Address")
    @Operation(summary = "Create Address",description = "API to create address")
    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO address){
        User user= authUtil.loggedInUser();
        AddressDTO savedAddress=addressService.createAddress(address,user);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);

    }
    @Tag(name = "Address APIs",description = "API for managing Address")
    @Operation(summary = "Get Address",description = "API to get address")
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAddress(){
       List<AddressDTO> addresses=addressService.getAddresses();
        return new ResponseEntity<>(addresses, HttpStatus.OK);

    }
    @Tag(name = "Address APIs",description = "API for managing Address")
    @Operation(summary = "Get Address by Id",description = "API to get address by id")
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Integer addressId){
        AddressDTO addresses=addressService.getAddressesById(addressId);
        return new ResponseEntity<>(addresses, HttpStatus.OK);

    }
    @Tag(name = "Address APIs",description = "API for managing Address")
    @Operation(summary = "Get Address by user",description = "API to get address by user")
    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getAddressByUser(){
        User user= authUtil.loggedInUser();
        List<AddressDTO> savedAddress=addressService.getAddressByUser(user);
        return new ResponseEntity<>(savedAddress, HttpStatus.OK);

    }

    @Tag(name = "Address APIs",description = "API for managing Address")
    @Operation(summary = "Update Address",description = "API to update address by id")
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@PathVariable Integer addressId,@RequestBody AddressDTO addressDTO){
        AddressDTO addresses=addressService.updateAddressesById(addressDTO,addressId);
        return new ResponseEntity<>(addresses, HttpStatus.CREATED);

    }
    @Tag(name = "Address APIs",description = "API for managing Address")
    @Operation(summary = "Delete Address",description = "API to delete address by id")
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Integer addressId){
        String status=addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status, HttpStatus.OK);

    }


}
