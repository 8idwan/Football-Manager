package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import models.Entraineur;

public class EntraineurDAO {
    private static final String URL = "jdbc:sqlite:football.db";

    // 🔹 Ajouter un entraîneur
    public void ajouterEntraineur(Entraineur entraineur) {
        String sql = "INSERT INTO Entraineur (nom, prenom, date_naissance) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entraineur.getNom());
            pstmt.setString(2, entraineur.getPrenom());
            pstmt.setString(3, entraineur.getDateNaissance());
            pstmt.executeUpdate();
            System.out.println("Entraîneur ajouté.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'entraîneur : " + e.getMessage());
        }
    }

    // 🔹 Lister les entraîneurs
    public List<Entraineur> listerEntraineurs() {
        List<Entraineur> entraineurs = new ArrayList<>();
        String sql = "SELECT * FROM Entraineur";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Entraineur entraineur = new Entraineur(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("date_naissance")
                );
                entraineur.setId(rs.getInt("id"));
                entraineurs.add(entraineur);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des entraîneurs : " + e.getMessage());
        }
        return entraineurs;
    }

    // 🔹 Modifier un entraîneur
 // 🔹 Modifier un entraîneur (mise à jour complète)
    public void modifierEntraineur(int id, Entraineur entraineur) {
        String sql = "UPDATE Entraineur SET nom = ?, prenom = ?, date_naissance = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entraineur.getNom());
            pstmt.setString(2, entraineur.getPrenom());
            pstmt.setString(3, entraineur.getDateNaissance());
            pstmt.setInt(4, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Entraîneur mis à jour avec succès.");
            } else {
                System.out.println("Aucun entraîneur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    // 🔹 Supprimer un entraîneur
    public void supprimerEntraineur(int id) {
        String sql = "DELETE FROM Entraineur WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Entraîneur supprimé.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }
}
