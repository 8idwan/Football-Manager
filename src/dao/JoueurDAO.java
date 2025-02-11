package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import models.Joueur;

public class JoueurDAO {
    private static final String URL = "jdbc:sqlite:football.db";

    // 🔹 1. Ajouter un joueur
    public void ajouterJoueur(Joueur joueur) {
        String sql = "INSERT INTO Joueur (nom, prenom, poste, date_naissance, equipe_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, joueur.getNom());
            pstmt.setString(2, joueur.getPrenom());
            pstmt.setString(3, joueur.getPoste());
            pstmt.setString(4, joueur.getDateNaissance());
            pstmt.setInt(5, joueur.getEquipeId());
            pstmt.executeUpdate();
            System.out.println("Joueur ajouté avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du joueur : " + e.getMessage());
        }
    }

    // 🔹 2. Lire tous les joueurs
    public List<Joueur> listerJoueurs() {
        List<Joueur> joueurs = new ArrayList<>();
        String sql = "SELECT * FROM Joueur";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Joueur joueur = new Joueur(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("poste"),
                    rs.getString("date_naissance"),
                    rs.getInt("equipe_id")
                );
                joueur.setId(rs.getInt("id"));
                joueurs.add(joueur);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des joueurs : " + e.getMessage());
        }
        return joueurs;
    }

    // 🔹 3. Modifier un joueur
 // 🔹 Modifier un joueur (mise à jour complète)
    public void modifierJoueur(int id, Joueur joueur) {
        String sql = "UPDATE Joueur SET nom = ?, prenom = ?, poste = ?, date_naissance = ?, equipe_id = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, joueur.getNom());
            pstmt.setString(2, joueur.getPrenom());
            pstmt.setString(3, joueur.getPoste());
            pstmt.setString(4, joueur.getDateNaissance());
            pstmt.setInt(5, joueur.getEquipeId());
            pstmt.setInt(6, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Joueur mis à jour avec succès.");
            } else {
                System.out.println("Aucun joueur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }


    // 🔹 4. Supprimer un joueur
    public void supprimerJoueur(int id) {
        String sql = "DELETE FROM Joueur WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Joueur supprimé.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }
}
