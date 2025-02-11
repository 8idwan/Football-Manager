package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.LineBorder;
import dao.MatchDAO;
import dao.EquipeDAO;
import models.Match;
import models.Equipe;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class MatchPanel extends JPanel {
    private MatchDAO matchDAO = new MatchDAO();
    private EquipeDAO equipeDAO = new EquipeDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtLieu;
    private JComboBox<String> comboEquipe1, comboEquipe2;
    private List<Equipe> equipes;

    private static final Color BACKGROUND_COLOR = Color.decode("#cccebf");
    private static final Color FORM_BACKGROUND_COLOR = Color.decode("#f0eae4");
    private static final Color TEXT_COLOR = Color.decode("#5d4024");

    public MatchPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // 🔹 1. Création du tableau pour afficher les matchs
        table = createTable(); // Utiliser la méthode createTable()
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(FORM_BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        // Initialisation du panneau de formulaire
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(FORM_BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(TEXT_COLOR, 1), "Gestion des Matchs", 0, 0, new Font("Times New Roman", Font.BOLD, 14), TEXT_COLOR));

        // Initialisation des composants de formulaire
        txtNom = createStyledTextField();
        txtLieu = createStyledTextField();
        comboEquipe1 = createStyledComboBox();
        comboEquipe2 = createStyledComboBox();

        // Chargement des données
        chargerEquipes();
        chargerMatchs();

        // Ajout des composants au formulaire
        formPanel.add(createStyledLabel("Compétition:")); formPanel.add(txtNom);
        formPanel.add(createStyledLabel("Lieu:")); formPanel.add(txtLieu);
        formPanel.add(createStyledLabel("Équipe 1:")); formPanel.add(comboEquipe1);
        formPanel.add(createStyledLabel("Équipe 2:")); formPanel.add(comboEquipe2);

        // Panneau de boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(FORM_BACKGROUND_COLOR);
        JButton btnAjouter = createStyledButton("Ajouter");
        JButton btnSimuler = createStyledButton("Simuler");
        JButton btnSupprimer = createStyledButton("Supprimer");
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnSimuler);
        buttonPanel.add(btnSupprimer);
        formPanel.add(buttonPanel);

        // Ajouter la barre de navigation
        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(formPanel, BorderLayout.NORTH); // Formulaire en haut
        southContainer.add(createNavigationBar(), BorderLayout.SOUTH); // Navigation en bas
        add(southContainer, BorderLayout.SOUTH);

        // Ajout des écouteurs d'événements
        btnAjouter.addActionListener(e -> ajouterMatch());
        btnSimuler.addActionListener(e -> simulerMatch());
        btnSupprimer.addActionListener(e -> supprimerMatch());
    }
    
    private JTable createTable() {
        tableModel = new DefaultTableModel(new String[]{"ID", "Compétition", "Lieu", "Équipe 1", "Équipe 2", "Résultat"}, 0);
        JTable table = new JTable(tableModel);

        // Appliquer le style au tableau
        table.setBackground(FORM_BACKGROUND_COLOR);
        table.setForeground(TEXT_COLOR);
        table.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Appliquer le style à l'en-tête du tableau
        JTableHeader header = table.getTableHeader();
        header.setBackground(TEXT_COLOR);
        header.setForeground(FORM_BACKGROUND_COLOR);
        header.setFont(new Font("Times New Roman", Font.BOLD, 12));

        // Ajouter un écouteur de sélection pour charger les données du match sélectionné
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormWithSelectedMatch();
            }
        });

        return table;
    }

    // Méthode pour créer la barre de navigation
    private JPanel createNavigationBar() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navPanel.setBackground(FORM_BACKGROUND_COLOR);
        navPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, TEXT_COLOR));

        String[] pages = {"Equipe", "Joueur", "Entraineur", "Match"};
        for (String page : pages) {
            JButton navButton = createNavigationButton(page);
            navPanel.add(navButton);
        }

        return navPanel;
    }

    // Méthode pour créer un bouton de navigation
    private JButton createNavigationButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(FORM_BACKGROUND_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFont(new Font("Times New Roman", Font.BOLD, 14));
        button.setBorder(new LineBorder(TEXT_COLOR, 1, true));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 30));

        // Désactiver le bouton de la page actuelle
        if (text.equals("Match")) {
            button.setEnabled(false);
        }

        button.addActionListener(e -> navigateToPage(text));
        return button;
    }

    // Méthode pour naviguer entre les pages
    private void navigateToPage(String page) {
        Container parent = getParent();
        if (parent != null) {
            parent.removeAll();

            switch (page) {
                case "Equipe":
                    parent.add(new EquipePanel());
                    break;
                case "Joueur":
                    parent.add(new JoueurPanel());
                    break;
                case "Entraineur":
                    parent.add(new EntraineurPanel());
                    break;
                case "Match":
                    parent.add(new MatchPanel());
                    break;
            }

            parent.revalidate();
            parent.repaint();
        }
    }

    // Méthodes pour charger les données
    private void chargerMatchs() {
        tableModel.setRowCount(0);
        List<Match> matchs = matchDAO.listerMatchs();
        List<Equipe> equipes = equipeDAO.listerEquipes();

        for (Match m : matchs) {
            String nomEquipe1 = "Non assigné";
            String nomEquipe2 = "Non assigné";
            
            for (Equipe equipe : equipes) {
                if (equipe.getId() == m.getEquipe1Id()) {
                    nomEquipe1 = equipe.getNom();
                }
                if (equipe.getId() == m.getEquipe2Id()) {
                    nomEquipe2 = equipe.getNom();
                }
            }
            
            tableModel.addRow(new Object[]{
                m.getId(), 
                m.getNom(), 
                m.getLieu(), 
                nomEquipe1,
                nomEquipe2,
                m.getResultat()
            });
        }
    }

    private void chargerEquipes() {
        equipes = equipeDAO.listerEquipes();
        comboEquipe1.removeAllItems();
        comboEquipe2.removeAllItems();
        
        for (Equipe e : equipes) {
            comboEquipe1.addItem(e.getNom());
            comboEquipe2.addItem(e.getNom());
        }
    }

    // Méthodes pour gérer les matchs
    private void ajouterMatch() {
        String nom = txtNom.getText().trim();
        String lieu = txtLieu.getText().trim();
        if (nom.isEmpty() || lieu.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        String nomEquipe1 = (String) comboEquipe1.getSelectedItem();
        String nomEquipe2 = (String) comboEquipe2.getSelectedItem();
        
        int equipe1Id = -1;
        int equipe2Id = -1;
        
        for (Equipe e : equipes) {
            if (e.getNom().equals(nomEquipe1)) {
                equipe1Id = e.getId();
            }
            if (e.getNom().equals(nomEquipe2)) {
                equipe2Id = e.getId();
            }
        }

        if (equipe1Id == equipe2Id) {
            JOptionPane.showMessageDialog(this, "Une équipe ne peut pas jouer contre elle-même.");
            return;
        }

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
        Random random = new Random();
        int score1 = random.nextInt(5);
        int score2 = random.nextInt(5);
        String resultat = score1 + " - " + score2;
        matchDAO.mettreAJourResultat(id, resultat);
        tableModel.setValueAt(resultat, row, 5);
    }
    
    private void modifierMatch() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un match à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0); // ID du match sélectionné

        // Récupérer les nouvelles valeurs du formulaire
        String nom = txtNom.getText().trim();
        String lieu = txtLieu.getText().trim();
        String nomEquipe1 = (String) comboEquipe1.getSelectedItem();
        String nomEquipe2 = (String) comboEquipe2.getSelectedItem();
        String resultat = "En attente"; // Par défaut

        int equipe1Id = -1;
        int equipe2Id = -1;

        for (Equipe e : equipes) {
            if (e.getNom().equals(nomEquipe1)) {
                equipe1Id = e.getId();
            }
            if (e.getNom().equals(nomEquipe2)) {
                equipe2Id = e.getId();
            }
        }

        if (equipe1Id == equipe2Id) {
            JOptionPane.showMessageDialog(this, "Une équipe ne peut pas jouer contre elle-même.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Créer un nouvel objet Match avec les données du formulaire
        Match matchModifie = new Match(nom, lieu, equipe1Id, equipe2Id, resultat);
        matchModifie.setId(id);

        // Appeler la méthode du DAO pour mettre à jour le match
        matchDAO.modifierMatch(id, matchModifie);
        
        // Recharger les données et nettoyer le formulaire
        chargerMatchs();
        viderChamps();
        
        // Afficher un message de confirmation
        JOptionPane.showMessageDialog(
            this,
            "Le match a été modifié avec succès",
            "Succès",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private boolean validateForm() {
        if (txtNom.getText().trim().isEmpty() ||
            txtLieu.getText().trim().isEmpty() ||
            comboEquipe1.getSelectedItem() == null ||
            comboEquipe2.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    private void fillFormWithSelectedMatch() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            txtNom.setText((String) tableModel.getValueAt(selectedRow, 1)); // Nom
            txtLieu.setText((String) tableModel.getValueAt(selectedRow, 2)); // Lieu
            
            // Sélectionner les équipes dans les ComboBox
            String nomEquipe1 = (String) tableModel.getValueAt(selectedRow, 3);
            String nomEquipe2 = (String) tableModel.getValueAt(selectedRow, 4);
            
            for (int i = 0; i < comboEquipe1.getItemCount(); i++) {
                if (comboEquipe1.getItemAt(i).equals(nomEquipe1)) {
                    comboEquipe1.setSelectedIndex(i);
                }
                if (comboEquipe2.getItemAt(i).equals(nomEquipe2)) {
                    comboEquipe2.setSelectedIndex(i);
                }
            }
        }
    }

    private void supprimerMatch() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un match à supprimer.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce match ?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            matchDAO.supprimerMatch(id);
            tableModel.removeRow(row);
        }
    }

    private void viderChamps() {
        txtNom.setText("");
        txtLieu.setText("");
        if (comboEquipe1.getItemCount() > 0) {
            comboEquipe1.setSelectedIndex(0);
        }
        if (comboEquipe2.getItemCount() > 0) {
            comboEquipe2.setSelectedIndex(0);
        }
    }

    // Méthodes pour créer des composants stylisés
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
        return button;
    }
    
    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBackground(FORM_BACKGROUND_COLOR);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        return comboBox;
    }
}