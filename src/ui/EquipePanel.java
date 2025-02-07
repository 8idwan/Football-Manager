package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import dao.EquipeDAO;
import models.Equipe;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class EquipePanel extends JPanel {
	
    private EquipeDAO equipeDAO = new EquipeDAO(); // Accès à la BD
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtVille, txtPays, txtEntraineurId;
    
    public EquipePanel() {
        setLayout(new BorderLayout());

        // 🔹 1. Création du tableau pour afficher les équipes
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Ville", "Pays", "Entraîneur ID"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        chargerEquipes(); // Charger les équipes au démarrage

        // 🔹 2. Formulaire pour ajouter/modifier une équipe
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Gestion des Équipes"));

        txtNom = new JTextField();
        txtVille = new JTextField();
        txtPays = new JTextField();
        txtEntraineurId = new JTextField();

        formPanel.add(new JLabel("Nom:")); formPanel.add(txtNom);
        formPanel.add(new JLabel("Ville:")); formPanel.add(txtVille);
        formPanel.add(new JLabel("Pays:")); formPanel.add(txtPays);
        formPanel.add(new JLabel("Entraîneur ID:")); formPanel.add(txtEntraineurId);

        JButton btnAjouter = new JButton("Ajouter");
        JButton btnModifier = new JButton("Modifier");
        JButton btnSupprimer = new JButton("Supprimer");

        formPanel.add(btnAjouter);
        formPanel.add(btnModifier);
        formPanel.add(btnSupprimer);

        add(formPanel, BorderLayout.SOUTH);

        // 🔹 3. Actions des boutons
        btnAjouter.addActionListener(e -> ajouterEquipe());
        btnModifier.addActionListener(e -> modifierEquipe());
        btnSupprimer.addActionListener(e -> supprimerEquipe());
    }

    // 🔹 4. Charger la liste des équipes dans le tableau
    private void chargerEquipes() {
        tableModel.setRowCount(0);
        List<Equipe> equipes = equipeDAO.listerEquipes();
        for (Equipe e : equipes) {
            tableModel.addRow(new Object[]{e.getId(), e.getNom(), e.getVille(), e.getPays(), e.getEntraineurId()});
        }
    }

    // 🔹 5. Ajouter une équipe
    private void ajouterEquipe() {
        String nom = txtNom.getText();
        String ville = txtVille.getText();
        String pays = txtPays.getText();
        int entraineurId = Integer.parseInt(txtEntraineurId.getText());

        equipeDAO.ajouterEquipe(new Equipe(nom, ville, pays, entraineurId));
        chargerEquipes();
        viderChamps();
    }

    // 🔹 6. Modifier une équipe
    private void modifierEquipe() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une équipe à modifier.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String nom = JOptionPane.showInputDialog(this, "Nouveau nom :", tableModel.getValueAt(row, 1));
        if (nom != null) {
            equipeDAO.modifierEquipe(id, nom);
            chargerEquipes();
        }
    }

    // 🔹 7. Supprimer une équipe
    private void supprimerEquipe() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une équipe à supprimer.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirmation = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cette équipe ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            equipeDAO.supprimerEquipe(id);
            chargerEquipes();
        }
    }

    // 🔹 8. Vider les champs après l'ajout
    private void viderChamps() {
        txtNom.setText("");
        txtVille.setText("");
        txtPays.setText("");
        txtEntraineurId.setText("");
    }
}
