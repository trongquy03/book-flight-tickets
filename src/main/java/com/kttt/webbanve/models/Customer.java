package com.kttt.webbanve.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer")

public class Customer {
    @Valid
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customerID")
    private int customerID;

    @NotBlank(message = "Full name is required!")
    @Column(name = "fullname")
    private String fullname;

    @Size(max = 3)
    @Column(name = "sex")
    private String sex;

    @DateTimeFormat(pattern = "dd/mm/yyyy")
    @Column(name = "birthday")
    private String birthday;

    @NotBlank(message = "Nationality is required!")
    @Column(name = "nationality")
    private String nationality;

    @NotBlank(message = "Citizen identification is required!")
    @Column(name = "citizen_identification")
    private  String citizenIdentification;

    @Column(name = "phone")
    private String phone;

    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+@[a-zA-Z0-9-.]+$")
    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userID", referencedColumnName = "userID")
    private User user;

}
