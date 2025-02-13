package models;

import java.time.LocalDate;

/**
 * Représente un match de football dans l'application.
 * Contient des informations telles que le nom du match, le lieu, les équipes participantes, le résultat et la date du match.
 */
public class Match {
    
    /** Identifiant unique du match. */
    private int id;
    
    /** Nom du match. */
    private String nom;
    
    /** Lieu où le match a eu lieu. */
    private String lieu;
    
    /** Identifiant de la première équipe participante. */
    private int equipe1Id;
    
    /** Identifiant de la deuxième équipe participante. */
    private int equipe2Id;
    
    /** Résultat du match. */
    private String resultat;
    
    /** Date du match. */
    private LocalDate date;

    /**
     * Constructeur du match avec les informations essentielles.
     * 
     * @param nom Le nom du match.
     * @param lieu Le lieu où le match a eu lieu.
     * @param equipe1Id L'identifiant de la première équipe.
     * @param equipe2Id L'identifiant de la deuxième équipe.
     * @param resultat Le résultat du match.
     * @param date La date du match.
     */
    public Match(String nom, String lieu, int equipe1Id, int equipe2Id, String resultat, LocalDate date) {
        this.nom = nom;
        this.lieu = lieu;
        this.equipe1Id = equipe1Id;
        this.equipe2Id = equipe2Id;
        this.resultat = resultat;
        this.date = date;
    }

    /**
     * Récupère l'identifiant du match.
     * 
     * @return L'identifiant du match.
     */
    public int getId() { 
        return id; 
    }

    /**
     * Récupère le nom du match.
     * 
     * @return Le nom du match.
     */
    public String getNom() { 
        return nom; 
    }

    /**
     * Récupère le lieu du match.
     * 
     * @return Le lieu du match.
     */
    public String getLieu() { 
        return lieu; 
    }

    /**
     * Récupère l'identifiant de la première équipe.
     * 
     * @return L'identifiant de la première équipe.
     */
    public int getEquipe1Id() { 
        return equipe1Id; 
    }

    /**
     * Récupère l'identifiant de la deuxième équipe.
     * 
     * @return L'identifiant de la deuxième équipe.
     */
    public int getEquipe2Id() { 
        return equipe2Id; 
    }

    /**
     * Récupère le résultat du match.
     * 
     * @return Le résultat du match.
     */
    public String getResultat() { 
        return resultat; 
    }

    /**
     * Récupère la date du match.
     * 
     * @return La date du match.
     */
    public LocalDate getDate() { 
        return date; 
    }

    /**
     * Définit l'identifiant du match.
     * 
     * @param id L'identifiant du match.
     */
    public void setId(int id) { 
        this.id = id; 
    }

    /**
     * Définit la date du match.
     * 
     * @param date La date du match.
     */
    public void setDate(LocalDate date) { 
        this.date = date; 
    }
}
