package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.LineBorder;

import dao.MatchDAO;
import models.Match;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class MatchPanel extends JPanel {
    private MatchDAO matchDAO = new MatchDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtLieu, txtEquipe1Id, txtEquipe2Id;

    // Constantes de couleurs
    private static final Color BACKGROUND_COLOR = Color.decode("#cccebf");
    private static final Color FORM_BACKGROUND_COLOR = Color.decode("#f0eae4");
    private static final Color TEXT_COLOR = Color.decode("#5d4024");

    public MatchPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // 🔹 1. Création du tableau pour afficher les matchs
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Lieu", "Équipe 1", "Équipe 2", "Résultat"}, 0);
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
        chargerMatchs();

        // 🔹 2. Formulaire pour ajouter un match
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(FORM_BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(TEXT_COLOR, 1), 
            "Gestion des Matchs", 
            0, 0, 
            new Font("Times New Roman", Font.BOLD, 14), 
            TEXT_COLOR
        ));

        txtNom = createStyledTextField();
        txtLieu = createStyledTextField();
        txtEquipe1Id = createStyledTextField();
        txtEquipe2Id = createStyledTextField();

        formPanel.add(createStyledLabel("Nom du Match:")); formPanel.add(txtNom);
        formPanel.add(createStyledLabel("Lieu:")); formPanel.add(txtLieu);
        formPanel.add(createStyledLabel("Équipe 1 ID:")); formPanel.add(txtEquipe1Id);
        formPanel.add(createStyledLabel("Équipe 2 ID:")); formPanel.add(txtEquipe2Id);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(FORM_BACKGROUND_COLOR);

        JButton btnAjouter = createStyledButton("Ajouter");
        JButton btnSimuler = createStyledButton("Simuler");
        JButton btnSupprimer = createStyledButton("Supprimer");

        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnSimuler);
        buttonPanel.add(btnSupprimer);

        formPanel.add(buttonPanel);

        add(formPanel, BorderLayout.SOUTH);

        // 🔹 3. Actions des boutons
        btnAjouter.addActionListener(e -> ajouterMatch());
        btnSimuler.addActionListener(e -> simulerMatch());
        btnSupprimer.addActionListener(e -> supprimerMatch());
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
    private void chargerMatchs() {
        tableModel.setRowCount(0);
        List<Match> matchs = matchDAO.listerMatchs();
        for (Match m : matchs) {
            tableModel.addRow(new Object[]{m.getId(), m.getNom(), m.getLieu(), m.getEquipe1Id(), m.getEquipe2Id(), m.getResultat()});
        }
    }

    private void ajouterMatch() {
        String nom = txtNom.getText();
        String lieu = txtLieu.getText();
        int equipe1Id = Integer.parseInt(txtEquipe1Id.getText());
        int equipe2Id = Integer.parseInt(txtEquipe2Id.getText());

        matchDAO.ajouterMatch(new Match(nom, lieu, equipe1Id, equipe2Id, "En attente"));
        chargerMatchs();
        viderChamps();
    }

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

    private void viderChamps() {
        txtNom.setText("");
        txtLieu.setText("");
        txtEquipe1Id.setText("");
        txtEquipe2Id.setText("");
    }
}