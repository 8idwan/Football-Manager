package models;

public class Match {
    private int id;
    private String nom;
    private String lieu;
    private int equipe1Id;
    private int equipe2Id;
    private String resultat;

    public Match(String nom, String lieu, int equipe1Id, int equipe2Id, String resultat) {
        this.nom = nom;
        this.lieu = lieu;
        this.equipe1Id = equipe1Id;
        this.equipe2Id = equipe2Id;
        this.resultat = resultat;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getLieu() { return lieu; }
    public int getEquipe1Id() { return equipe1Id; }
    public int getEquipe2Id() { return equipe2Id; }
    public String getResultat() { return resultat; }
    public void setId(int id) { this.id = id; }
}
