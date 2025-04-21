package com.example.Student.crud.service;

import com.example.Student.crud.model.Address;
import com.example.Student.crud.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public List<Address> getAllAddress(){
        return addressRepository.findAll();
    }
    public Optional<Address> getAddressById(Long id){
        return addressRepository.findById(id);
    }

    public Address saveAddress(Address address){
        return addressRepository.save(address);
    }
    public Address updateAddress(Long id,Address address){
        if(addressRepository.existsById(id)){
            address.setId(id);
            return addressRepository.save(address);
        }else{
            throw new RuntimeException("address not found");
        }
    }
    public void deleteAddress(Long id){
        addressRepository.deleteById(id);
    }
}
