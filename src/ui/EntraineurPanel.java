package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import dao.EntraineurDAO;
import models.Entraineur;

import java.awt.*;
import java.util.List;

public class EntraineurPanel extends JPanel {
    private EntraineurDAO entraineurDAO = new EntraineurDAO(); // Accès à la BD
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtPrenom, txtDateNaissance;

    public EntraineurPanel() {
        setLayout(new BorderLayout());

        // 🔹 1. Création du tableau pour afficher les entraîneurs
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Prénom", "Date de Naissance"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        chargerEntraineurs(); // Charger les entraîneurs au démarrage

        // 🔹 2. Formulaire pour ajouter/modifier un entraîneur
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Gestion des Entraîneurs"));

        txtNom = new JTextField();
        txtPrenom = new JTextField();
        txtDateNaissance = new JTextField();

        formPanel.add(new JLabel("Nom:")); formPanel.add(txtNom);
        formPanel.add(new JLabel("Prénom:")); formPanel.add(txtPrenom);
        formPanel.add(new JLabel("Date de Naissance:")); formPanel.add(txtDateNaissance);

        JButton btnAjouter = new JButton("Ajouter");
        JButton btnModifier = new JButton("Modifier");
        JButton btnSupprimer = new JButton("Supprimer");

        formPanel.add(btnAjouter);
        formPanel.add(btnModifier);
        formPanel.add(btnSupprimer);

        add(formPanel, BorderLayout.SOUTH);

        // 🔹 3. Actions des boutons
        btnAjouter.addActionListener(e -> ajouterEntraineur());
        btnModifier.addActionListener(e -> modifierEntraineur());
        btnSupprimer.addActionListener(e -> supprimerEntraineur());
    }

    // 🔹 4. Charger la liste des entraîneurs dans le tableau
    private void chargerEntraineurs() {
        tableModel.setRowCount(0);
        List<Entraineur> entraineurs = entraineurDAO.listerEntraineurs();
        for (Entraineur e : entraineurs) {
            tableModel.addRow(new Object[]{e.getId(), e.getNom(), e.getPrenom(), e.getDateNaissance()});
        }
    }

    // 🔹 5. Ajouter un entraîneur
    private void ajouterEntraineur() {
        String nom = txtNom.getText();
        String prenom = txtPrenom.getText();
        String dateNaissance = txtDateNaissance.getText();

        entraineurDAO.ajouterEntraineur(new Entraineur(nom, prenom, dateNaissance));
        chargerEntraineurs();
        viderChamps();
    }

    // 🔹 6. Modifier un entraîneur
    private void modifierEntraineur() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un entraîneur à modifier.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String nom = JOptionPane.showInputDialog(this, "Nouveau nom :", tableModel.getValueAt(row, 1));
        if (nom != null) {
            entraineurDAO.modifierEntraineur(id, nom);
            chargerEntraineurs();
        }
    }

    // 🔹 7. Supprimer un entraîneur
    private void supprimerEntraineur() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un entraîneur à supprimer.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirmation = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cet entraîneur ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            entraineurDAO.supprimerEntraineur(id);
            chargerEntraineurs();
        }
    }

    // 🔹 8. Vider les champs après l'ajout
    private void viderChamps() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtDateNaissance.setText("");
    }
}
