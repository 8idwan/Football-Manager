package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Match;

/**
 * Classe DAO pour la gestion des matchs dans la base de données.
 */
public class MatchDAO {
    private static final String URL = "jdbc:sqlite:football.db";

    /**
     * Ajoute un match dans la base de données.
     * 
     * @param match Le match à ajouter.
     */
    public void ajouterMatch(Match match) {
        String sql = "INSERT INTO Match (nom, lieu, equipe1_id, equipe2_id, resultat, date) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, match.getNom());
            pstmt.setString(2, match.getLieu());
            pstmt.setInt(3, match.getEquipe1Id());
            pstmt.setInt(4, match.getEquipe2Id());
            pstmt.setString(5, match.getResultat());
            pstmt.setDate(6, Date.valueOf(match.getDate()));
            pstmt.executeUpdate();
            System.out.println("Match ajouté.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du match : " + e.getMessage());
        }
    }

    /**
     * Récupère la liste de tous les matchs.
     * 
     * @return Liste des matchs enregistrés.
     */
    public List<Match> listerMatchs() {
        List<Match> matchs = new ArrayList<>();
        String sql = "SELECT * FROM Match";
        
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Match match = new Match(
                    rs.getString("nom"),
                    rs.getString("lieu"),
                    rs.getInt("equipe1_id"),
                    rs.getInt("equipe2_id"),
                    rs.getString("resultat"),
                    rs.getDate("date").toLocalDate()
                );
                match.setId(rs.getInt("id"));
                matchs.add(match);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des matchs : " + e.getMessage());
        }
        return matchs;
    }

    /**
     * Met à jour le résultat d'un match.
     * 
     * @param id L'identifiant du match à mettre à jour.
     * @param resultat Le nouveau résultat du match.
     */
    public void mettreAJourResultat(int id, String resultat) {
        String sql = "UPDATE Match SET resultat = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, resultat);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Résultat du match mis à jour.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du résultat : " + e.getMessage());
        }
    }

    /**
     * Modifie un match existant.
     * 
     * @param id L'identifiant du match à modifier.
     * @param match Le match mis à jour.
     */
    public void modifierMatch(int id, Match match) {
        String sql = "UPDATE Match SET nom = ?, lieu = ?, equipe1_id = ?, equipe2_id = ?, resultat = ?, date = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, match.getNom());
            pstmt.setString(2, match.getLieu());
            pstmt.setInt(3, match.getEquipe1Id());
            pstmt.setInt(4, match.getEquipe2Id());
            pstmt.setString(5, match.getResultat());
            pstmt.setDate(6, Date.valueOf(match.getDate()));
            pstmt.setInt(7, id);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Match mis à jour avec succès.");
            } else {
                System.out.println("Aucun match trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    /**
     * Supprime un match de la base de données.
     * 
     * @param id L'identifiant du match à supprimer.
     */
    public void supprimerMatch(int id) {
        String sql = "DELETE FROM Match WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Match supprimé.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }
}