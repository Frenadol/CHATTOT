package com.github.Frenadol.model;

import java.util.Arrays;
import java.util.Objects;

public class User {
    private String name;
    private String password;
    private byte[] profileImagen;

    public User(String name, String password, byte[] profileImagen) {
        this.name = name;
        this.password = password;
        this.profileImagen = profileImagen;
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
        return profileImagen;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfileImagen(byte[] profileImagen) {
        this.profileImagen = profileImagen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(password, user.password) && Objects.deepEquals(profileImagen, user.profileImagen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password, Arrays.hashCode(profileImagen));
    }

    @Override
    public String toString() {
        return "========== Informacion del usuario ==========\n" +
                "Name          : " + name + "\n" +
                "Password      : " + password + "\n" +
                "Profile Image : " + Arrays.toString(profileImagen) + "\n" +
                "================================";
    }
}
