package com.github.Frenadol.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private String name;
    private String password;
    private byte[] ProfileImage;
    private List<User> contacts;

    public User(String name, String password, byte[] profileImage, List<User> contacts) {
        this.name = name;
        this.password = password;
        ProfileImage = profileImage;
        this.contacts = contacts != null ? contacts : new ArrayList<>();
    }

    public User(String name, byte[] profileImage) {
        this.name = name;
        ProfileImage = profileImage;
    }

    public User() {
        this.contacts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getProfileImage() {
        return ProfileImage;
    }

    public List<User> getContacts() {
        if (contacts==null){
            contacts= new ArrayList<>();
        }
        return contacts;
    }

    public byte[] getProfileImagen() {
        return ProfileImage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setProfileImage(byte[] profileImage) {
        ProfileImage = profileImage;
    }

    public void setContacts(List<User> contacts) {
        this.contacts = contacts != null ? contacts : new ArrayList<>();
    }

    public void addContactToList(User contact) {
        if (contacts == null) {
            contacts = new ArrayList<>();
        }
        contacts.add(contact);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(password, user.password) && Objects.deepEquals(ProfileImage, user.ProfileImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password, ProfileImage);
    }

    @Override
    public String toString() {
        return "========== Informacion del usuario ==========\n" +
                "Name          : " + name + "\n" +
                "Password      : " + password + "\n" +
                "Profile Image : " + ProfileImage + "\n" +
                "================================";
    }


}