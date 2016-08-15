package com.example.mtsihr.Models;

import android.media.Image;

import io.realm.RealmObject;


/**
 * Created by Виктор on 09.08.2016.
 */
public class Colleague extends RealmObject {
    public String name, post, phone, email, subdivision;

     public Colleague(String email, String name, String phone, String post, String subdivision) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.post = post;
        this.subdivision = subdivision;
    }

    public Colleague(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Colleague(String name, String phone, String email) {
        this.email = email;
        this.name = name;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPost() {
        return post;
    }

    public String getSubdivision() {
        return subdivision;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public void setSubdivision(String subdivision) {
        this.subdivision = subdivision;
    }

    public Colleague(){}
}
