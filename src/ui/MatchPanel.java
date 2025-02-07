package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import dao.MatchDAO;
import models.Match;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class MatchPanel extends JPanel {
    private MatchDAO matchDAO = new MatchDAO(); // Accès à la BD
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtLieu, txtEquipe1Id, txtEquipe2Id;

    public MatchPanel() {
        setLayout(new BorderLayout());

        // 🔹 1. Création du tableau pour afficher les matchs
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Lieu", "Équipe 1", "Équipe 2", "Résultat"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        chargerMatchs(); // Charger les matchs au démarrage

        // 🔹 2. Formulaire pour ajouter un match
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Gestion des Matchs"));

        txtNom = new JTextField();
        txtLieu = new JTextField();
        txtEquipe1Id = new JTextField();
        txtEquipe2Id = new JTextField();

        formPanel.add(new JLabel("Nom du Match:")); formPanel.add(txtNom);
        formPanel.add(new JLabel("Lieu:")); formPanel.add(txtLieu);
        formPanel.add(new JLabel("Équipe 1 ID:")); formPanel.add(txtEquipe1Id);
        formPanel.add(new JLabel("Équipe 2 ID:")); formPanel.add(txtEquipe2Id);

        JButton btnAjouter = new JButton("Ajouter");
        JButton btnSimuler = new JButton("Simuler");
        JButton btnSupprimer = new JButton("Supprimer");

        formPanel.add(btnAjouter);
        formPanel.add(btnSimuler);
        formPanel.add(btnSupprimer);

        add(formPanel, BorderLayout.SOUTH);

        // 🔹 3. Actions des boutons
        btnAjouter.addActionListener(e -> ajouterMatch());
        btnSimuler.addActionListener(e -> simulerMatch());
        btnSupprimer.addActionListener(e -> supprimerMatch());
    }

    // 🔹 4. Charger la liste des matchs dans le tableau
    private void chargerMatchs() {
        tableModel.setRowCount(0);
        List<Match> matchs = matchDAO.listerMatchs();
        for (Match m : matchs) {
            tableModel.addRow(new Object[]{m.getId(), m.getNom(), m.getLieu(), m.getEquipe1Id(), m.getEquipe2Id(), m.getResultat()});
        }
    }

    // 🔹 5. Ajouter un match
    private void ajouterMatch() {
        String nom = txtNom.getText();
        String lieu = txtLieu.getText();
        int equipe1Id = Integer.parseInt(txtEquipe1Id.getText());
        int equipe2Id = Integer.parseInt(txtEquipe2Id.getText());

        matchDAO.ajouterMatch(new Match(nom, lieu, equipe1Id, equipe2Id, "En attente"));
        chargerMatchs();
        viderChamps();
    }

    // 🔹 6. Simuler un match (résultat aléatoire)
    private void simulerMatch() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un match à simuler.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int equipe1Id = (int) tableModel.getValueAt(row, 3);
        int equipe2Id = (int) tableModel.getValueAt(row, 4);

        Random random = new Random();
        int vainqueur = random.nextBoolean() ? equipe1Id : equipe2Id;
        String resultat = "Victoire Équipe " + vainqueur;

        matchDAO.mettreAJourResultat(id, resultat);
        chargerMatchs();
    }

    // 🔹 7. Supprimer un match
    private void supprimerMatch() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un match à supprimer.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirmation = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce match ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            matchDAO.supprimerMatch(id);
            chargerMatchs();
        }
    }

    // 🔹 8. Vider les champs après l'ajout
    private void viderChamps() {
        txtNom.setText("");
        txtLieu.setText("");
        txtEquipe1Id.setText("");
        txtEquipe2Id.setText("");
    }
}
