package com.example.Ecommerce.service;

import com.example.Ecommerce.exceptions.APIException;
import com.example.Ecommerce.exceptions.ResourceNotFoundException;
import com.example.Ecommerce.model.Address;
import com.example.Ecommerce.model.User;
import com.example.Ecommerce.payload.AddressDTO;
import com.example.Ecommerce.repository.AddressRepository;
import com.example.Ecommerce.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address=modelMapper.map(addressDTO, Address.class);
        List<Address> addressList=user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);
        address.setUser(user);
        Address savedAddress=addressRepository.save(address);

        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
       List<Address> addresses= addressRepository.findAll();
       List<AddressDTO> addressDTOS=addresses.stream().map(address -> modelMapper.map(address,AddressDTO.class)).toList();
       return addressDTOS;
    }

    @Override
    public AddressDTO getAddressesById(Integer addressId) {
        Address address=addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","AddressId",addressId));
        AddressDTO addressDTO=modelMapper.map(address,AddressDTO.class);
        return addressDTO;

    }

    @Override
    public List<AddressDTO> getAddressByUser(User user) {
       //List<Address> addresses= addressRepository.findAddressesByUserId(user.getUserId());
        List<Address> addresses=user.getAddresses();
       if (addresses.isEmpty()){
           throw  new APIException("No addresses found with user: "+user.getUsername());
       }
       List<AddressDTO> addressDTOS=addresses.stream().map(address -> modelMapper.map(address,AddressDTO.class)).toList();
       return addressDTOS;
    }

    @Override
    public AddressDTO updateAddressesById(AddressDTO addressDTO, Integer addressId) {
        Address address=addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","AddressId",addressId));
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setPincode(addressDTO.getPincode());
        address.setCountry(addressDTO.getCountry());
        address.setStreet(addressDTO.getStreet());

        Address updatedAddress=addressRepository.save(address);
        User user=address.getUser();
        user.getAddresses().removeIf(address1 -> address1.getId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);
        return modelMapper.map(updatedAddress,AddressDTO.class);
    }

    @Override
    public String deleteAddress(Integer addressId) {
        Address address=addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","AddressId",addressId));
        User user= address.getUser();
        user.getAddresses().removeIf(address1 -> address1.getId().equals(addressId));
        addressRepository.deleteById(addressId);
        return "Address deleted with addressId: "+addressId;
    }


}
