package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import dao.JoueurDAO;
import models.Joueur;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class JoueurPanel extends JPanel {
	
	private JoueurDAO joueurDAO = new JoueurDAO(); // Accès à la BD
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtPrenom, txtPoste, txtDateNaissance, txtEquipeId;
    
    public JoueurPanel() {
        setLayout(new BorderLayout());

        // 🔹 1. Création du tableau pour afficher les joueurs
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Prénom", "Poste", "Date de Naissance", "Équipe"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        chargerJoueurs(); // Charger les joueurs au démarrage

        // 🔹 2. Formulaire pour ajouter/modifier un joueur
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Gestion des Joueurs"));

        txtNom = new JTextField();
        txtPrenom = new JTextField();
        txtPoste = new JTextField();
        txtDateNaissance = new JTextField();
        txtEquipeId = new JTextField();

        formPanel.add(new JLabel("Nom:")); formPanel.add(txtNom);
        formPanel.add(new JLabel("Prénom:")); formPanel.add(txtPrenom);
        formPanel.add(new JLabel("Poste:")); formPanel.add(txtPoste);
        formPanel.add(new JLabel("Date de Naissance:")); formPanel.add(txtDateNaissance);
        formPanel.add(new JLabel("Équipe ID:")); formPanel.add(txtEquipeId);

        JButton btnAjouter = new JButton("Ajouter");
        JButton btnModifier = new JButton("Modifier");
        JButton btnSupprimer = new JButton("Supprimer");

        formPanel.add(btnAjouter);
        formPanel.add(btnModifier);
        formPanel.add(btnSupprimer);

        add(formPanel, BorderLayout.SOUTH);

        // 🔹 3. Actions des boutons
        btnAjouter.addActionListener(e -> ajouterJoueur());
        btnModifier.addActionListener(e -> modifierJoueur());
        btnSupprimer.addActionListener(e -> supprimerJoueur());
    }

    // 🔹 4. Charger la liste des joueurs dans le tableau
    private void chargerJoueurs() {
        tableModel.setRowCount(0);
        List<Joueur> joueurs = joueurDAO.listerJoueurs();
        for (Joueur j : joueurs) {
            tableModel.addRow(new Object[]{j.getId(), j.getNom(), j.getPrenom(), j.getPoste(), j.getDateNaissance(), j.getEquipeId()});
        }
    }

    // 🔹 5. Ajouter un joueur
    private void ajouterJoueur() {
        String nom = txtNom.getText();
        String prenom = txtPrenom.getText();
        String poste = txtPoste.getText();
        String dateNaissance = txtDateNaissance.getText();
        int equipeId = Integer.parseInt(txtEquipeId.getText());

        joueurDAO.ajouterJoueur(new Joueur(nom, prenom, poste, dateNaissance, equipeId));
        chargerJoueurs();
        viderChamps();
    }

    // 🔹 6. Modifier un joueur
    private void modifierJoueur() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un joueur à modifier.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String poste = JOptionPane.showInputDialog(this, "Nouveau poste :", tableModel.getValueAt(row, 3));
        if (poste != null) {
            joueurDAO.modifierJoueur(id, poste);
            chargerJoueurs();
        }
    }

    // 🔹 7. Supprimer un joueur
    private void supprimerJoueur() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un joueur à supprimer.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirmation = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce joueur ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            joueurDAO.supprimerJoueur(id);
            chargerJoueurs();
        }
    }

    // 🔹 8. Vider les champs après l'ajout
    private void viderChamps() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtPoste.setText("");
        txtDateNaissance.setText("");
        txtEquipeId.setText("");
    }
}
