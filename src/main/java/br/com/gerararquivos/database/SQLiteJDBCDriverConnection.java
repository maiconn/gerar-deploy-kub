package br.com.gerararquivos.database;

import java.sql.*;

public class SQLiteJDBCDriverConnection {
    private static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection("jdbc:sqlite:memory");

//        return DriverManager.getConnection("jdbc:sqlite:c:/tmp/teste.db");
        return DriverManager.getConnection("jdbc:sqlite:/usr/local/bin/upkub/portas.db");
    }

    public synchronized static Integer getPorta(String urlGit) throws SQLException {
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();

            // criando tabela
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS CONTROLE_PORTA (REPO VARCHAR, PORTA INT)
                    """);


            PreparedStatement stmt = connection.prepareStatement("""                  
                         select PORTA
                           from CONTROLE_PORTA
                          where REPO = ?
                    """);
            stmt.setString(1, urlGit);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("PORTA");
            } else {
                Integer proxima = getNextPort(connection);
                PreparedStatement insert = connection.prepareStatement("INSERT INTO CONTROLE_PORTA(REPO, PORTA) VALUES (?, ?)");
                insert.setString(1, urlGit);
                insert.setInt(2, proxima);
                insert.executeUpdate();
                return proxima;
            }
        }
    }

    private static int getNextPort(Connection connection) throws SQLException {
        PreparedStatement stmt;
        ResultSet resultSet;
        stmt = connection.prepareStatement("select max(PORTA) as ultima_porta from CONTROLE_PORTA");
        resultSet = stmt.executeQuery();

        while (resultSet.next()) {
            Integer ultima = resultSet.getInt("ultima_porta");
            if (!Integer.valueOf(0).equals(ultima)) {
                return ultima + 1;
            }
        }
        return 9090;
    }
}
