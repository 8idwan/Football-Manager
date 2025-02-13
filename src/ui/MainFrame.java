package ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.swing.border.LineBorder;

/**
 * Fenêtre principale de l'application Football Manager.
 * Cette fenêtre inclut un titre, un logo, et des boutons de navigation pour accéder à différentes sections de l'application.
 */
public class MainFrame extends JFrame {
    
    // Constants for colors and dimensions
    private static final Color BACKGROUND_COLOR = Color.decode("#cccebf");
    private static final Color BUTTON_BACKGROUND = Color.decode("#f0eae4");
    private static final Color TEXT_COLOR = Color.decode("#5d4024");
    private static final Dimension BUTTON_SIZE = new Dimension(150, 40);
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int LOGO_WIDTH = 60;
    private static final int LOGO_HEIGHT = 80;

    private final JPanel mainPanel;
    private final String[] navigationButtons = {"Equipe", "Joueur", "Entraineur", "Match"};

    /**
     * Constructeur de la fenêtre principale.
     * Initialise la fenêtre et le panneau principal.
     */
    public MainFrame() {
        initializeFrame();
        mainPanel = createMainPanel();
        add(mainPanel);
    }

    /**
     * Initialise la fenêtre (titre, taille, comportement de fermeture, etc.).
     */
    private void initializeFrame() {
        setTitle("Football Manager");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centre la fenêtre
    }

    /**
     * Crée le panneau principal de la fenêtre.
     * @return Le panneau principal contenant les éléments graphiques.
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        JPanel centerPanel = createCenterPanel();
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Crée le panneau central contenant le logo, le titre et les boutons de navigation.
     * @return Le panneau central.
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel logoTitlePanel = createLogoTitlePanel();
        JPanel buttonPanel = createButtonPanel();

        centerPanel.add(Box.createHorizontalGlue());
        centerPanel.add(logoTitlePanel);
        centerPanel.add(Box.createHorizontalStrut(50));
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createHorizontalGlue());

        return centerPanel;
    }

    /**
     * Crée un panneau contenant le logo et le titre de l'application.
     * @return Le panneau avec le logo et le titre.
     */
    private JPanel createLogoTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel logoLabel = createLogoLabel();
        JPanel titlePanel = createTitlePanel();

        panel.add(logoLabel);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(titlePanel);

        return panel;
    }

    /**
     * Crée un label pour afficher le logo de l'application.
     * @return Le label contenant le logo.
     */
    private JLabel createLogoLabel() {
        String logoPath = "./logo.png";
        ImageIcon logoIcon;

        File file = new File(logoPath);
        if (file.exists()) {
            logoIcon = new ImageIcon(logoPath);
            Image img = logoIcon.getImage().getScaledInstance(LOGO_WIDTH, LOGO_HEIGHT, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(img);
        } else {
            logoIcon = new ImageIcon();
            System.err.println("⚠ Logo not found: " + file.getAbsolutePath());
        }

        return new JLabel(logoIcon);
    }

    /**
     * Crée un panneau contenant le titre de l'application (Football Manager).
     * @return Le panneau avec le titre.
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);

        String[] titleWords = {"Football", "Manager"};
        for (String word : titleWords) {
            JLabel label = new JLabel(word);
            label.setFont(new Font("Arial", Font.BOLD, 30));
            label.setForeground(TEXT_COLOR);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(label);
        }

        return panel;
    }

    /**
     * Crée un panneau contenant les boutons de navigation.
     * @return Le panneau avec les boutons.
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BUTTON_BACKGROUND);
        panel.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (String buttonLabel : navigationButtons) {
            JButton button = createNavigationButton(buttonLabel);
            panel.add(button);
            panel.add(Box.createVerticalStrut(10));
        }

        return panel;
    }

    /**
     * Crée un bouton de navigation pour accéder aux différentes sections.
     * @param label Le texte du bouton.
     * @return Le bouton de navigation.
     */
    private JButton createNavigationButton(String label) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(BUTTON_SIZE);
        button.setBackground(BUTTON_BACKGROUND);
        button.setForeground(TEXT_COLOR);
        button.setFont(new Font("Times New Roman", Font.BOLD, 14));
        button.setBorder(new LineBorder(TEXT_COLOR, 1, true));
        button.setFocusPainted(false);

        button.addActionListener(e -> handleNavigation(label));

        return button;
    }

    /**
     * Gère la navigation lorsque l'un des boutons est cliqué.
     * @param label Le texte du bouton cliqué.
     */
    private void handleNavigation(String label) {
        JPanel newPanel = switch (label) {
            case "Equipe" -> new EquipePanel();
            case "Joueur" -> new JoueurPanel();
            case "Entraineur" -> new EntraineurPanel();
            case "Match" -> new MatchPanel();
            default -> new JPanel();
        };
        setContent(mainPanel, newPanel);
    }

    /**
     * Remplace le contenu du panneau principal avec un nouveau panneau.
     * @param mainPanel Le panneau principal.
     * @param newPanel Le nouveau panneau à afficher.
     */
    private void setContent(JPanel mainPanel, JPanel newPanel) {
        mainPanel.removeAll();
        mainPanel.add(newPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Méthode principale pour démarrer l'application.
     * @param args Les arguments en ligne de commande.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
