package com.aj.springbatch.springbatch_test.config;


import com.aj.springbatch.springbatch_test.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer customer) throws Exception {
        if(customer.getSex().equalsIgnoreCase("female")){
            return customer;
        } else {
            return null;
        }
    }
}
