package models;

public class Equipe {
    private int id;
    private String nom;
    private String ville;
    private String pays;
    private int entraineurId;

    public Equipe(String nom, String ville, String pays, int entraineurId) {
        this.nom = nom;
        this.ville = ville;
        this.pays = pays;
        this.entraineurId = entraineurId;
    }

    // Getters et Setters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getVille() { return ville; }
    public String getPays() { return pays; }
    public int getEntraineurId() { return entraineurId; }
    public void setId(int id) { this.id = id; }
}
