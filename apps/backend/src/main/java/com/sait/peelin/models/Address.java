package com.sait.peelin.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID addressId;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String province;
    private String postalCode;

    public Address(String addressLine1, String addressLine2, String city, String province, String postalCode) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
    }

    public Address() {

    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public CharSequence getProvince() {
        return province;
    }

    public String getPostalCode() {
        return postalCode;
    }


}
