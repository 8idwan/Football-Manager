package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import dao.EquipeDAO;
import dao.JoueurDAO;
import models.Equipe;
import models.Joueur;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import java.util.Date;

public class JoueurPanel extends JPanel {
    private JoueurDAO joueurDAO = new JoueurDAO();
    private EquipeDAO equipeDAO = new EquipeDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtPrenom, txtPoste;
    private JDateChooser dateChooserNaissance;
    private JComboBox<String> comboEquipe;

    // Constantes de couleurs
    private static final Color BACKGROUND_COLOR = Color.decode("#cccebf");
    private static final Color FORM_BACKGROUND_COLOR = Color.decode("#f0eae4");
    private static final Color TEXT_COLOR = Color.decode("#5d4024");
    private static final Font REGULAR_FONT = new Font("Times New Roman", Font.PLAIN, 12);
    private static final Font BOLD_FONT = new Font("Times New Roman", Font.BOLD, 12);
    private static final Font BUTTON_FONT = new Font("Times New Roman", Font.BOLD, 14);

    public JoueurPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // 🔹 1. Création du tableau pour afficher les joueurs
        table = createTable(); // Utiliser la méthode createTable()

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(FORM_BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);
        chargerJoueurs();

        // 🔹 2. Formulaire pour ajouter/modifier un joueur
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(FORM_BACKGROUND_COLOR);
        formPanel.setBorder(createTitledBorder("Gestion des Joueurs"));

        txtNom = createStyledTextField();
        txtPrenom = createStyledTextField();
        txtPoste = createStyledTextField();
        
        dateChooserNaissance = new JDateChooser();
        
        comboEquipe = createStyledComboBox();

        chargerEquipes();

        formPanel.add(createStyledLabel("Nom:")); formPanel.add(txtNom);
        formPanel.add(createStyledLabel("Prénom:")); formPanel.add(txtPrenom);
        formPanel.add(createStyledLabel("Poste:")); formPanel.add(txtPoste);
        formPanel.add(createStyledLabel("Date de Naissance:")); formPanel.add(dateChooserNaissance);
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

        // 🔹 3. Actions des boutons
        btnAjouter.addActionListener(e -> ajouterJoueur());
        btnModifier.addActionListener(e -> modifierJoueur());
        btnSupprimer.addActionListener(e -> supprimerJoueur());

        // 🔹 4. Ajout de la barre de navigation en bas
        
        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(formPanel, BorderLayout.NORTH); // Formulaire en haut
        southContainer.add(createNavigationBar(), BorderLayout.SOUTH); // Navigation en bas
        
        add(southContainer, BorderLayout.SOUTH);
    }
    
    private JTable createTable() {
        // Créer le modèle de table
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Prénom", "Poste", "Date de Naissance", "Équipe"}, 0);
        JTable table = new JTable(tableModel);

        // Appliquer le style au tableau
        table.setBackground(FORM_BACKGROUND_COLOR);
        table.setForeground(TEXT_COLOR);
        table.setFont(REGULAR_FONT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Appliquer le style à l'en-tête du tableau
        JTableHeader header = table.getTableHeader();
        header.setBackground(TEXT_COLOR);
        header.setForeground(FORM_BACKGROUND_COLOR);
        header.setFont(BOLD_FONT);

        // Ajouter un écouteur de sélection pour charger les données du joueur sélectionné
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormWithSelectedPlayer();
            }
        });

        return table;
    }
    
    private void fillFormWithSelectedPlayer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            txtNom.setText((String) tableModel.getValueAt(selectedRow, 1)); // Nom
            txtPrenom.setText((String) tableModel.getValueAt(selectedRow, 2)); // Prénom
            txtPoste.setText((String) tableModel.getValueAt(selectedRow, 3)); // Poste
            
            // Convertir la date de naissance en objet Date
            String dateNaissanceStr = (String) tableModel.getValueAt(selectedRow, 4);
            try {
                Date dateNaissance = new SimpleDateFormat("yyyy-MM-dd").parse(dateNaissanceStr);
                dateChooserNaissance.setDate(dateNaissance);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Sélectionner l'équipe correspondante dans le ComboBox
            String equipeNom = (String) tableModel.getValueAt(selectedRow, 5); // Nom de l'équipe
            for (int i = 0; i < comboEquipe.getItemCount(); i++) {
                if (comboEquipe.getItemAt(i).equals(equipeNom)) {
                    comboEquipe.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    // 🔹 Méthode pour créer la barre de navigation
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

    // 🔹 Méthode pour créer un bouton de navigation
    private JButton createNavigationButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(FORM_BACKGROUND_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFont(BUTTON_FONT);
        button.setBorder(new LineBorder(TEXT_COLOR, 1, true));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 30));

        // Désactiver le bouton de la page actuelle
        if (text.equals("Joueur")) {
            button.setEnabled(false);
        }

        button.addActionListener(e -> navigateToPage(text));
        return button;
    }

    // 🔹 Méthode pour naviguer entre les pages
    private void navigateToPage(String page) {
        // Obtenir le conteneur parent (MainFrame)
        Container parent = getParent();
        if (parent != null) {
            parent.removeAll();
            
            // Ajouter le panneau approprié en fonction de la navigation
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
            
            // Rafraîchir le conteneur
            parent.revalidate();
            parent.repaint();
        }
    }

    // 🔹 Méthode pour créer un TitledBorder stylisé
    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(TEXT_COLOR, 1),
            title,
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            BUTTON_FONT,
            TEXT_COLOR
        );
    }

    // 🔹 Méthodes de création de composants stylisés
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(FORM_BACKGROUND_COLOR);
        textField.setForeground(TEXT_COLOR);
        textField.setFont(REGULAR_FONT);
        textField.setBorder(new LineBorder(TEXT_COLOR, 1));
        return textField;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(BOLD_FONT);
        return label;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBackground(FORM_BACKGROUND_COLOR);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setFont(REGULAR_FONT);
        return comboBox;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(FORM_BACKGROUND_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFont(BUTTON_FONT);
        button.setBorder(new LineBorder(TEXT_COLOR, 1, true));
        button.setFocusPainted(false);
        return button;
    }

    // 🔹 Méthodes de gestion des joueurs
    private void chargerJoueurs() {
        tableModel.setRowCount(0);
        List<Joueur> joueurs = joueurDAO.listerJoueurs();
        List<Equipe> equipes = equipeDAO.listerEquipes();

        for (Joueur j : joueurs) {
            // Trouver le nom de l'équipe correspondante
            String nomEquipe = "Non assigné";
            for (Equipe equipe : equipes) {
                if (equipe.getId() == j.getEquipeId()) {
                    nomEquipe = equipe.getNom();
                    break;
                }
            }
            
            tableModel.addRow(new Object[]{
                j.getId(), 
                j.getNom(), 
                j.getPrenom(), 
                j.getPoste(), 
                j.getDateNaissance(), 
                nomEquipe  // Au lieu de j.getEquipeId()
            });
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
        
        Date dateNaissance = dateChooserNaissance.getDate();
        String dateNaissanceStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(dateNaissance);

        String equipeNom = (String) comboEquipe.getSelectedItem();

        int equipeId = equipeDAO.getIdParNom(equipeNom);

        joueurDAO.ajouterJoueur(new Joueur(nom, prenom, poste, dateNaissanceStr, equipeId));
        chargerJoueurs();
        viderChamps();
    }

    private void modifierJoueur() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un joueur à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0); // ID du joueur sélectionné

        // Récupérer les nouvelles valeurs du formulaire
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String poste = txtPoste.getText().trim();
        
        Date dateNaissance = dateChooserNaissance.getDate();
        String dateNaissanceStr = new SimpleDateFormat("yyyy-MM-dd").format(dateNaissance);

        String equipeNom = (String) comboEquipe.getSelectedItem();
        int equipeId = equipeDAO.getIdParNom(equipeNom);

        // Créer un nouvel objet Joueur avec les données du formulaire
        Joueur joueurModifie = new Joueur(nom, prenom, poste, dateNaissanceStr, equipeId);
        joueurModifie.setId(id);

        // Appeler la méthode du DAO pour mettre à jour le joueur
        joueurDAO.modifierJoueur(id, joueurModifie);
        
        // Recharger les données et nettoyer le formulaire
        chargerJoueurs();
        viderChamps();
        
        // Afficher un message de confirmation
        JOptionPane.showMessageDialog(
            this,
            "Le joueur a été modifié avec succès",
            "Succès",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private boolean validateForm() {
        if (txtNom.getText().trim().isEmpty() ||
            txtPrenom.getText().trim().isEmpty() ||
            txtPoste.getText().trim().isEmpty() ||
            dateChooserNaissance.getDate() == null ||
            comboEquipe.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
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
        dateChooserNaissance.setDate(null);  // Réinitialiser la date
        comboEquipe.setSelectedIndex(0);
    }
}