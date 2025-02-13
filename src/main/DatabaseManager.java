package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe gérant la connexion à la base de données SQLite et la création des tables.
 */
public class DatabaseManager {
    
    /** URL de la base de données SQLite. */
    private static final String URL = "jdbc:sqlite:football.db";

    /**
     * Établit une connexion à la base de données SQLite.
     * 
     * @return Connexion à la base de données, ou null si la connexion échoue.
     */
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

    /**
     * Crée les tables nécessaires pour l'application si elles n'existent pas déjà.
     * La base de données contient les tables suivantes : Entraineur, Equipe, Joueur, Match.
     */
    public static void createTables() {
        // Requête pour créer la table Entraineur
        String sqlEntraineur = """
                    CREATE TABLE IF NOT EXISTS Entraineur (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nom TEXT NOT NULL,
                        prenom TEXT NOT NULL,
                        date_naissance DATE NOT NULL
                    );
                """;

        // Requête pour créer la table Equipe
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

        // Requête pour créer la table Joueur
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

        // Requête pour créer la table Match
        String sqlMatch = """
                    CREATE TABLE IF NOT EXISTS Match (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nom TEXT NOT NULL,
                        lieu TEXT NOT NULL,
                        equipe1_id INTEGER NOT NULL,
                        equipe2_id INTEGER NOT NULL,
                        resultat TEXT,
                        date DATE NOT NULL,
                        FOREIGN KEY (equipe1_id) REFERENCES Equipe(id) ON DELETE CASCADE,
                        FOREIGN KEY (equipe2_id) REFERENCES Equipe(id) ON DELETE CASCADE
                    );
                """;

        // Exécution des requêtes pour créer les tables
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

    /**
     * Méthode principale pour tester la connexion et la création des tables.
     * 
     * @param args Arguments de la ligne de commande (non utilisés ici).
     */
    public static void main(String[] args) {
        connect(); // Tester la connexion
        createTables(); // Créer les tables
    }
}
