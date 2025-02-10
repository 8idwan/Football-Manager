package ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.swing.border.LineBorder;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Football Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal avec background #cccebf
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.decode("#cccebf"));
        
        // Panel central pour centrer horizontalement le contenu
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.setBackground(Color.decode("#cccebf"));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel pour le logo et le titre
        JPanel logoTitlePanel = new JPanel();
        logoTitlePanel.setLayout(new BoxLayout(logoTitlePanel, BoxLayout.X_AXIS));
        logoTitlePanel.setBackground(Color.decode("#cccebf"));
        logoTitlePanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        // Chargement correct de l'image de logo depuis les ressources
        URL logoURL = getClass().getResource("../ressources/logo.png");
        ImageIcon logoIcon;
        if (logoURL != null) {
            logoIcon = new ImageIcon(logoURL);
            // Redimensionner l'image si nécessaire
            Image img = logoIcon.getImage().getScaledInstance(60, 80, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(img);
        } else {
            // Fallback si l'image n'est pas trouvée
            logoIcon = new ImageIcon();
            System.err.println("Logo not found!");
        }
        
        JLabel logoLabel = new JLabel(logoIcon);
        
        // Panel pour le titre avec disposition verticale
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.decode("#cccebf"));
        
        // Labels pour chaque ligne du titre
        JLabel footballLabel = new JLabel("Football");
        footballLabel.setFont(new Font("Arial", Font.BOLD, 30));
        footballLabel.setForeground(Color.decode("#5d4024"));
        
        JLabel managerLabel = new JLabel("Manager");
        managerLabel.setFont(new Font("Arial", Font.BOLD, 30));
        managerLabel.setForeground(Color.decode("#5d4024"));
        
        // Centrer horizontalement les labels dans le titlePanel
        footballLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        managerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Ajouter les labels au titlePanel
        titlePanel.add(footballLabel);
        titlePanel.add(managerLabel);
        
        logoTitlePanel.add(logoLabel);
        logoTitlePanel.add(Box.createHorizontalStrut(10)); // Espace entre logo et titre
        logoTitlePanel.add(titlePanel);
        
        // Panel pour les boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.decode("#f0eae4"));
        buttonPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Couleur de texte pour les boutons
        Color buttonTextColor = Color.decode("#5d4024");
        
        // Création des boutons
        String[] buttonLabels = {"Equipe", "Joueur", "Entraineur", "Match"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(150, 40));
            button.setBackground(Color.decode("#f0eae4"));
            button.setForeground(buttonTextColor);
            
            // Définir la police en Times New Roman
            button.setFont(new Font("Times New Roman", Font.BOLD, 14));
            
            // Ajouter une bordure de la couleur du texte
            button.setBorder(new LineBorder(buttonTextColor, 1, true));
            
            // Désactiver la mise en relief du focus
            button.setFocusPainted(false);
            
            // Ajouter un espace entre les boutons
            buttonPanel.add(button);
            buttonPanel.add(Box.createVerticalStrut(10));
            
            // Ajout des listeners pour changer le contenu
            button.addActionListener(e -> {
                switch (label) {
                    case "Equipe":
                        setContent(mainPanel, new EquipePanel());
                        break;
                    case "Joueur":
                        setContent(mainPanel, new JoueurPanel());
                        break;
                    case "Entraineur":
                        setContent(mainPanel, new EntraineurPanel());
                        break;
                    case "Match":
                        setContent(mainPanel, new MatchPanel());
                        break;
                }
            });
        }
        
        // Ajouter les composants au panel central
        centerPanel.add(Box.createHorizontalGlue()); // Espace extensible à gauche
        centerPanel.add(logoTitlePanel);
        centerPanel.add(Box.createHorizontalStrut(50)); // Espace entre logo et boutons
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createHorizontalGlue()); // Espace extensible à droite
        
        // Ajout du panel central à la fenêtre
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void setContent(JPanel mainPanel, JPanel newPanel) {
        // Conserve la structure existante
        mainPanel.removeAll();
        mainPanel.add(new JPanel(), BorderLayout.CENTER); // Placeholder
        mainPanel.add(newPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}