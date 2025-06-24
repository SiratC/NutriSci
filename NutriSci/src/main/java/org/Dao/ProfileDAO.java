package org.Dao;

import org.Entity.Profile;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * An interface defining the saved data of a profile.
 */
public interface ProfileDAO {
    /**
     * Saves the profile with given information.
     * @param profile the profile information
     */
    void save(Profile profile);

    /**
     * Updates the profile with given information.
     * @param profile the profile information
     */
    void update(Profile profile);

    /**
     * Deletes the profile based on given ID.
     * @param id the ID of the profile
     */
    void delete(UUID id);

    /**
     * Finds a profile based on given ID.
     * @param id the ID of the profile
     * @return the profile
     */
    Optional<Profile> findById(UUID id);

    /**
     * Finds all profiles based on criteria given.
     * @return list of profiles
     */
    List<Profile> findAll();

    /**
     * Finds a profile based on given name.
     * @param name name of profile
     * @return profile
     */
    Optional<Profile> findByName(String name);

    /**
     * Finds profiles based on given sex.
     * @param sex sex of profile
     * @return list of profiles
     */
    List<Profile> findBySex(String sex);

    /**
     * Finds profiles based on given units.
     * @param units units of measurement used in profile
     * @return list of profiles
     */
    List<Profile> findByUnits(String units);

    /**
     * Finds profiles based on a given age range.
     * @param minAge minimum age found
     * @param maxAge maximum age found
     * @return list of profiles
     */
    List<Profile> findByAgeRange(int minAge, int maxAge);

    /**
     * Finds profiles based on a given height range.
     * @param minHeight minimum height found
     * @param maxHeight maximum height found
     * @return list of profiles
     */
    List<Profile> findByHeightRange(BigDecimal minHeight, BigDecimal maxHeight);

    /**
     * Finds profiles based on a given weight range.
     * @param minWeight minimum weight found
     * @param maxWeight maximum weight found
     * @return list of profiles
     */
    List<Profile> findByWeightRange(BigDecimal minWeight, BigDecimal maxWeight);

    /**
     * Checks if profile exists by name.
     * @param name name of profile
     * @return true if profile exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Checks if profile exists by ID.
     * @param id ID of profile
     * @return true if profile exists, false otherwise
     */
    boolean existsById(UUID id);
}
