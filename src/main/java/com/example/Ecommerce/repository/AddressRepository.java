package com.example.Ecommerce.repository;

import com.example.Ecommerce.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address,Integer> {
    @Query("SELECT a FROM Address a WHERE a.user.id=?1")
    List<Address> findAddressesByUserId(Integer userId);
}
