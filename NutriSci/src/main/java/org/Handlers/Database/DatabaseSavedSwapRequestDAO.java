package org.Handlers.Database;

import org.Dao.SavedSwapRequestDAO;
import org.Entity.SavedSwapRequest;
import org.Enums.NutrientType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DatabaseSavedSwapRequestDAO implements SavedSwapRequestDAO {

    @Override
    public UUID save(SavedSwapRequest savedSwapRequest) throws SQLException {
        String sql = """
                INSERT INTO SavedSwapRequests (profileId, name, description, targetNutrient, 
                intensityAmount, isPercentage, secondTargetNutrient, secondIntensityAmount, 
                secondIsPercentage, createdAt) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, savedSwapRequest.getProfileId());
            ps.setString(2, savedSwapRequest.getName());
            ps.setString(3, savedSwapRequest.getDescription());
            ps.setString(4, savedSwapRequest.getTargetNutrient().name());
            ps.setDouble(5, savedSwapRequest.getIntensityAmount());
            ps.setBoolean(6, savedSwapRequest.isPercentage());
            
            if (savedSwapRequest.hasSecondTarget()) {
                ps.setString(7, savedSwapRequest.getSecondTargetNutrient().name());
                ps.setDouble(8, savedSwapRequest.getSecondIntensityAmount());
                ps.setBoolean(9, savedSwapRequest.isSecondPercentage());
            } else {
                ps.setNull(7, Types.VARCHAR);
                ps.setNull(8, Types.DECIMAL);
                ps.setNull(9, Types.BOOLEAN);
            }
            
            ps.setTimestamp(10, Timestamp.valueOf(savedSwapRequest.getCreatedAt()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UUID generatedId = (UUID) rs.getObject("id");
                    savedSwapRequest.setId(generatedId);
                    return generatedId;
                }
            }
        }
        throw new SQLException("Failed to save SavedSwapRequest");
    }

    @Override
    public void update(SavedSwapRequest savedSwapRequest) throws SQLException {
        String sql = """
                UPDATE SavedSwapRequests 
                SET name = ?, description = ?, targetNutrient = ?, intensityAmount = ?, 
                isPercentage = ?, secondTargetNutrient = ?, secondIntensityAmount = ?, 
                secondIsPercentage = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, savedSwapRequest.getName());
            ps.setString(2, savedSwapRequest.getDescription());
            ps.setString(3, savedSwapRequest.getTargetNutrient().name());
            ps.setDouble(4, savedSwapRequest.getIntensityAmount());
            ps.setBoolean(5, savedSwapRequest.isPercentage());
            
            if (savedSwapRequest.hasSecondTarget()) {
                ps.setString(6, savedSwapRequest.getSecondTargetNutrient().name());
                ps.setDouble(7, savedSwapRequest.getSecondIntensityAmount());
                ps.setBoolean(8, savedSwapRequest.isSecondPercentage());
            } else {
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.DECIMAL);
                ps.setNull(8, Types.BOOLEAN);
            }
            
            ps.setObject(9, savedSwapRequest.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("No SavedSwapRequest found with id: " + savedSwapRequest.getId());
            }
        }
    }

    @Override
    public void delete(UUID id) throws SQLException {
        String sql = "DELETE FROM SavedSwapRequests WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);
            
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                throw new SQLException("No SavedSwapRequest found with id: " + id);
            }
        }
    }

    @Override
    public Optional<SavedSwapRequest> findById(UUID id) throws SQLException {
        String sql = """
                SELECT id, profileId, name, description, targetNutrient, intensityAmount, 
                isPercentage, secondTargetNutrient, secondIntensityAmount, secondIsPercentage, 
                createdAt, lastUsedAt
                FROM SavedSwapRequests 
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSavedSwapRequest(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<SavedSwapRequest> findByProfileId(UUID profileId) throws SQLException {
        String sql = """
                SELECT id, profileId, name, description, targetNutrient, intensityAmount, 
                isPercentage, secondTargetNutrient, secondIntensityAmount, secondIsPercentage, 
                createdAt, lastUsedAt
                FROM SavedSwapRequests 
                WHERE profileId = ?
                ORDER BY createdAt DESC
                """;

        List<SavedSwapRequest> results = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, profileId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResultSetToSavedSwapRequest(rs));
                }
            }
        }
        return results;
    }

    @Override
    public List<SavedSwapRequest> findByProfileIdAndName(UUID profileId, String name) throws SQLException {
        String sql = """
                SELECT id, profileId, name, description, targetNutrient, intensityAmount, 
                isPercentage, secondTargetNutrient, secondIntensityAmount, secondIsPercentage, 
                createdAt, lastUsedAt
                FROM SavedSwapRequests 
                WHERE profileId = ? AND name ILIKE ?
                ORDER BY createdAt DESC
                """;

        List<SavedSwapRequest> results = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, profileId);
            ps.setString(2, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResultSetToSavedSwapRequest(rs));
                }
            }
        }
        return results;
    }

    @Override
    public void updateLastUsedAt(UUID id) throws SQLException {
        String sql = "UPDATE SavedSwapRequests SET lastUsedAt = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);
            
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("No SavedSwapRequest found with id: " + id);
            }
        }
    }

    @Override
    public int countByProfileId(UUID profileId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM SavedSwapRequests WHERE profileId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, profileId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private SavedSwapRequest mapResultSetToSavedSwapRequest(ResultSet rs) throws SQLException {
        SavedSwapRequest savedSwapRequest = new SavedSwapRequest();
        
        savedSwapRequest.setId((UUID) rs.getObject("id"));
        savedSwapRequest.setProfileId((UUID) rs.getObject("profileId"));
        savedSwapRequest.setName(rs.getString("name"));
        savedSwapRequest.setDescription(rs.getString("description"));
        savedSwapRequest.setTargetNutrient(NutrientType.valueOf(rs.getString("targetNutrient")));
        savedSwapRequest.setIntensityAmount(rs.getDouble("intensityAmount"));
        savedSwapRequest.setPercentage(rs.getBoolean("isPercentage"));
        
        String secondTarget = rs.getString("secondTargetNutrient");
        if (secondTarget != null) {
            savedSwapRequest.setSecondTargetNutrient(NutrientType.valueOf(secondTarget));
            savedSwapRequest.setSecondIntensityAmount(rs.getDouble("secondIntensityAmount"));
            savedSwapRequest.setSecondPercentage(rs.getBoolean("secondIsPercentage"));
        }
        
        Timestamp createdAt = rs.getTimestamp("createdAt");
        if (createdAt != null) {
            savedSwapRequest.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp lastUsedAt = rs.getTimestamp("lastUsedAt");
        if (lastUsedAt != null) {
            savedSwapRequest.setLastUsedAt(lastUsedAt.toLocalDateTime());
        }
        
        return savedSwapRequest;
    }
}