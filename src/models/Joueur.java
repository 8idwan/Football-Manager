package models;

/**
 * Représente un joueur de football dans l'application.
 * Contient des informations telles que le nom, le prénom, le poste, la date de naissance et l'équipe à laquelle le joueur appartient.
 */
public class Joueur {
    
    /** Identifiant unique du joueur. */
    private int id;
    
    /** Nom du joueur. */
    private String nom;
    
    /** Prénom du joueur. */
    private String prenom;
    
    /** Poste du joueur (ex : attaquant, défenseur, etc.). */
    private String poste;
    
    /** Date de naissance du joueur. */
    private String dateNaissance;
    
    /** Identifiant de l'équipe à laquelle le joueur appartient (clé étrangère). */
    private int equipeId;

    /**
     * Constructeur du joueur avec les informations essentielles.
     * 
     * @param nom Le nom du joueur.
     * @param prenom Le prénom du joueur.
     * @param poste Le poste du joueur.
     * @param dateNaissance La date de naissance du joueur.
     * @param equipeId L'identifiant de l'équipe à laquelle le joueur appartient.
     */
    public Joueur(String nom, String prenom, String poste, String dateNaissance, int equipeId) {
        this.nom = nom;
        this.prenom = prenom;
        this.poste = poste;
        this.dateNaissance = dateNaissance;
        this.equipeId = equipeId;
    }

    /**
     * Récupère l'identifiant du joueur.
     * 
     * @return L'identifiant du joueur.
     */
    public int getId() { 
        return id; 
    }

    /**
     * Récupère le nom du joueur.
     * 
     * @return Le nom du joueur.
     */
    public String getNom() { 
        return nom; 
    }

    /**
     * Récupère le prénom du joueur.
     * 
     * @return Le prénom du joueur.
     */
    public String getPrenom() { 
        return prenom; 
    }

    /**
     * Récupère le poste du joueur.
     * 
     * @return Le poste du joueur.
     */
    public String getPoste() { 
        return poste; 
    }

    /**
     * Récupère la date de naissance du joueur.
     * 
     * @return La date de naissance du joueur.
     */
    public String getDateNaissance() { 
        return dateNaissance; 
    }

    /**
     * Récupère l'identifiant de l'équipe à laquelle le joueur appartient.
     * 
     * @return L'identifiant de l'équipe.
     */
    public int getEquipeId() { 
        return equipeId; 
    }

    /**
     * Définit l'identifiant du joueur.
     * 
     * @param id L'identifiant du joueur.
     */
    public void setId(int id) { 
        this.id = id; 
    }
}
