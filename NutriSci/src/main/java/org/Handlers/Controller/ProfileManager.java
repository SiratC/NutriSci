package org.Handlers.Controller;

import org.Entity.Profile;
import org.Handlers.Database.DatabaseProfileDAO;

import java.util.Optional;
import java.util.UUID;

public class ProfileManager {

    private static ProfileManager instance;

    private final DatabaseProfileDAO profileDAO = new DatabaseProfileDAO();

    // private constructor to prevent instantiation
    private ProfileManager() {
    }

    // singleton implementation; returns instance of ProfileManager
    public static ProfileManager getInstance() {
        if (instance == null) {
            instance = new ProfileManager();
        }
        return instance;
    }

    public Profile loadProfile(UUID userID) {
        try {
            Optional<Profile> profile = profileDAO.findById(userID);
            if (profile.isPresent()) {
                return profile.get();
            } else {
                System.err.println("Profile not found for userID: " + userID);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error loading profile: " + e.getMessage());
            return null;
        }
    }

    public Profile loadProfileByName(String name) {
        try {
            Optional<Profile> profile = profileDAO.findByName(name);
            if (profile.isPresent()) {
                return profile.get();
            } else {
                System.err.println("Profile not found for name: " + name);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error loading profile by name: " + e.getMessage());
            return null;
        }
    }

    public void saveProfile(Profile profile) {
        try {
            if (profileDAO.existsByName(profile.getName())) {
                profileDAO.update(profile);
            } else {
                profileDAO.save(profile);
            }
        } catch (Exception e) {
            System.err.println("Error saving profile: " + e.getMessage());
        }
    }

    public void deleteProfile(UUID userID) {
        try {
            if (profileDAO.existsById(userID)) {
                profileDAO.delete(userID);
            } else {
                System.err.println("Profile not found for userID: " + userID);
            }
        } catch (Exception e) {
            System.err.println("Error deleting profile: " + e.getMessage());
        }
    }

    public boolean authenticate(String name, String password) {
        try {
            Optional<Profile> profile = profileDAO.findByName(name);
            if (profile.isPresent()) {
                return password != null && password.equals(profile.get().getPassword());
            } else {
                throw new Exception("Profile not found for: " + name);
            }
        } catch (Exception e) {
            System.err.println("Error authenticating profile: " + e.getMessage());
            return false;
        }
    }
}
