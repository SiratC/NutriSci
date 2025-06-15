package org.Handlers.Controller;

import org.Entity.Profile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {
  private Map<UUID, Profile> store = new HashMap<>();
  public Profile loadProfile(UUID userID) {
    return store.get(userID);
  }
  public void saveProfile(Profile profile) {
    store.put(profile.getUserID(), profile);
  }
}