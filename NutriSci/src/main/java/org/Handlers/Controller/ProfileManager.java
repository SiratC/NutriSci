package org.Handlers.Controller;
import org.Entity.Profile;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {

  private static ProfileManager instance;

  private final Map<UUID, Profile> store = new HashMap<>();

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
    return store.get(userID);
  }

  public void saveProfile(Profile profile) {
    if (profile != null && profile.getUserID() != null) {
      store.put(profile.getUserID(), profile);
    }
  }

  public void deleteProfile(UUID userID) {
    store.remove(userID);
  }

  public boolean authenticate(UUID userID, String password) {
    Profile profile = store.get(userID);
    return profile != null && password != null && password.equals(profile.getPassword());
  }

}



