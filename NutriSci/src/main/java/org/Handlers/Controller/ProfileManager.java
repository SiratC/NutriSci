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

  private static ProfileManager instance;

  private final Map<UUID, Profile> store = new HashMap<>();

  /**
   * Private constructor to prevent instantiation
   */
  private ProfileManager() {
  }

  /**
   * Singleton implementation; returns instance of ProfileManager
   * @return profile manager instance
   */
  public static ProfileManager getInstance() {
    if (instance == null) {
      instance = new ProfileManager();
    }
    return instance;
  }

  /**
   * Loads a profile with given user ID.
   * @param userID user's ID
   * @return profile
   */
  public Profile loadProfile(UUID userID) {
    return store.get(userID);
  }

  /**
   * Saves the profile.
   * @param profile profile to be saved
   */
  public void saveProfile(Profile profile) {
    if (profile != null && profile.getUserID() != null) {
      store.put(profile.getUserID(), profile);
    }
  }

  /**
   * Deletes the profile.
   * @param userID user's ID
   */
  public void deleteProfile(UUID userID) {
    store.remove(userID);
  }

  /**
   * Authenticates the profile based on ID and password given.
   * @param userID user's ID
   * @param password user's password
   * @return true if profile and password are valid, false otherwise
   */
  public boolean authenticate(UUID userID, String password) {
    Profile profile = store.get(userID);
    return profile != null && password != null && password.equals(profile.getPassword());
  }

}



