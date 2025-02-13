package models;

/**
 * Représente un entraineur dans l'application.
 * Contient les informations personnelles de l'entraineur telles que son nom, prénom et date de naissance.
 */
public class Entraineur {
    
    /** Identifiant unique de l'entraineur. */
    private int id;
    
    /** Nom de l'entraineur. */
    private String nom;
    
    /** Prénom de l'entraineur. */
    private String prenom;
    
    /** Date de naissance de l'entraineur. */
    private String dateNaissance;

    /**
     * Constructeur de l'entraineur avec les informations essentielles.
     * 
     * @param nom Le nom de l'entraineur.
     * @param prenom Le prénom de l'entraineur.
     * @param dateNaissance La date de naissance de l'entraineur.
     */
    public Entraineur(String nom, String prenom, String dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
    }

    /**
     * Récupère l'identifiant de l'entraineur.
     * 
     * @return L'identifiant de l'entraineur.
     */
    public int getId() { 
        return id; 
    }

    /**
     * Récupère le nom de l'entraineur.
     * 
     * @return Le nom de l'entraineur.
     */
    public String getNom() { 
        return nom; 
    }

    /**
     * Récupère le prénom de l'entraineur.
     * 
     * @return Le prénom de l'entraineur.
     */
    public String getPrenom() { 
        return prenom; 
    }

    /**
     * Récupère la date de naissance de l'entraineur.
     * 
     * @return La date de naissance de l'entraineur.
     */
    public String getDateNaissance() { 
        return dateNaissance; 
    }

    /**
     * Définit l'identifiant de l'entraineur.
     * 
     * @param id L'identifiant de l'entraineur.
     */
    public void setId(int id) { 
        this.id = id; 
    }
}
