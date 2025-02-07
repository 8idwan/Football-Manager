package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:football.db"; // Nom de la base de données

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Connexion à SQLite établie.");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
        return conn;
    }

    public static void createTables() {
        String sqlEntraineur = """
            CREATE TABLE IF NOT EXISTS Entraineur (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT NOT NULL,
                prenom TEXT NOT NULL,
                date_naissance DATE NOT NULL
            );
        """;

        String sqlEquipe = """
            CREATE TABLE IF NOT EXISTS Equipe (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT NOT NULL,
                ville TEXT NOT NULL,
                pays TEXT NOT NULL,
                entraineur_id INTEGER,
                FOREIGN KEY (entraineur_id) REFERENCES Entraineur(id) ON DELETE SET NULL
            );
        """;

        String sqlJoueur = """
            CREATE TABLE IF NOT EXISTS Joueur (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT NOT NULL,
                prenom TEXT NOT NULL,
                poste TEXT NOT NULL,
                date_naissance DATE NOT NULL,
                equipe_id INTEGER,
                FOREIGN KEY (equipe_id) REFERENCES Equipe(id) ON DELETE SET NULL
            );
        """;

        String sqlMatch = """
            CREATE TABLE IF NOT EXISTS Match (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT NOT NULL,
                lieu TEXT NOT NULL,
                equipe1_id INTEGER NOT NULL,
                equipe2_id INTEGER NOT NULL,
                resultat TEXT,
                FOREIGN KEY (equipe1_id) REFERENCES Equipe(id) ON DELETE CASCADE,
                FOREIGN KEY (equipe2_id) REFERENCES Equipe(id) ON DELETE CASCADE
            );
        """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlEntraineur);
            stmt.execute(sqlEquipe);
            stmt.execute(sqlJoueur);
            stmt.execute(sqlMatch);
            System.out.println("Tables créées avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création des tables : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        connect(); // Tester la connexion
        createTables(); // Créer les tables
    }
}
