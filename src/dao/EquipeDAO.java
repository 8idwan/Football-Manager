package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Equipe;

/**
 * Classe DAO pour la gestion des équipes dans la base de données.
 */
public class EquipeDAO {
    private static final String URL = "jdbc:sqlite:football.db";

    /**
     * Ajoute une équipe à la base de données.
     * 
     * @param equipe L'équipe à ajouter.
     */
    public void ajouterEquipe(Equipe equipe) {
        String sql = "INSERT INTO Equipe (nom, ville, pays, entraineur_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, equipe.getNom());
            pstmt.setString(2, equipe.getVille());
            pstmt.setString(3, equipe.getPays());
            pstmt.setInt(4, equipe.getEntraineurId());
            pstmt.executeUpdate();
            System.out.println("Équipe ajoutée avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'équipe : " + e.getMessage());
        }
    }

    /**
     * Récupère la liste de toutes les équipes.
     * 
     * @return Liste des équipes enregistrées.
     */
    public List<Equipe> listerEquipes() {
        List<Equipe> equipes = new ArrayList<>();
        String sql = "SELECT * FROM Equipe";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Equipe equipe = new Equipe(
                    rs.getString("nom"),
                    rs.getString("ville"),
                    rs.getString("pays"),
                    rs.getInt("entraineur_id")
                );
                equipe.setId(rs.getInt("id"));
                equipes.add(equipe);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des équipes : " + e.getMessage());
        }
        return equipes;
    }

    /**
     * Met à jour une équipe existante dans la base de données.
     * 
     * @param id L'identifiant de l'équipe à modifier.
     * @param equipe Les nouvelles informations de l'équipe.
     */
    public void modifierEquipe(int id, Equipe equipe) {
        String sql = "UPDATE Equipe SET nom = ?, ville = ?, pays = ?, entraineur_id = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, equipe.getNom());
            pstmt.setString(2, equipe.getVille());
            pstmt.setString(3, equipe.getPays());
            pstmt.setInt(4, equipe.getEntraineurId());
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            System.out.println("Équipe mise à jour avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    /**
     * Supprime une équipe de la base de données.
     * 
     * @param id L'identifiant de l'équipe à supprimer.
     */
    public void supprimerEquipe(int id) {
        String sql = "DELETE FROM Equipe WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Équipe supprimée.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    /**
     * Récupère l'identifiant d'une équipe à partir de son nom.
     * 
     * @param nom Le nom de l'équipe.
     * @return L'identifiant de l'équipe ou -1 si l'équipe n'est pas trouvée.
     */
    public int getIdParNom(String nom) {
        String sql = "SELECT id FROM Equipe WHERE nom = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'ID de l'équipe : " + e.getMessage());
        }
        return -1; // Retourne -1 si l'équipe n'est pas trouvée
    }
}
