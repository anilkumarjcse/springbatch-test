package com.aj.springbatch.springbatch_test.repository;

import com.aj.springbatch.springbatch_test.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
