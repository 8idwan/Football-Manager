package models;

/**
 * Représente une équipe de football dans l'application.
 * Contient des informations telles que le nom de l'équipe, sa ville, son pays et l'identifiant de l'entraineur associé.
 */
public class Equipe {
    
    /** Identifiant unique de l'équipe. */
    private int id;
    
    /** Nom de l'équipe. */
    private String nom;
    
    /** Ville où l'équipe est basée. */
    private String ville;
    
    /** Pays de l'équipe. */
    private String pays;
    
    /** Identifiant de l'entraineur de l'équipe. */
    private int entraineurId;

    /**
     * Constructeur de l'équipe avec les informations essentielles.
     * 
     * @param nom Le nom de l'équipe.
     * @param ville La ville où l'équipe est basée.
     * @param pays Le pays de l'équipe.
     * @param entraineurId L'identifiant de l'entraineur de l'équipe.
     */
    public Equipe(String nom, String ville, String pays, int entraineurId) {
        this.nom = nom;
        this.ville = ville;
        this.pays = pays;
        this.entraineurId = entraineurId;
    }

    /**
     * Récupère l'identifiant de l'équipe.
     * 
     * @return L'identifiant de l'équipe.
     */
    public int getId() { 
        return id; 
    }

    /**
     * Récupère le nom de l'équipe.
     * 
     * @return Le nom de l'équipe.
     */
    public String getNom() { 
        return nom; 
    }

    /**
     * Récupère la ville où l'équipe est basée.
     * 
     * @return La ville de l'équipe.
     */
    public String getVille() { 
        return ville; 
    }

    /**
     * Récupère le pays de l'équipe.
     * 
     * @return Le pays de l'équipe.
     */
    public String getPays() { 
        return pays; 
    }

    /**
     * Récupère l'identifiant de l'entraineur de l'équipe.
     * 
     * @return L'identifiant de l'entraineur.
     */
    public int getEntraineurId() { 
        return entraineurId; 
    }

    /**
     * Définit l'identifiant de l'équipe.
     * 
     * @param id L'identifiant de l'équipe.
     */
    public void setId(int id) { 
        this.id = id; 
    }
}
