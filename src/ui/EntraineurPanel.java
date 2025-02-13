package ui;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import dao.EntraineurDAO;
import models.Entraineur;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.*;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;

/**
 * Panel permettant de gérer les entraîneurs dans une interface graphique.
 * Il inclut des fonctionnalités pour ajouter, modifier, supprimer des entraîneurs,
 * afficher la liste dans un tableau, et exporter la liste au format PDF.
 * La recherche dans le tableau est également supportée.
 */

public class EntraineurPanel extends JPanel {
	// Constants
	private static final Color BACKGROUND_COLOR = Color.decode("#cccebf");
	private static final Color FORM_BACKGROUND_COLOR = Color.decode("#f0eae4");
	private static final Color TEXT_COLOR = Color.decode("#5d4024");
	private static final Font REGULAR_FONT = new Font("Times New Roman", Font.PLAIN, 12);
	private static final Font BOLD_FONT = new Font("Times New Roman", Font.BOLD, 12);
	private static final Font BUTTON_FONT = new Font("Times New Roman", Font.BOLD, 14);

	// Data Access Objects
	private final EntraineurDAO entraineurDAO;

	// UI Components
	private final JTable table;
	private final DefaultTableModel tableModel;
	private final JTextField txtNom, txtPrenom;
	private final JDateChooser dateChooserNaissance;
	private final JButton btnAjouter;
	private boolean isEditing = false; // Variable pour suivre l'état
    private final JTextField txtRecherche; // Champ de recherche
    private final JButton btnExporter;

    public EntraineurPanel() {
        this.entraineurDAO = new EntraineurDAO();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Initialize components
        this.tableModel = createTableModel();
        this.table = createTable();
        this.txtNom = createStyledTextField();
        this.txtPrenom = createStyledTextField();
        this.dateChooserNaissance = new JDateChooser();
        this.btnAjouter = createStyledButton("Ajouter");
        this.btnExporter = createStyledButton("Exporter");

        
        // Initialisation du champ de recherche
        this.txtRecherche = createStyledTextField();
        txtRecherche.setPreferredSize(new Dimension(200, 30));
        txtRecherche.setText(" Recherche..."); // Texte par défaut
        txtRecherche.setForeground(Color.GRAY); // Couleur grise pour le placeholder

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
                    txtRecherche.setText("Recherche...");
                    txtRecherche.setForeground(Color.GRAY);
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

	private JButton createNavigationButton(String text) {
		JButton button = new JButton(text);
		button.setBackground(FORM_BACKGROUND_COLOR);
		button.setForeground(TEXT_COLOR);
		button.setFont(BUTTON_FONT);
		button.setBorder(new LineBorder(TEXT_COLOR, 1, true));
		button.setFocusPainted(false);
		button.setPreferredSize(new Dimension(120, 30));

		if (text.equals("Entraineur")) {
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
		return new DefaultTableModel(new String[] { "ID", "Nom", "Prénom", "Date de Naissance", "Action" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 4; // Seule la colonne "Action" est éditable
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
		private final JButton btnModifier;
		private final JButton btnSupprimer;

		public ButtonRenderer() {
			setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			setOpaque(true);

			btnModifier = createStyledButton("Modifier");
			btnSupprimer = createStyledButton("Supprimer");

			add(btnModifier);
			add(btnSupprimer);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return this;
		}
	}

	class ButtonEditor extends DefaultCellEditor {
		private final JButton btnModifier;
		private final JButton btnSupprimer;
		private JPanel panel;
		private int selectedRow;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);

			btnModifier = createStyledButton("Modifier");
			btnSupprimer = createStyledButton("Supprimer");

			btnModifier.addActionListener(e -> {
				fireEditingStopped();
				remplirFormulairePourModification(selectedRow);
			});

			btnSupprimer.addActionListener(e -> {
				fireEditingStopped();
				supprimerEntraineur(selectedRow);
			});

			panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
			panel.add(btnModifier);
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
	private void filtrerTableau() {
	    String recherche = txtRecherche.getText().trim().toLowerCase();
	    
	    // Ignorer "Recherche..." dans la recherche
	    if (recherche.equals(" recherche...")) {
	        return; // Ne pas faire de recherche si c'est le texte par défaut
	    }

	    tableModel.setRowCount(0); // Effacer les données actuelles du tableau

	    List<Entraineur> entraineurs = entraineurDAO.listerEntraineurs();
	    for (Entraineur entraineur : entraineurs) {
	        if (entraineur.getNom().toLowerCase().contains(recherche) ||
	            entraineur.getPrenom().toLowerCase().contains(recherche)) {
	            tableModel.addRow(new Object[] {
	                entraineur.getId(),
	                entraineur.getNom(),
	                entraineur.getPrenom(),
	                entraineur.getDateNaissance(),
	                "" // Texte vide pour la colonne "Action"
	            });
	        }
	    }
	}


	private JPanel createFormPanel() {
		JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
		formPanel.setBackground(FORM_BACKGROUND_COLOR);
		formPanel.setBorder(createTitledBorder("Gestion des Entraîneurs"));

		formPanel.add(createStyledLabel("Nom:"));
		formPanel.add(txtNom);
		formPanel.add(createStyledLabel("Prénom:"));
		formPanel.add(txtPrenom);
		formPanel.add(createStyledLabel("Date de Naissance:"));
		formPanel.add(dateChooserNaissance);
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
					modifierEntraineur(id);
				}
			} else {
				ajouterEntraineur();
			}
		});
		
        btnExporter.addActionListener(e -> exporterVersPDF());


		buttonPanel.add(btnAjouter);
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
	            Paragraph title = new Paragraph("Liste des Entraineurs");
	            title.setFont(titleFont);
	            title.setAlignment(Element.ALIGN_CENTER);
	            title.setSpacingAfter(20);
	            document.add(title);

	            // Création du tableau
	            PdfPTable pdfTable = new PdfPTable(4); // 4 colonnes
	            pdfTable.setWidthPercentage(100);

	            // En-têtes
	            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	            String[] headers = {"ID", "Nom", "Prénom", "Date de Naissance"};
	            for (String header : headers) {
	                PdfPCell cell = new PdfPCell(new Phrase(header));
	                cell.getPhrase().setFont(headerFont);
	                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	                cell.setPadding(5);
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
	                    cell.setPadding(4);
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

	// Data Loading
	private void loadData() {
		loadEntraineurs();
	}

	private void loadEntraineurs() {
		tableModel.setRowCount(0);
		List<Entraineur> entraineurs = entraineurDAO.listerEntraineurs();

		for (Entraineur entraineur : entraineurs) {
			tableModel.addRow(new Object[] { entraineur.getId(), entraineur.getNom(), entraineur.getPrenom(),
					entraineur.getDateNaissance(), "" // Texte vide pour la colonne "Action"
			});
		}
	}

	// Form Actions
	private void ajouterEntraineur() {
		if (!validateForm())
			return;

		String nom = txtNom.getText().trim();
		String prenom = txtPrenom.getText().trim();
		Date dateNaissance = dateChooserNaissance.getDate();
		String dateNaissanceStr = new SimpleDateFormat("yyyy-MM-dd").format(dateNaissance);

		Entraineur newEntraineur = new Entraineur(nom, prenom, dateNaissanceStr);
		entraineurDAO.ajouterEntraineur(newEntraineur);
		loadEntraineurs();
		clearForm();
	}

	private void remplirFormulairePourModification(int row) {
		if (row != -1) {
			txtNom.setText((String) tableModel.getValueAt(row, 1));
			txtPrenom.setText((String) tableModel.getValueAt(row, 2));

			String dateNaissanceStr = (String) tableModel.getValueAt(row, 3);
			try {
				Date dateNaissance = new SimpleDateFormat("yyyy-MM-dd").parse(dateNaissanceStr);
				dateChooserNaissance.setDate(dateNaissance);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			setEditingMode(true);
		}
	}

	private void modifierEntraineur(int id) {
		if (!validateForm())
			return;

		String nom = txtNom.getText().trim();
		String prenom = txtPrenom.getText().trim();
		Date dateNaissance = dateChooserNaissance.getDate();
		String dateNaissanceStr = new SimpleDateFormat("yyyy-MM-dd").format(dateNaissance);

		Entraineur entraineurModifie = new Entraineur(nom, prenom, dateNaissanceStr);
		entraineurModifie.setId(id);

		entraineurDAO.modifierEntraineur(id, entraineurModifie);
		loadEntraineurs();
		clearForm();
	}

	private void supprimerEntraineur(int row) {
		int id = (int) tableModel.getValueAt(row, 0);
		if (confirmDeletion()) {
			entraineurDAO.supprimerEntraineur(id);
			loadEntraineurs();
		}
	}

	// Helper Methods
	private void setEditingMode(boolean editing) {
		isEditing = editing;
		btnAjouter.setText(editing ? "Sauvegarder" : "Ajouter");
	}

	private boolean validateForm() {
		if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty()
				|| dateChooserNaissance.getDate() == null) {
			showError("Tous les champs sont obligatoires");
			return false;
		}
		return true;
	}

	private boolean confirmDeletion() {
		return JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cet entraîneur ?", "Confirmation",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
	}

	private void clearForm() {
		txtNom.setText("");
		txtPrenom.setText("");
		dateChooserNaissance.setDate(null);
		setEditingMode(false);
	}

	// Méthodes de création de composants stylisés
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

	private TitledBorder createTitledBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(TEXT_COLOR, 1), title,
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, BUTTON_FONT, TEXT_COLOR);
	}

}