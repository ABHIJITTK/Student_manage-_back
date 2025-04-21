package com.example.Student.crud.controller;

import com.example.Student.crud.model.Address;
import com.example.Student.crud.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public List<Address> getAllAddress() {
        return addressService.getAllAddress();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getContactById(@PathVariable Long id) {
        Optional<Address> contact = addressService.getAddressById(id);
        return contact.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Address> createContact(@RequestBody Address address) {
        Address saveAddress = addressService.saveAddress(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveAddress);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateContact(@PathVariable Long id, @RequestBody Address address) {
        Optional<Address> existingContact = addressService.getAddressById(id);
        if (existingContact.isPresent()) {
            address.setId(id);
            Address updatedAddress = addressService.saveAddress(address);
            return ResponseEntity.ok(updatedAddress);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
