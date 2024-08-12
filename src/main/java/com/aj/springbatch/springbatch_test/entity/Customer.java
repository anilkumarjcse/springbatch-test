package com.aj.springbatch.springbatch_test.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="customers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @Column(name="id")
    private int  id;

    @Column(name="customer_id")
    private String userId;
//    id,userId,firstName,lastName,sex,email,phone,dateOfBirth,job title
    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String  lastName;

    @Column(name="gender")
    private String  sex;

    @Column(name="email")
    private String email;

    @Column(name="phone")
    private String phone;

    @Column(name="date_of_birth")
    private String  dateOfBirth;

    @Column(name="title")
    private String title;
}
