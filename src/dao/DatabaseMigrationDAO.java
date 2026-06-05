package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMigrationDAO {

    public void pastikanKolomPetugasTransaksi(Connection connection) throws SQLException {
        String cekSql = """
                SELECT COUNT(*) AS jumlah
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = 'transaksi'
                  AND COLUMN_NAME = 'id_user'
                """;

        try (PreparedStatement statement = connection.prepareStatement(cekSql);
             ResultSet result = statement.executeQuery()) {
            if (result.next() && result.getInt("jumlah") > 0) {
                return;
            }
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("ALTER TABLE transaksi ADD COLUMN id_user INT NULL AFTER id_anggota");
        }
    }
}
