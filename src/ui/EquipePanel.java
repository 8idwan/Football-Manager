package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import dao.EntraineurDAO;
import dao.EquipeDAO;
import models.Entraineur;
import models.Equipe;

import java.awt.*;
import java.util.List;

public class EquipePanel extends JPanel {
    // Constants
    private static final Color BACKGROUND_COLOR = Color.decode("#cccebf");
    private static final Color FORM_BACKGROUND_COLOR = Color.decode("#f0eae4");
    private static final Color TEXT_COLOR = Color.decode("#5d4024");
    private static final Font REGULAR_FONT = new Font("Times New Roman", Font.PLAIN, 12);
    private static final Font BOLD_FONT = new Font("Times New Roman", Font.BOLD, 12);
    private static final Font BUTTON_FONT = new Font("Times New Roman", Font.BOLD, 14);
    
    // Data Access Objects
    private final EquipeDAO equipeDAO;
    private final EntraineurDAO entraineurDAO;
    
    // UI Components
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField txtNom;
    private final JTextField txtVille;
    private final JTextField txtPays;
    private final JComboBox<EntraineurComboItem> comboEntraineur;
    private final JPanel mainContent;

    public EquipePanel() {
        this.equipeDAO = new EquipeDAO();
        this.entraineurDAO = new EntraineurDAO();
        
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Initialize components
        this.tableModel = createTableModel();
        this.table = createTable();
        this.txtNom = createStyledTextField();
        this.txtVille = createStyledTextField();
        this.txtPays = createStyledTextField();
        this.comboEntraineur = createStyledComboBox();
        this.mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND_COLOR);

        // Setup UI
        setupMainPanel();
        add(mainContent, BorderLayout.CENTER);
        add(createNavigationBar(), BorderLayout.SOUTH);
        loadData();
    }

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

    private JButton createNavigationButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(FORM_BACKGROUND_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFont(BUTTON_FONT);
        button.setBorder(new LineBorder(TEXT_COLOR, 1, true));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 30));

        // Disable the current page button
        if (text.equals("Equipe")) {
            button.setEnabled(false);
        }

        button.addActionListener(e -> navigateToPage(text));
        return button;
    }

    private void navigateToPage(String page) {
        // Get the parent container (MainFrame)
        Container parent = getParent();
        if (parent != null) {
            parent.removeAll();
            
            // Add the appropriate panel based on navigation
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
            
            // Refresh the container
            parent.revalidate();
            parent.repaint();
        }
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(
            new String[]{"ID", "Nom", "Ville", "Pays", "Entraîneur"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void setupMainPanel() {
        // Table Panel
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(FORM_BACKGROUND_COLOR);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        // Form Panel
        mainContent.add(createFormPanel(), BorderLayout.SOUTH);
    }

    private JTable createTable() {
        JTable table = new JTable(tableModel);
        styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormWithSelectedTeam();
            }
        });
        return table;
    }

    private void styleTable(JTable table) {
        table.setBackground(FORM_BACKGROUND_COLOR);
        table.setForeground(TEXT_COLOR);
        table.setFont(REGULAR_FONT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(TEXT_COLOR);
        header.setForeground(FORM_BACKGROUND_COLOR);
        header.setFont(BOLD_FONT);
    }


    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(FORM_BACKGROUND_COLOR);
        formPanel.setBorder(createTitledBorder("Gestion des Équipes"));

        // Add form components
        formPanel.add(createStyledLabel("Nom:"));
        formPanel.add(txtNom);
        formPanel.add(createStyledLabel("Ville:"));
        formPanel.add(txtVille);
        formPanel.add(createStyledLabel("Pays:"));
        formPanel.add(txtPays);
        formPanel.add(createStyledLabel("Entraîneur:"));
        formPanel.add(comboEntraineur);
        formPanel.add(createButtonPanel());

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(FORM_BACKGROUND_COLOR);

        JButton btnAjouter = createStyledButton("Ajouter");
        JButton btnModifier = createStyledButton("Modifier");
        JButton btnSupprimer = createStyledButton("Supprimer");

        btnAjouter.addActionListener(e -> ajouterEquipe());
        btnModifier.addActionListener(e -> modifierEquipe());
        btnSupprimer.addActionListener(e -> supprimerEquipe());

        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);

        return buttonPanel;
    }

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

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(FORM_BACKGROUND_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFont(BUTTON_FONT);
        button.setBorder(new LineBorder(TEXT_COLOR, 1, true));
        button.setFocusPainted(false);
        return button;
    }

    private JComboBox<EntraineurComboItem> createStyledComboBox() {
        JComboBox<EntraineurComboItem> comboBox = new JComboBox<>();
        comboBox.setBackground(FORM_BACKGROUND_COLOR);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setFont(REGULAR_FONT);
        return comboBox;
    }

    private void loadData() {
        loadEquipes();
        loadEntraineurs();
    }

    private void loadEquipes() {
        tableModel.setRowCount(0);
        List<Equipe> equipes = equipeDAO.listerEquipes();
        List<Entraineur> entraineurs = entraineurDAO.listerEntraineurs();

        for (Equipe equipe : equipes) {
            String nomEntraineur = entraineurs.stream()
                .filter(e -> e.getId() == equipe.getEntraineurId())
                .map(e -> e.getNom() + " " + e.getPrenom())
                .findFirst()
                .orElse("Non assigné");

            tableModel.addRow(new Object[]{
                equipe.getId(),
                equipe.getNom(),
                equipe.getVille(),
                equipe.getPays(),
                nomEntraineur
            });
        }
    }

    private void loadEntraineurs() {
        comboEntraineur.removeAllItems();
        entraineurDAO.listerEntraineurs().forEach(entraineur -> 
            comboEntraineur.addItem(new EntraineurComboItem(entraineur))
        );
    }

    private void ajouterEquipe() {
        if (!validateForm()) {
            return;
        }

        EntraineurComboItem selectedEntraineur = (EntraineurComboItem) comboEntraineur.getSelectedItem();
        if (selectedEntraineur == null) {
            showError("Veuillez sélectionner un entraîneur");
            return;
        }

        Equipe newEquipe = new Equipe(
            txtNom.getText().trim(),
            txtVille.getText().trim(),
            txtPays.getText().trim(),
            selectedEntraineur.getId()
        );

        equipeDAO.ajouterEquipe(newEquipe);
        loadEquipes();
        clearForm();
    }

    private void modifierEquipe() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Veuillez sélectionner une équipe à modifier");
            return;
        }

        if (!validateForm()) {
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        EntraineurComboItem selectedEntraineur = (EntraineurComboItem) comboEntraineur.getSelectedItem();
        
        if (selectedEntraineur == null) {
            showError("Veuillez sélectionner un entraîneur");
            return;
        }

        // Créer un nouvel objet Equipe avec les données du formulaire
        Equipe equipeModifiee = new Equipe(
            txtNom.getText().trim(),
            txtVille.getText().trim(),
            txtPays.getText().trim(),
            selectedEntraineur.getId()
        );

        // Appeler la méthode du DAO avec l'ID et le nouvel objet Equipe
        equipeDAO.modifierEquipe(id, equipeModifiee);
        
        // Recharger les données et nettoyer le formulaire
        loadEquipes();
        clearForm();
        
        // Afficher un message de confirmation
        JOptionPane.showMessageDialog(
            this,
            "L'équipe a été modifiée avec succès",
            "Succès",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Ajouter cette méthode pour remplir le formulaire avec les données de l'équipe sélectionnée
    private void fillFormWithSelectedTeam() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            txtNom.setText((String) tableModel.getValueAt(selectedRow, 1));
            txtVille.setText((String) tableModel.getValueAt(selectedRow, 2));
            txtPays.setText((String) tableModel.getValueAt(selectedRow, 3));
            
            // Rechercher et sélectionner l'entraîneur dans le ComboBox
            String entraineurNom = (String) tableModel.getValueAt(selectedRow, 4);
            for (int i = 0; i < comboEntraineur.getItemCount(); i++) {
                EntraineurComboItem item = comboEntraineur.getItemAt(i);
                if (item.toString().equals(entraineurNom)) {
                    comboEntraineur.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void supprimerEquipe() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Veuillez sélectionner une équipe à supprimer");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        if (confirmDeletion()) {
            equipeDAO.supprimerEquipe(id);
            loadEquipes();
        }
    }

    private boolean validateForm() {
        if (txtNom.getText().trim().isEmpty() ||
            txtVille.getText().trim().isEmpty() ||
            txtPays.getText().trim().isEmpty()) {
            showError("Tous les champs sont obligatoires");
            return false;
        }
        return true;
    }

    private boolean confirmDeletion() {
        return JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous vraiment supprimer cette équipe ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Erreur",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void clearForm() {
        txtNom.setText("");
        txtVille.setText("");
        txtPays.setText("");
        if (comboEntraineur.getItemCount() > 0) {
            comboEntraineur.setSelectedIndex(0);
        }
    }

    // Helper class for ComboBox
    private static class EntraineurComboItem {
        private final int id;
        private final String displayName;

        public EntraineurComboItem(Entraineur entraineur) {
            this.id = entraineur.getId();
            this.displayName = entraineur.getNom() + " " + entraineur.getPrenom();
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}