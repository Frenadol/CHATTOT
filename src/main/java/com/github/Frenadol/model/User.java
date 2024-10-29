package com.github.Frenadol.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class User {
    private String name;
    private String password;
    private byte[] profileImage;
    private List<User> contacts;

    public User(String name, String password, byte[] profileImage, List<User> contacts) {
        this.name = name;
        this.password = password;
        this.profileImage = profileImage;
        this.contacts = contacts != null ? contacts : new ArrayList<>();
    }

    public User(String name, byte[] password) {
        this.name = name;
        this.password = new String(password);
        this.contacts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public List<User> getContacts() {
        return contacts;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public void setContacts(List<User> contacts) {
        this.contacts = contacts != null ? contacts : new ArrayList<>();
    }

    public void addContactToList(User contact) {
        if (!contacts.contains(contact)) {
            contacts.add(contact);
        }
    }
    /**
     * Checks if this User object is equal to another object.
     * Two User objects are considered equal if they are the same instance
     * or if they are of the same class and have the same name.
     *
     * @param obj the object to compare with this User
     * @return true if the objects are equal; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        return getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password, Arrays.hashCode(profileImage), contacts);
    }

    @Override
    public String toString() {
        return "========== Información del usuario ==========\n" +
                "Nombre        : " + name + "\n" +
                "Contraseña    : " + password + "\n" +
                "Imagen Perfil : " + (profileImage != null ? "Disponible" : "No disponible") + "\n" +
                "================================";
    }
}
