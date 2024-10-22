package com.github.Frenadol.utils;

import com.github.Frenadol.model.User;

/**
 * Singleton class for managing user sessions.
 */
public class SessionManager {
    /** Singleton instance */
    private static final SessionManager instance = new SessionManager();

    /** Current user logged in */
    private User currentUser;

    /** Private constructor to prevent instantiation from outside */
    private SessionManager() {}

    /**
     * Get the singleton instance of SessionManager.
     * @return The singleton instance of SessionManager.
     */
    public static SessionManager getInstance() {
        return instance;
    }

    /**
     * Set the current user for the session.
     * @param user The current user to set.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Get the current user for the session.
     * @return The current user for the session, or null if no user is set.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Clear the current user session (logout).
     */
    public void clearSession() {
        this.currentUser = null;
    }
}
