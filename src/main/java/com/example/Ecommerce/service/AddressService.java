package com.example.Ecommerce.service;

import com.example.Ecommerce.model.User;
import com.example.Ecommerce.payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO address, User user);

    List<AddressDTO> getAddresses();

    AddressDTO getAddressesById(Integer addressId);

    List<AddressDTO> getAddressByUser(User user);


    AddressDTO updateAddressesById(AddressDTO addressDTO, Integer addressId);

    String deleteAddress(Integer addressId);
}
