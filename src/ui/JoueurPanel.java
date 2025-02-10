package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.LineBorder;

import dao.EquipeDAO;
import dao.JoueurDAO;
import models.Equipe;
import models.Joueur;

import java.awt.*;
import java.util.List;

public class JoueurPanel extends JPanel {
    private JoueurDAO joueurDAO = new JoueurDAO();
    private EquipeDAO equipeDAO = new EquipeDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtPrenom, txtPoste, txtDateNaissance;
    private JComboBox<String> comboEquipe;

    // Constantes de couleurs
    private static final Color BACKGROUND_COLOR = Color.decode("#cccebf");
    private static final Color FORM_BACKGROUND_COLOR = Color.decode("#f0eae4");
    private static final Color TEXT_COLOR = Color.decode("#5d4024");

    public JoueurPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // 🔹 1. Création du tableau pour afficher les joueurs
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Prénom", "Poste", "Date de Naissance", "Équipe"}, 0);
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
        chargerJoueurs();

        // 🔹 2. Formulaire pour ajouter/modifier un joueur
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(FORM_BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(TEXT_COLOR, 1), 
            "Gestion des Joueurs", 
            0, 0, 
            new Font("Times New Roman", Font.BOLD, 14), 
            TEXT_COLOR
        ));

        txtNom = createStyledTextField();
        txtPrenom = createStyledTextField();
        txtPoste = createStyledTextField();
        txtDateNaissance = createStyledTextField();
        comboEquipe = createStyledComboBox();

        chargerEquipes();

        formPanel.add(createStyledLabel("Nom:")); formPanel.add(txtNom);
        formPanel.add(createStyledLabel("Prénom:")); formPanel.add(txtPrenom);
        formPanel.add(createStyledLabel("Poste:")); formPanel.add(txtPoste);
        formPanel.add(createStyledLabel("Date de Naissance:")); formPanel.add(txtDateNaissance);
        formPanel.add(createStyledLabel("Équipe:")); formPanel.add(comboEquipe);

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
        btnAjouter.addActionListener(e -> ajouterJoueur());
        btnModifier.addActionListener(e -> modifierJoueur());
        btnSupprimer.addActionListener(e -> supprimerJoueur());
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

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBackground(FORM_BACKGROUND_COLOR);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        return comboBox;
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

    // Reste du code identique au précédent (méthodes chargerJoueurs, chargerEquipes, etc.)
    // ... (copier les méthodes existantes)

    private void chargerJoueurs() {
        tableModel.setRowCount(0);
        List<Joueur> joueurs = joueurDAO.listerJoueurs();
        for (Joueur j : joueurs) {
            tableModel.addRow(new Object[]{j.getId(), j.getNom(), j.getPrenom(), j.getPoste(), j.getDateNaissance(), j.getEquipeId()});
        }
    }

    private void chargerEquipes() {
        comboEquipe.removeAllItems();
        List<Equipe> equipes = equipeDAO.listerEquipes();
        for (Equipe equipe : equipes) {
            comboEquipe.addItem(equipe.getNom());
        }
    }

    private void ajouterJoueur() {
        String nom = txtNom.getText();
        String prenom = txtPrenom.getText();
        String poste = txtPoste.getText();
        String dateNaissance = txtDateNaissance.getText();
        String equipeNom = (String) comboEquipe.getSelectedItem();

        int equipeId = equipeDAO.getIdParNom(equipeNom);

        joueurDAO.ajouterJoueur(new Joueur(nom, prenom, poste, dateNaissance, equipeId));
        chargerJoueurs();
        viderChamps();
    }

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

    private void viderChamps() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtPoste.setText("");
        txtDateNaissance.setText("");
        comboEquipe.setSelectedIndex(0);
    }
}