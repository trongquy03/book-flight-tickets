package com.kttt.webbanve.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_infor")
public class OrderInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderID")
    private int orderID;

    @Column(name="status")
    private int status;

    @Column(name = "total_cost")
    private long total_cost;

    @Column(name = "date")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "customerID",referencedColumnName = "customerID")
    public Customer customer;

    @Column(name = "qr_code")
    private String qrCode;
}
