import static org.junit.jupiter.api.Assertions.*;
import org.Entity.Profile;
import org.Enums.Sex;
import org.Handlers.Controller.ProfileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProfileManagerTest {

    private ProfileManager profileManager;
    private Profile profile;

    @BeforeEach
    void setUp() {
        profileManager = ProfileManager.getInstance();

        profile = new Profile(
                UUID.randomUUID(),
                "Alex",
                "pass123",
                Sex.Female,
                LocalDate.of(2000, 1, 1),
                165.0,
                60.0,
                "metric",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        profileManager.saveProfile(profile);

        profile = profileManager.loadProfileByName("Alex");
    }

    @Test
    void loadProfile_shouldReturnSavedProfile() {
        Profile loaded = profileManager.loadProfile(profile.getUserID());
        assertNotNull(loaded);
        assertEquals("Alex", loaded.getName());
    }

    @Test
    void authenticate_shouldReturnTrueForValidCredentials() {
        assertTrue(profileManager.authenticate("Alex", "pass123"));
    }

    @Test
    void authenticate_shouldReturnFalseForInvalidPassword() {
        assertFalse(profileManager.authenticate("Alex", "wrongPassword"));
    }

    @Test
    void authenticate_shouldReturnFalseForNullPassword() {
        assertFalse(profileManager.authenticate("Alex", null));
    }
}


