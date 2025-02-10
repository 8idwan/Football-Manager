package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.LineBorder;

import dao.EntraineurDAO;
import models.Entraineur;

import java.awt.*;
import java.util.List;

public class EntraineurPanel extends JPanel {
    private EntraineurDAO entraineurDAO = new EntraineurDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtPrenom, txtDateNaissance;

    // Constantes de couleurs
    private static final Color BACKGROUND_COLOR = Color.decode("#cccebf");
    private static final Color FORM_BACKGROUND_COLOR = Color.decode("#f0eae4");
    private static final Color TEXT_COLOR = Color.decode("#5d4024");

    public EntraineurPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // 🔹 1. Création du tableau pour afficher les entraîneurs
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Prénom", "Date de Naissance"}, 0);
        table = new JTable(tableModel);
        
        // Style du tableau
        table.setBackground(FORM_BACKGROUND_COLOR);
        table.setForeground(TEXT_COLOR);
        table.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        
        // Style de l'en-tête du tableau
        JTableHeader header = table.getTableHeader();
        header.setBackground(TEXT_COLOR);
        header.setForeground(FORM_BACKGROUND_COLOR);
        header.setFont(new Font("Times New Roman", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(FORM_BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);
        chargerEntraineurs();

        // 🔹 2. Formulaire pour ajouter/modifier un entraîneur
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBackground(FORM_BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(TEXT_COLOR, 1), 
            "Gestion des Entraîneurs", 
            0, 0, 
            new Font("Times New Roman", Font.BOLD, 14), 
            TEXT_COLOR
        ));

        txtNom = createStyledTextField();
        txtPrenom = createStyledTextField();
        txtDateNaissance = createStyledTextField();

        formPanel.add(createStyledLabel("Nom:")); formPanel.add(txtNom);
        formPanel.add(createStyledLabel("Prénom:")); formPanel.add(txtPrenom);
        formPanel.add(createStyledLabel("Date de Naissance:")); formPanel.add(txtDateNaissance);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(FORM_BACKGROUND_COLOR);

        JButton btnAjouter = createStyledButton("Ajouter");
        JButton btnModifier = createStyledButton("Modifier");
        JButton btnSupprimer = createStyledButton("Supprimer");

        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);

        formPanel.add(buttonPanel);

        add(formPanel, BorderLayout.SOUTH);

        // 🔹 3. Actions des boutons
        btnAjouter.addActionListener(e -> ajouterEntraineur());
        btnModifier.addActionListener(e -> modifierEntraineur());
        btnSupprimer.addActionListener(e -> supprimerEntraineur());
    }

    // Méthodes de création de composants stylisés
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(FORM_BACKGROUND_COLOR);
        textField.setForeground(TEXT_COLOR);
        textField.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        textField.setBorder(new LineBorder(TEXT_COLOR, 1));
        return textField;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Times New Roman", Font.BOLD, 12));
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(FORM_BACKGROUND_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFont(new Font("Times New Roman", Font.BOLD, 14));
        button.setBorder(new LineBorder(TEXT_COLOR, 1, true));
        button.setFocusPainted(false);
        return button;
    }

    // Reste des méthodes identiques au code précédent
    private void chargerEntraineurs() {
        tableModel.setRowCount(0);
        List<Entraineur> entraineurs = entraineurDAO.listerEntraineurs();
        for (Entraineur e : entraineurs) {
            tableModel.addRow(new Object[]{e.getId(), e.getNom(), e.getPrenom(), e.getDateNaissance()});
        }
    }

    private void ajouterEntraineur() {
        String nom = txtNom.getText();
        String prenom = txtPrenom.getText();
        String dateNaissance = txtDateNaissance.getText();

        entraineurDAO.ajouterEntraineur(new Entraineur(nom, prenom, dateNaissance));
        chargerEntraineurs();
        viderChamps();
    }

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

    private void viderChamps() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtDateNaissance.setText("");
    }
}