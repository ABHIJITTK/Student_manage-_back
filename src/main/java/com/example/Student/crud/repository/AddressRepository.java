package com.example.Student.crud.repository;

import com.example.Student.crud.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,Long> {
}
