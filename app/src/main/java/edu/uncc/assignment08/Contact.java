package edu.uncc.assignment08;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Contact implements Serializable {
    public String created_by_uid;
    public String name;
    public String email;
    public String contact_id;

    public ArrayList<PhoneNumber> phone_numbers;

    public ArrayList<PhoneNumber> getPhone_numbers() {
        return phone_numbers;
    }

    public void setPhone_numbers(ArrayList<PhoneNumber> phone_numbers) {
        this.phone_numbers = phone_numbers;
    }

    public Contact() {
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }



    public String getCreated_by_uid() {
        return created_by_uid;
    }

    public void setCreated_by_uid(String created_by_uid) {
        this.created_by_uid = created_by_uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    @Override
    public String toString() {
        return "Contact{" +
                "created_by_uid='" + created_by_uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone_numbers=" + phone_numbers +
                '}';
    }
}
