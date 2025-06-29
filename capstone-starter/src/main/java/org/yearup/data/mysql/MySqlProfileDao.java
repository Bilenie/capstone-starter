package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao {


    public MySqlProfileDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile) {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Profile getByUserId(int id) {
        String sql = "SELECT p.* FROM profiles p WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                } else {
                    return null; // Return null so controller throws 404
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving category with ID: " + id, e);
        }
    }

    @Override
    public Profile update(int userId, Profile profile) {
        String sql = """
                UPDATE profiles
                       SET first_name = ?
                         , last_name = ?
                         , phone = ?
                         , email = ?
                         , address = ?
                         , city = ?
                         , state = ?
                         , zip = ?
                     WHERE user_id = ?;
                """;

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, profile.getFirstName());
            statement.setString(2, profile.getLastName());
            statement.setString(3, profile.getPhone());
            statement.setString(4, profile.getEmail());
            statement.setString(5, profile.getAddress());
            statement.setString(6, profile.getCity());
            statement.setString(7, profile.getState());
            statement.setString(8, profile.getZip());
            statement.setInt(9, userId); // assuming userId is passed as method parameter

            statement.executeUpdate();
            //  Actually execute the update
            int rowsAffected = statement.executeUpdate();

            // Check if the update actually changed something
            if (rowsAffected == 0) {
                throw new RuntimeException("user ID " + userId + " not found. Nothing was updated.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return profile;
    }

    public static Profile mapRow(ResultSet row) throws SQLException {
        int userId = row.getInt("user_id");
        String firstName = row.getString("first_name");
        String lastName = row.getString("last_name");
        String phone = row.getString("phone");
        String email = row.getString("email");
        String address = row.getString("address");
        String city = row.getString("city");
        String state = row.getString("state");
        String zip = row.getString("zip");


        return new Profile(userId, firstName, lastName, phone, email, address, city, state, zip);
    }
}
