package com.example.justchat;

public class Profiledata {
    String Firstname,Lastname,Age,Gmail,Phoneno,Address,Gender,Link;


    public Profiledata(String firstname, String lastname, String age, String gmail, String phoneno, String address, String gender, String link) {
        Firstname = firstname;
        Lastname = lastname;
        Age = age;
        Gmail = gmail;
        Phoneno = phoneno;
        Address = address;
        Gender = gender;
        Link = link;
    }

    public Profiledata(String firstname, String gmail) {
        Firstname = firstname;
        Gmail = gmail;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getGmail() {
        return Gmail;
    }

    public void setGmail(String gmail) {
        Gmail = gmail;
    }

    public String getPhoneno() {
        return Phoneno;
    }

    public void setPhoneno(String phoneno) {
        Phoneno = phoneno;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }
}
