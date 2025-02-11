package ui;

import com.toedter.calendar.JDateChooser;  // Import du JDateChooser
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.LineBorder;

import dao.EntraineurDAO;
import models.Entraineur;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EntraineurPanel extends JPanel {
    private EntraineurDAO entraineurDAO = new EntraineurDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtPrenom;
    private JDateChooser dateChooserNaissance;

    private static final Color BACKGROUND_COLOR = Color.decode("#cccebf");
    private static final Color FORM_BACKGROUND_COLOR = Color.decode("#f0eae4");
    private static final Color TEXT_COLOR = Color.decode("#5d4024");

    public EntraineurPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        table = createTable(); // Utiliser la méthode createTable()        
        
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

        // Remplacer le champ de texte par un JDateChooser
        dateChooserNaissance = new JDateChooser();
        dateChooserNaissance.setBackground(FORM_BACKGROUND_COLOR);
        dateChooserNaissance.setForeground(TEXT_COLOR);
        dateChooserNaissance.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        dateChooserNaissance.setBorder(new LineBorder(TEXT_COLOR, 1));

        formPanel.add(createStyledLabel("Nom:")); formPanel.add(txtNom);
        formPanel.add(createStyledLabel("Prénom:")); formPanel.add(txtPrenom);
        formPanel.add(createStyledLabel("Date de Naissance:")); formPanel.add(dateChooserNaissance);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(FORM_BACKGROUND_COLOR);

        JButton btnAjouter = createStyledButton("Ajouter");
        JButton btnModifier = createStyledButton("Modifier");
        JButton btnSupprimer = createStyledButton("Supprimer");

        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);

        formPanel.add(buttonPanel);

        // 🔹 Ajouter la barre de navigation
        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(formPanel, BorderLayout.NORTH); // Formulaire en haut
        southContainer.add(createNavigationBar(), BorderLayout.SOUTH); // Navigation en bas

        add(southContainer, BorderLayout.SOUTH);

        // 🔹 3. Actions des boutons
        btnAjouter.addActionListener(e -> ajouterEntraineur());
        btnModifier.addActionListener(e -> modifierEntraineur());
        btnSupprimer.addActionListener(e -> supprimerEntraineur());
    }
    
    private JTable createTable() {
        // Créer le modèle de table avec les colonnes appropriées
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Prénom", "Date de Naissance"}, 0);
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

        // Ajouter un écouteur de sélection pour charger les données de l'entraîneur sélectionné
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormWithSelectedEntraineur(); // Charger les données de l'entraîneur sélectionné
            }
        });

        return table;
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
        button.setFont(new Font("Times New Roman", Font.BOLD, 14));
        button.setBorder(new LineBorder(TEXT_COLOR, 1, true));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 30));

        // Désactiver le bouton de la page actuelle
        if (text.equals("Entraineur")) {
            button.setEnabled(false);
        }

        button.addActionListener(e -> navigateToPage(text));
        return button;
    }

    // 🔹 Méthode pour naviguer entre les pages
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

    // 🔹 Méthodes pour charger et gérer les entraîneurs
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
        String dateNaissance = new java.text.SimpleDateFormat("yyyy-MM-dd").format(dateChooserNaissance.getDate());

        entraineurDAO.ajouterEntraineur(new Entraineur(nom, prenom, dateNaissance));
        chargerEntraineurs();
        viderChamps();
    }

    private void modifierEntraineur() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un entraîneur à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }
        

        int id = (int) tableModel.getValueAt(selectedRow, 0); // ID de l'entraîneur sélectionné

        // Récupérer les nouvelles valeurs du formulaire
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String dateNaissance = new java.text.SimpleDateFormat("yyyy-MM-dd").format(dateChooserNaissance.getDate());

        // Créer un nouvel objet Entraineur avec les données du formulaire
        Entraineur entraineurModifie = new Entraineur(nom, prenom, dateNaissance);
        entraineurModifie.setId(id);

        // Appeler la méthode du DAO pour mettre à jour l'entraîneur
        entraineurDAO.modifierEntraineur(id, entraineurModifie);
        
        // Recharger les données et nettoyer le formulaire
        chargerEntraineurs();
        viderChamps();
        
        // Afficher un message de confirmation
        JOptionPane.showMessageDialog(
            this,
            "L'entraîneur a été modifié avec succès",
            "Succès",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private boolean validateForm() {
        if (txtNom.getText().trim().isEmpty() ||
            txtPrenom.getText().trim().isEmpty() ||
            dateChooserNaissance.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    
    private void fillFormWithSelectedEntraineur() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            txtNom.setText((String) tableModel.getValueAt(selectedRow, 1)); // Nom
            txtPrenom.setText((String) tableModel.getValueAt(selectedRow, 2)); // Prénom
            
            // Convertir la date de naissance en objet Date
            String dateNaissanceStr = (String) tableModel.getValueAt(selectedRow, 3);
            try {
                Date dateNaissance = new SimpleDateFormat("yyyy-MM-dd").parse(dateNaissanceStr);
                dateChooserNaissance.setDate(dateNaissance);
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
        dateChooserNaissance.setDate(null);
    }

    // 🔹 Méthodes de création de composants stylisés (identiques à celles de JoueurPanel.java)
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
        button.setBorder(new LineBorder(TEXT_COLOR, 1));
        button.setFocusPainted(false);
        return button;
    }
}
