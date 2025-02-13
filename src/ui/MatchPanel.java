package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import dao.MatchDAO;
import dao.EquipeDAO;
import models.Match;
import models.Equipe;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;

import com.itextpdf.text.*;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;

/**
 * Panneau principal pour la gestion des matchs dans l'application.
 * Il permet d'ajouter, modifier, supprimer et simuler des matchs, ainsi que d'exporter les données 
 * vers un fichier PDF. 
 */

public class MatchPanel extends JPanel {
	// Constants
	private static final Color BACKGROUND_COLOR = Color.decode("#cccebf");
	private static final Color FORM_BACKGROUND_COLOR = Color.decode("#f0eae4");
	private static final Color TEXT_COLOR = Color.decode("#5d4024");
	private static final Font REGULAR_FONT = new Font("Times New Roman", Font.PLAIN, 12);
	private static final Font BOLD_FONT = new Font("Times New Roman", Font.BOLD, 12);
	private static final Font BUTTON_FONT = new Font("Times New Roman", Font.BOLD, 14);

	// Data Access Objects
	private final MatchDAO matchDAO;
	private final EquipeDAO equipeDAO;

	// UI Components
	private final JTable table;
	private final DefaultTableModel tableModel;
	private final JTextField txtNom, txtLieu;
	private final JComboBox<String> comboEquipe1, comboEquipe2;
	private final JDateChooser dateChooser; // Calendrier pour la date
	private final JButton btnAjouter, btnAgenda;
	private boolean isEditing = false; // Variable pour suivre l'état
	private List<Equipe> equipes;
	private final JTextField txtRecherche;
    private final JButton btnExporter;


	public MatchPanel() {
		this.matchDAO = new MatchDAO();
		this.equipeDAO = new EquipeDAO();

		setLayout(new BorderLayout());
		setBackground(BACKGROUND_COLOR);

		// Initialize components
		this.tableModel = createTableModel();
		this.table = createTable();
		this.txtNom = createStyledTextField();
		this.txtLieu = createStyledTextField();
		this.dateChooser = new JDateChooser(); // Initialisation du calendrier
		this.comboEquipe1 = createStyledComboBox();
		this.comboEquipe2 = createStyledComboBox();
		this.btnAjouter = createStyledButton("Ajouter");
	    this.btnAgenda = createStyledButton("Agenda"); // Initialisation du bouton Agenda
        this.btnExporter = createStyledButton("Exporter");

	    
	    this.txtRecherche = createStyledTextField();
	    txtRecherche.setPreferredSize(new Dimension(200, 30));
	    txtRecherche.setText(" Recherche...");
	    txtRecherche.setForeground(Color.GRAY);
	    
	 // FocusListener pour gérer le placeholder
	    txtRecherche.addFocusListener(new FocusAdapter() {
	        @Override
	        public void focusGained(FocusEvent e) {
	            if (txtRecherche.getText().equals(" Recherche...")) {
	                txtRecherche.setText("");
	                txtRecherche.setForeground(Color.BLACK);
	            }
	        }

	        @Override
	        public void focusLost(FocusEvent e) {
	            if (txtRecherche.getText().isEmpty()) {
	                txtRecherche.setText(" Recherche...");
	                txtRecherche.setForeground(Color.GRAY);
	                loadMatchs(); // Recharger toutes les données quand le champ est vide
	            }
	        }
	    });

	    // Ajout du listener de recherche instantanée
	    txtRecherche.getDocument().addDocumentListener(new DocumentListener() {
	        @Override
	        public void insertUpdate(DocumentEvent e) {
	            filtrerTableau();
	        }

	        @Override
	        public void removeUpdate(DocumentEvent e) {
	            filtrerTableau();
	        }

	        @Override
	        public void changedUpdate(DocumentEvent e) {
	            filtrerTableau();
	        }
	    });

		// Setup UI
		setupMainPanel();
		add(createNavigationBar(), BorderLayout.SOUTH);
		loadData();
	}

	// Navigation Bar
	private JPanel createNavigationBar() {
		JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		navPanel.setBackground(FORM_BACKGROUND_COLOR);
		navPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, TEXT_COLOR));

		String[] pages = { "Equipe", "Joueur", "Entraineur", "Match" };
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

		if (text.equals("Match")) {
			button.setEnabled(false); // Désactiver le bouton de la page actuelle
		}

		button.addActionListener(e -> navigateToPage(text));
		return button;
	}

	private void navigateToPage(String page) {
		Container parent = getParent();
		if (parent != null) {
			parent.removeAll();
			switch (page) {
			case "Equipe" -> parent.add(new EquipePanel());
			case "Joueur" -> parent.add(new JoueurPanel());
			case "Entraineur" -> parent.add(new EntraineurPanel());
			case "Match" -> parent.add(new MatchPanel());
			}
			parent.revalidate();
			parent.repaint();
		}
	}

	// Table Setup
	private DefaultTableModel createTableModel() {
		return new DefaultTableModel(
				new String[] { "ID", "Compétition", "Lieu", "Équipe 1", "Équipe 2", "Date", "Résultat", "Action" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 7; // Seule la colonne "Action" est éditable
			}
		};
	}

	private JTable createTable() {
		JTable table = new JTable(tableModel);
		styleTable(table);

		// Ajouter un renderer et un éditeur pour la colonne "Action"
		table.getColumn("Action").setCellRenderer(new ButtonRenderer());
		table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

		return table;
	}

	// Renderer et Éditeur pour les boutons
	class ButtonRenderer extends JPanel implements TableCellRenderer {
		private final JButton btnSimuler;
		private final JButton btnSupprimer;

		public ButtonRenderer() {
			setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			setOpaque(true);

			btnSimuler = createStyledButton("Simuler");
			btnSupprimer = createStyledButton("Supprimer");

			add(btnSimuler);
			add(btnSupprimer);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return this;
		}
	}

	class ButtonEditor extends DefaultCellEditor {
		private final JButton btnSimuler;
		private final JButton btnSupprimer;
		private JPanel panel;
		private int selectedRow;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);

			btnSimuler = createStyledButton("Simuler");
			btnSupprimer = createStyledButton("Supprimer");

			btnSimuler.addActionListener(e -> {
				fireEditingStopped();
				simulerMatch(selectedRow);
			});

			btnSupprimer.addActionListener(e -> {
				fireEditingStopped();
				supprimerMatch(selectedRow);
			});

			panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
			panel.add(btnSimuler);
			panel.add(btnSupprimer);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			selectedRow = row;
			return panel;
		}

		@Override
		public Object getCellEditorValue() {
			return "";
		}
	}

	// Form Panel
	private void setupMainPanel() {
	    JPanel mainContent = new JPanel(new BorderLayout());
	    mainContent.setBackground(BACKGROUND_COLOR);

	    // Barre de recherche
	    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
	    searchPanel.setBackground(BACKGROUND_COLOR);
	    searchPanel.add(txtRecherche);

	    mainContent.add(searchPanel, BorderLayout.NORTH);

	    // Table Panel
	    JScrollPane scrollPane = new JScrollPane(table);
	    scrollPane.getViewport().setBackground(FORM_BACKGROUND_COLOR);
	    mainContent.add(scrollPane, BorderLayout.CENTER);

	    // Form Panel
	    mainContent.add(createFormPanel(), BorderLayout.SOUTH);
	    add(mainContent, BorderLayout.CENTER);
	}
	
	private void filtrerTableau() {
	    String recherche = txtRecherche.getText().trim().toLowerCase();
	    
	    // Si le champ est vide ou contient le placeholder, afficher toutes les données
	    if (recherche.isEmpty() || recherche.equals("recherche...")) {
	        loadMatchs(); // Recharger toutes les données
	        return;
	    }

	    tableModel.setRowCount(0); // Effacer les données actuelles du tableau
	    List<Match> matchs = matchDAO.listerMatchs();
	    List<Equipe> equipes = equipeDAO.listerEquipes();

	    for (Match match : matchs) {
	        String nomEquipe1 = "Non assigné";
	        String nomEquipe2 = "Non assigné";

	        for (Equipe equipe : equipes) {
	            if (equipe.getId() == match.getEquipe1Id()) {
	                nomEquipe1 = equipe.getNom();
	            }
	            if (equipe.getId() == match.getEquipe2Id()) {
	                nomEquipe2 = equipe.getNom();
	            }
	        }

	        if (match.getNom().toLowerCase().contains(recherche) ||
	            match.getLieu().toLowerCase().contains(recherche) ||
	            nomEquipe1.toLowerCase().contains(recherche) ||
	            nomEquipe2.toLowerCase().contains(recherche)) {
	            
	            tableModel.addRow(new Object[] {
	                match.getId(),
	                match.getNom(),
	                match.getLieu(),
	                nomEquipe1,
	                nomEquipe2,
	                match.getDate(),
	                match.getResultat(),
	                "" // Texte vide pour la colonne "Action"
	            });
	        }
	    }
	}

	private JPanel createFormPanel() {
		JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
		formPanel.setBackground(FORM_BACKGROUND_COLOR);
		formPanel.setBorder(createTitledBorder("Gestion des Matchs"));

		formPanel.add(createStyledLabel("Compétition:"));
		formPanel.add(txtNom);
		formPanel.add(createStyledLabel("Lieu:"));
		formPanel.add(txtLieu);
		formPanel.add(createStyledLabel("Date:"));
		formPanel.add(dateChooser); // Ajout du calendrier
		formPanel.add(createStyledLabel("Équipe 1:"));
		formPanel.add(comboEquipe1);
		formPanel.add(createStyledLabel("Équipe 2:"));
		formPanel.add(comboEquipe2);
		formPanel.add(createButtonPanel());

		return formPanel;
	}

	private JPanel createButtonPanel() {
	    JPanel buttonPanel = new JPanel(new FlowLayout());
	    buttonPanel.setBackground(FORM_BACKGROUND_COLOR);

	    btnAjouter.addActionListener(e -> {
	        if (isEditing) {
	            int selectedRow = table.getSelectedRow();
	            if (selectedRow != -1) {
	                int id = (int) tableModel.getValueAt(selectedRow, 0);
	                modifierMatch(id);
	            }
	        } else {
	            ajouterMatch();
	        }
	    });

	    btnAgenda.addActionListener(e -> ouvrirAgenda()); // Action pour ouvrir l'agenda
        btnExporter.addActionListener(e -> exporterVersPDF());


	    buttonPanel.add(btnAjouter);
	    buttonPanel.add(btnAgenda); // Ajout du bouton Agenda
		buttonPanel.add(btnExporter);

	    return buttonPanel;
	}
	
	
	private void exporterVersPDF() {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Enregistrer le fichier PDF");
	    fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers PDF (*.pdf)", "pdf"));

	    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
	        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
	        if (!filePath.endsWith(".pdf")) {
	            filePath += ".pdf";
	        }

	        try {
	            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
	            PdfWriter.getInstance(document, new FileOutputStream(filePath));
	            document.open();

	            // Ajout du titre
	            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	            Paragraph title = new Paragraph("Liste des Matchs");
	            title.setFont(titleFont);
	            title.setAlignment(Element.ALIGN_CENTER);
	            title.setSpacingAfter(20);
	            document.add(title);

	            // Création du tableau
	            PdfPTable pdfTable = new PdfPTable(7); // 7 colonnes
	            pdfTable.setWidthPercentage(100);

	            // En-têtes
	            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	            String[] headers = {"ID", "Compétition", "Lieu", "Équipe 1", "Équipe 2", "Date", "Résultat"};
	            for (String header : headers) {
	                PdfPCell cell = new PdfPCell(new Phrase(header));
	                cell.getPhrase().setFont(headerFont);
	                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	                cell.setPadding(3);
	                pdfTable.addCell(cell);
	            }

	            // Données
	            com.itextpdf.text.Font contentFont = new com.itextpdf.text.Font(FontFamily.TIMES_ROMAN, 12, Font.ITALIC);
	            for (int i = 0; i < tableModel.getRowCount(); i++) {
	                for (int j = 0; j < headers.length; j++) {
	                    Object value = tableModel.getValueAt(i, j);
	                    PdfPCell cell = new PdfPCell(new Phrase(value != null ? value.toString() : ""));
	                    cell.getPhrase().setFont(contentFont);
	                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	                    cell.setPadding(5);
	                    pdfTable.addCell(cell);
	                }
	            }

	            document.add(pdfTable);
	            document.close();

	            JOptionPane.showMessageDialog(this,
	                "Export réussi !\nFichier sauvegardé : " + filePath,
	                "Succès",
	                JOptionPane.INFORMATION_MESSAGE);

	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(this,
	                "Erreur lors de l'export : " + ex.getMessage(),
	                "Erreur",
	                JOptionPane.ERROR_MESSAGE);
	            ex.printStackTrace();
	        }
	    }
	}

	
	private void ouvrirAgenda() {
	    JFrame agendaFrame = new JFrame("Agenda des Matchs");
	    agendaFrame.setSize(400, 300);
	    agendaFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    agendaFrame.setLocationRelativeTo(this);

	    // Création d'un calendrier JCalendar
	    JCalendar calendar = new JCalendar();
	    calendar.setBackground(FORM_BACKGROUND_COLOR);
	    calendar.setForeground(TEXT_COLOR);

	    // Essayer d'accéder aux boutons de navigation de manière plus sûre
	    for (Component component : calendar.getComponents()) {
	        if (component instanceof JPanel panel) {
	            for (Component subComponent : panel.getComponents()) {
	                if (subComponent instanceof JButton button) {
	                    button.setBackground(FORM_BACKGROUND_COLOR);
	                    button.setForeground(TEXT_COLOR);
	                    button.setFont(BUTTON_FONT);
	                    button.setBorder(new LineBorder(TEXT_COLOR, 1, true));
	                }
	            }
	        }
	    }

	    // Personnaliser les jours du mois (éviter l'accès direct via indices)
	    for (Component comp : calendar.getDayChooser().getDayPanel().getComponents()) {
	        if (comp instanceof JButton dayButton) {
	            dayButton.setBackground(FORM_BACKGROUND_COLOR);
	            dayButton.setForeground(TEXT_COLOR);
	            dayButton.setFont(REGULAR_FONT);
	            dayButton.setBorder(new LineBorder(TEXT_COLOR, 1, true));
	        }
	    }

	    // Charger les matchs et mettre en évidence les jours où il y a des matchs
	    List<Match> matchs = matchDAO.listerMatchs();
	    for (Match match : matchs) {
	        LocalDate matchDate = match.getDate();
	        Date date = Date.from(matchDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	        calendar.setDate(date);
	    }

	    // Ajouter un écouteur d'événements pour afficher les matchs du jour sélectionné
	    calendar.addPropertyChangeListener("calendar", evt -> {
	        Date selectedDate = calendar.getDate();
	        LocalDate selectedLocalDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

	        List<Match> matchsDuJour = matchs.stream()
	                .filter(m -> m.getDate().equals(selectedLocalDate))
	                .toList();

	        if (!matchsDuJour.isEmpty()) {
	            StringBuilder matchsInfo = new StringBuilder("Matchs prévus le " + selectedLocalDate + ":\n");
	            for (Match match : matchsDuJour) {
	                matchsInfo.append("- ").append(match.getNom()).append(" (").append(match.getLieu()).append(")\n");
	            }
	            JOptionPane.showMessageDialog(agendaFrame, matchsInfo.toString(), "Matchs du Jour", JOptionPane.INFORMATION_MESSAGE);
	        } else {
	            JOptionPane.showMessageDialog(agendaFrame, "Aucun match prévu pour ce jour.", "Matchs du Jour", JOptionPane.INFORMATION_MESSAGE);
	        }
	    });

	    // Ajouter le calendrier à la fenêtre et l'afficher
	    agendaFrame.add(calendar);
	    agendaFrame.setVisible(true);
	}


	// Data Loading
	private void loadData() {
		loadMatchs();
		loadEquipes();
	}

	private void loadMatchs() {
		tableModel.setRowCount(0);
		List<Match> matchs = matchDAO.listerMatchs();
		List<Equipe> equipes = equipeDAO.listerEquipes();

		for (Match match : matchs) {
			String nomEquipe1 = "Non assigné";
			String nomEquipe2 = "Non assigné";

			for (Equipe equipe : equipes) {
				if (equipe.getId() == match.getEquipe1Id()) {
					nomEquipe1 = equipe.getNom();
				}
				if (equipe.getId() == match.getEquipe2Id()) {
					nomEquipe2 = equipe.getNom();
				}
			}

			tableModel.addRow(new Object[] { match.getId(), match.getNom(), match.getLieu(), nomEquipe1, nomEquipe2,
					match.getDate(), match.getResultat(), "" // Texte vide pour la colonne "Action"
			});
		}
	}

	private void loadEquipes() {
		equipes = equipeDAO.listerEquipes();
		comboEquipe1.removeAllItems();
		comboEquipe2.removeAllItems();

		for (Equipe equipe : equipes) {
			comboEquipe1.addItem(equipe.getNom());
			comboEquipe2.addItem(equipe.getNom());
		}
	}

	// Form Actions
	private void ajouterMatch() {
		if (!validateForm())
			return;

		String nom = txtNom.getText().trim();
		String lieu = txtLieu.getText().trim();
		LocalDate date = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // Conversion
																											// de Date
																											// en
																											// LocalDate
		String nomEquipe1 = (String) comboEquipe1.getSelectedItem();
		String nomEquipe2 = (String) comboEquipe2.getSelectedItem();

		int equipe1Id = -1;
		int equipe2Id = -1;

		for (Equipe equipe : equipes) {
			if (equipe.getNom().equals(nomEquipe1)) {
				equipe1Id = equipe.getId();
			}
			if (equipe.getNom().equals(nomEquipe2)) {
				equipe2Id = equipe.getId();
			}
		}

		if (equipe1Id == equipe2Id) {
			JOptionPane.showMessageDialog(this, "Une équipe ne peut pas jouer contre elle-même.");
			return;
		}

		matchDAO.ajouterMatch(new Match(nom, lieu, equipe1Id, equipe2Id, "En attente", date));
		loadMatchs();
		clearForm();
	}

	private void simulerMatch(int row) {
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
		tableModel.setValueAt(resultat, row, 6);
	}

	private void supprimerMatch(int row) {
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Sélectionnez un match à supprimer.");
			return;
		}
		int id = (int) tableModel.getValueAt(row, 0);
		if (confirmDeletion()) {
			matchDAO.supprimerMatch(id);
			tableModel.removeRow(row);
		}
	}

	private void modifierMatch(int id) {
		if (!validateForm())
			return;

		String nom = txtNom.getText().trim();
		String lieu = txtLieu.getText().trim();
		LocalDate date = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // Conversion
																											// de Date
																											// en
																											// LocalDate
		String nomEquipe1 = (String) comboEquipe1.getSelectedItem();
		String nomEquipe2 = (String) comboEquipe2.getSelectedItem();
		String resultat = "En attente"; // Par défaut

		int equipe1Id = -1;
		int equipe2Id = -1;

		for (Equipe equipe : equipes) {
			if (equipe.getNom().equals(nomEquipe1)) {
				equipe1Id = equipe.getId();
			}
			if (equipe.getNom().equals(nomEquipe2)) {
				equipe2Id = equipe.getId();
			}
		}

		if (equipe1Id == equipe2Id) {
			JOptionPane.showMessageDialog(this, "Une équipe ne peut pas jouer contre elle-même.", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		Match matchModifie = new Match(nom, lieu, equipe1Id, equipe2Id, resultat, date);
		matchModifie.setId(id);

		matchDAO.modifierMatch(id, matchModifie);
		loadMatchs();
		clearForm();
	}

	// Helper Methods
	private void setEditingMode(boolean editing) {
		isEditing = editing;
		btnAjouter.setText(editing ? "Sauvegarder" : "Ajouter");
	}

	private boolean validateForm() {
		if (txtNom.getText().trim().isEmpty() || txtLieu.getText().trim().isEmpty() || dateChooser.getDate() == null
				|| comboEquipe1.getSelectedItem() == null || comboEquipe2.getSelectedItem() == null) {
			showError("Tous les champs sont obligatoires");
			return false;
		}
		return true;
	}

	private boolean confirmDeletion() {
		return JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce match ?", "Confirmation",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
	}

	private void clearForm() {
		txtNom.setText("");
		txtLieu.setText("");
		dateChooser.setDate(null); // Réinitialiser le calendrier
		if (comboEquipe1.getItemCount() > 0) {
			comboEquipe1.setSelectedIndex(0);
		}
		if (comboEquipe2.getItemCount() > 0) {
			comboEquipe2.setSelectedIndex(0);
		}
		setEditingMode(false);
	}

	// Méthodes de création de composants stylisés
	private void styleTable(JTable table) {
		table.setBackground(FORM_BACKGROUND_COLOR);
		table.setForeground(TEXT_COLOR);
		table.setFont(REGULAR_FONT);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JTableHeader header = table.getTableHeader();
		header.setBackground(TEXT_COLOR);
		header.setForeground(FORM_BACKGROUND_COLOR);
		header.setFont(BOLD_FONT);

		// Style spécifique pour la colonne "Action"
		table.getColumn("Action").setPreferredWidth(120);
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

	private JComboBox<String> createStyledComboBox() {
		JComboBox<String> comboBox = new JComboBox<>();
		comboBox.setBackground(FORM_BACKGROUND_COLOR);
		comboBox.setForeground(TEXT_COLOR);
		comboBox.setFont(REGULAR_FONT);
		return comboBox;
	}

	private TitledBorder createTitledBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(TEXT_COLOR, 1), title,
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, BUTTON_FONT, TEXT_COLOR);
	}
}