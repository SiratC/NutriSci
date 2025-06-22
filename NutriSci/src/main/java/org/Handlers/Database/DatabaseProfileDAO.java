package org.Handlers.Database;

import org.Dao.ProfileDAO;
import org.Entity.Profile;
import org.Enums.Sex;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * An implementation of ProfileDAO, controlling the user interactions for profile managing such as saving, deleting, updating, etc.
 */
public class DatabaseProfileDAO implements ProfileDAO {
    /**
     * Saves the information based on given profile settings.
     *
     * @param profile the profile to be saved
     * @throws RuntimeException if profile isn't saved
     */
    @Override
    public void save(Profile profile) {
        String sql = """
            INSERT INTO Profiles (id, name, sex, dob, height, weight, units, createdAt, modifiedAt)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, profile.getUserID());
            stmt.setString(2, profile.getName());
            stmt.setString(3, profile.getSex().name());
            stmt.setDate(4, Date.valueOf(profile.getDob()));
            stmt.setDouble(5, profile.getHeight());
            stmt.setDouble(6, profile.getWeight());
            stmt.setString(7, profile.getUnits());
            stmt.setTimestamp(8, Timestamp.valueOf(profile.getCreatedAt()));
            stmt.setTimestamp(9, profile.getModifiedAt() != null ?
                    Timestamp.valueOf(profile.getModifiedAt()) : null);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving profile: " + e.getMessage(), e);
        }
    }

    /**
     * Updates existing data within profile with changed settings given.
     *
     * @param profile the profile to be saved
     * @throws RuntimeException if profile isn't found with given ID
     * @throws RuntimeException if profile isn't updated
     */
    @Override
    public void update(Profile profile) {
        String sql = """
            UPDATE Profiles
            SET name = ?, sex = ?, dob = ?, height = ?, weight = ?, units = ?, modifiedAt = ?
            WHERE id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profile.getName());
            stmt.setString(2, profile.getSex().name());
            stmt.setDate(3, Date.valueOf(profile.getDob()));
            stmt.setDouble(4, profile.getHeight());
            stmt.setDouble(5, profile.getWeight());
            stmt.setString(6, profile.getUnits());
            stmt.setTimestamp(7, Timestamp.valueOf(profile.getModifiedAt()));
            stmt.setObject(8, profile.getUserID());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Profile not found with id: " + profile.getUserID());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating profile: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes the profile found through given ID.
     *
     * @param id identification of the profile
     * @throws RuntimeException if profile isn't found with given ID
     * @throws RuntimeException if profile isn't deleted
     */
    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM Profiles WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Profile not found with id: " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting profile: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a profile based on given ID.
     *
     * @param id the identification of the profile
     * @return the profile
     * @throws RuntimeException if profile isn't found with given ID
     */
    @Override
    public Optional<Profile> findById(UUID id) {
        String sql = "SELECT * FROM Profiles WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProfile(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding profile by id: " + e.getMessage(), e);
        }
    }

    /**
     * Finds all profile sorted based off of user specifications such as creation date.
     *
     * @return list of profiles
     * @throws RuntimeException if all profiles aren't found
     */
    @Override
    public List<Profile> findAll() {
        String sql = "SELECT * FROM Profiles ORDER BY createdAt DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<Profile> profiles = new ArrayList<>();
            while (rs.next()) {
                profiles.add(mapResultSetToProfile(rs));
            }
            return profiles;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all profiles: " + e.getMessage(), e);
        }
    }

    /**
     * Finds and returns profile based off of the name.
     *
     * @param name the name of profile
     * @return a profile
     * @throws RuntimeException if profile isn't found with given ID
     */
    @Override
    public Optional<Profile> findByName(String name) {
        // NOTE: Profile class doesn't have a name field, but database schema does
        // This method assumes the database has a 'name' column that isn't mapped to the Profile class
        String sql = "SELECT * FROM Profiles WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProfile(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding profile by name: " + e.getMessage(), e);
        }
    }

    /**
     * Finds and returns profile based off of the sex.
     *
     * @param sex the sex of profile
     * @return list of profiles found
     * @throws RuntimeException if profiles aren't found with given sex
     */
    @Override
    public List<Profile> findBySex(String sex) {
        String sql = "SELECT * FROM Profiles WHERE sex = ? ORDER BY dob DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sex);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Profile> profiles = new ArrayList<>();
                while (rs.next()) {
                    profiles.add(mapResultSetToProfile(rs));
                }
                return profiles;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding profiles by sex: " + e.getMessage(), e);
        }
    }

    /**
     * Finds and returns a list of profiles based on the units chosen.
     *
     * @param units the unit of measurement in profile
     * @return list of profiles found
     * @throws RuntimeException if profiles aren't found with given units
     */
    @Override
    public List<Profile> findByUnits(String units) {
        String sql = "SELECT * FROM Profiles WHERE units = ? ORDER BY dob DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, units);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Profile> profiles = new ArrayList<>();
                while (rs.next()) {
                    profiles.add(mapResultSetToProfile(rs));
                }
                return profiles;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding profiles by units: " + e.getMessage(), e);
        }
    }

    /**
     * Finds and returns a list of profiles based on an age range specified
     *
     * @param minAge minimum age in range criteria
     * @param maxAge maximum age in range criteria
     * @return list of profiles found
     * @throws RuntimeException if profiles aren't found with given age range
     */
    @Override
    public List<Profile> findByAgeRange(int minAge, int maxAge) {
        String sql = """
            SELECT * FROM Profiles
            WHERE EXTRACT(YEAR FROM AGE(CURRENT_DATE, dob)) BETWEEN ? AND ?
            ORDER BY dob DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, minAge);
            stmt.setInt(2, maxAge);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Profile> profiles = new ArrayList<>();
                while (rs.next()) {
                    profiles.add(mapResultSetToProfile(rs));
                }
                return profiles;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding profiles by age range: " + e.getMessage(), e);
        }
    }

    /**
     * Finds and returns a list of profiles based on an age range specified
     *
     * @param minHeight minimum height in range criteria
     * @param maxHeight maximum height in range criteria
     * @return list of profiles found
     * @throws RuntimeException if profiles aren't found with given height
     */
    @Override
    public List<Profile> findByHeightRange(BigDecimal minHeight, BigDecimal maxHeight) {
        String sql = "SELECT * FROM Profiles WHERE height BETWEEN ? AND ? ORDER BY height";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, minHeight.doubleValue());
            stmt.setDouble(2, maxHeight.doubleValue());

            try (ResultSet rs = stmt.executeQuery()) {
                List<Profile> profiles = new ArrayList<>();
                while (rs.next()) {
                    profiles.add(mapResultSetToProfile(rs));
                }
                return profiles;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding profiles by height range: " + e.getMessage(), e);
        }
    }

    /**
     * Finds and returns a list of profiles based on a weight range specified
     *
     * @param minWeight minimum weight in range criteria
     * @param maxWeight maximum weight in range criteria
     * @return list of profiles found
     * @throws RuntimeException if profiles aren't found with given weight range
     */
    @Override
    public List<Profile> findByWeightRange(BigDecimal minWeight, BigDecimal maxWeight) {
        String sql = "SELECT * FROM Profiles WHERE weight BETWEEN ? AND ? ORDER BY weight";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, minWeight.doubleValue());
            stmt.setDouble(2, maxWeight.doubleValue());

            try (ResultSet rs = stmt.executeQuery()) {
                List<Profile> profiles = new ArrayList<>();
                while (rs.next()) {
                    profiles.add(mapResultSetToProfile(rs));
                }
                return profiles;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding profiles by weight range: " + e.getMessage(), e);
        }
    }

    /**
     * Searches for profile based on name and returns true if it exists.
     *
     * @param name the name of the profile
     * @return true if profile exists, false otherwise
     * @throws RuntimeException if error occurs during searching of given name
     */
    @Override
    public boolean existsByName(String name) {
        // NOTE: Profile class doesn't have a name field, but database schema does
        String sql = "SELECT COUNT(*) FROM Profiles WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error checking if profile exists by name: " + e.getMessage(), e);
        }
    }

    /**
     * Searches for profile based on name and returns true if it exists.
     *
     * @param id the ID of the profile
     * @return true if profile exists, false otherwise
     * @throws RuntimeException if error occurs during searching of given ID
     */
    @Override
    public boolean existsById(UUID id) {
        String sql = "SELECT COUNT(*) FROM Profiles WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error checking if profile exists by id: " + e.getMessage(), e);
        }
    }

    /**
     * Returns and creates a profile based on a given SQL result.
     *
     * @param rs a given ResultSet to create into a profile
     * @return a profile containing the data given
     * @throws SQLException if database error occurs
     */
    private Profile mapResultSetToProfile(ResultSet rs) throws SQLException {
        UUID userID = (UUID) rs.getObject("id");
        String name = rs.getString("name");
        Sex sex = Sex.valueOf(rs.getString("sex"));
        LocalDate dob = rs.getDate("dob").toLocalDate();
        double height = rs.getDouble("height");
        double weight = rs.getDouble("weight");
        String units = rs.getString("units");
        LocalDateTime createdAt = rs.getTimestamp("createdAt").toLocalDateTime();

        Timestamp modifiedAtTimestamp = rs.getTimestamp("modifiedAt");
        LocalDateTime modifiedAt = modifiedAtTimestamp != null ?
                modifiedAtTimestamp.toLocalDateTime() : null;

        return new Profile(userID, name, sex, dob, height, weight, units, createdAt, modifiedAt);
    }
}