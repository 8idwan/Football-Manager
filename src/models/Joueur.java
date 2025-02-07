package models;

public class Joueur {
    private int id;
    private String nom;
    private String prenom;
    private String poste;
    private String dateNaissance;
    private int equipeId; // Clé étrangère

    public Joueur(String nom, String prenom, String poste, String dateNaissance, int equipeId) {
        this.nom = nom;
        this.prenom = prenom;
        this.poste = poste;
        this.dateNaissance = dateNaissance;
        this.equipeId = equipeId;
    }

    // Getters et Setters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getPoste() { return poste; }
    public String getDateNaissance() { return dateNaissance; }
    public int getEquipeId() { return equipeId; }

    public void setId(int id) { this.id = id; }
}
