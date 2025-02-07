package models;

public class Entraineur {
    private int id;
    private String nom;
    private String prenom;
    private String dateNaissance;

    public Entraineur(String nom, String prenom, String dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getDateNaissance() { return dateNaissance; }
    public void setId(int id) { this.id = id; }
}
