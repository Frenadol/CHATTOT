package com.github.Frenadol.model;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private String name;
    private String password;
    private byte[] ProfileImagen;
    private List<User> contacts = new ArrayList<>();

    public User(String name, String password, byte[] profileImagen, List<User> contacts) {
        this.name = name;
        this.password = password;
        ProfileImagen = profileImagen;
        this.contacts = contacts;
    }

    public List<User> getContacts() {
        return contacts;
    }

    public void setContacts(List<User> contacts) {
        this.contacts = contacts;
    }

    public User(String name, byte[] profileImagen) {
        this.name = name;
        ProfileImagen = profileImagen;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getProfileImagen() {
        return ProfileImagen;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfileImagen(byte[] profileImagen) {
        ProfileImagen = profileImagen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(password, user.password) && Objects.deepEquals(ProfileImagen, user.ProfileImagen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password,ProfileImagen);
    }



    @Override
    public String toString() {
        return "========== Informacion del usuario ==========\n" +
                "Name          : " + name + "\n" +
                "Password      : " + password + "\n" +
                "Profile Image : " + ProfileImagen + "\n" +
                "================================";
    }
    public void addContactToList(User newContact) {
        if (!contacts.contains(newContact)) {
            contacts.add(newContact);
        }
    }

}
