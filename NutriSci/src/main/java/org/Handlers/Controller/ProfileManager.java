package org.Handlers.Controller;

import org.Entity.Profile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the profile created by the user.
 * <p>Handles saving and loading of profile information.</p>
 */
public class ProfileManager {
  private Map<UUID, Profile> store = new HashMap<>();

  /**
   * Loads the profile based on given user ID.
   *
   * @param userID the ID of the profile
   * @return the profile
   */
  public Profile loadProfile(UUID userID) {
    return store.get(userID);
  }

  /**
   * Saves the profile to store.
   *
   * @param profile the profile information to be saved
   */
  public void saveProfile(Profile profile) {
    store.put(profile.getUserID(), profile);
  }
}