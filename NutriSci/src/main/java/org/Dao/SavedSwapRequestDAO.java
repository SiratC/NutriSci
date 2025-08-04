package org.Dao;

import org.Entity.SavedSwapRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavedSwapRequestDAO {
    
    /**
     * Save a new swap request to the database
     */
    UUID save(SavedSwapRequest savedSwapRequest) throws SQLException;
    
    /**
     * Update an existing swap request
     */
    void update(SavedSwapRequest savedSwapRequest) throws SQLException;
    
    /**
     * Delete a swap request by ID
     */
    void delete(UUID id) throws SQLException;
    
    /**
     * Find a swap request by ID
     */
    Optional<SavedSwapRequest> findById(UUID id) throws SQLException;
    
    /**
     * Find all swap requests for a specific profile
     */
    List<SavedSwapRequest> findByProfileId(UUID profileId) throws SQLException;
    
    /**
     * Find swap requests by name (for a specific profile)
     */
    List<SavedSwapRequest> findByProfileIdAndName(UUID profileId, String name) throws SQLException;
    
    /**
     * Update the lastUsedAt timestamp for a swap request
     */
    void updateLastUsedAt(UUID id) throws SQLException;
    
    /**
     * Get count of swap requests for a profile
     */
    int countByProfileId(UUID profileId) throws SQLException;
}