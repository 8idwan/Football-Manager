package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.LineBorder;

import dao.EquipeDAO;
import models.Equipe;

import java.awt.*;
import java.util.List;

public class EquipePanel extends JPanel {
    private EquipeDAO equipeDAO = new EquipeDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtVille, txtPays, txtEntraineurId;

    // Constantes de couleurs
    private static final Color BACKGROUND_COLOR = Color.decode("#cccebf");
    private static final Color FORM_BACKGROUND_COLOR = Color.decode("#f0eae4");
    private static final Color TEXT_COLOR = Color.decode("#5d4024");

    public EquipePanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // 🔹 1. Création du tableau pour afficher les équipes
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Ville", "Pays", "Entraîneur ID"}, 0);
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
        chargerEquipes();

        // 🔹 2. Formulaire pour ajouter/modifier une équipe
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(FORM_BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(TEXT_COLOR, 1), 
            "Gestion des Équipes", 
            0, 0, 
            new Font("Times New Roman", Font.BOLD, 14), 
            TEXT_COLOR
        ));

        txtNom = createStyledTextField();
        txtVille = createStyledTextField();
        txtPays = createStyledTextField();
        txtEntraineurId = createStyledTextField();

        formPanel.add(createStyledLabel("Nom:")); formPanel.add(txtNom);
        formPanel.add(createStyledLabel("Ville:")); formPanel.add(txtVille);
        formPanel.add(createStyledLabel("Pays:")); formPanel.add(txtPays);
        formPanel.add(createStyledLabel("Entraîneur ID:")); formPanel.add(txtEntraineurId);

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
        btnAjouter.addActionListener(e -> ajouterEquipe());
        btnModifier.addActionListener(e -> modifierEquipe());
        btnSupprimer.addActionListener(e -> supprimerEquipe());
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
    private void chargerEquipes() {
        tableModel.setRowCount(0);
        List<Equipe> equipes = equipeDAO.listerEquipes();
        for (Equipe e : equipes) {
            tableModel.addRow(new Object[]{e.getId(), e.getNom(), e.getVille(), e.getPays(), e.getEntraineurId()});
        }
    }

    private void ajouterEquipe() {
        String nom = txtNom.getText();
        String ville = txtVille.getText();
        String pays = txtPays.getText();
        int entraineurId = Integer.parseInt(txtEntraineurId.getText());

        equipeDAO.ajouterEquipe(new Equipe(nom, ville, pays, entraineurId));
        chargerEquipes();
        viderChamps();
    }

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

    private void viderChamps() {
        txtNom.setText("");
        txtVille.setText("");
        txtPays.setText("");
        txtEntraineurId.setText("");
    }
}