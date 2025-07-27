package org.Dao;

import org.Entity.Profile;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileDAO {
    void save(Profile profile);

    void update(Profile profile);

    void delete(UUID id);

    Optional<Profile> findById(UUID id);

    List<Profile> findAll();

    Optional<Profile> findByName(String name);

    List<Profile> findBySex(String sex);

    List<Profile> findByUnits(String units);

    List<Profile> findByAgeRange(int minAge, int maxAge);

    List<Profile> findByHeightRange(BigDecimal minHeight, BigDecimal maxHeight);

    List<Profile> findByWeightRange(BigDecimal minWeight, BigDecimal maxWeight);

    boolean existsByName(String name);

    boolean existsById(UUID id);
}
