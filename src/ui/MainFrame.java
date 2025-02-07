package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

	public MainFrame() {
        setTitle("Gestion d'Équipes de Football");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création de la barre de menu
        JMenuBar menuBar = new JMenuBar();

        // Menus
        JMenu menuJoueur = new JMenu("Joueurs");
        JMenu menuEquipe = new JMenu("Équipes");
        JMenu menuEntraineur = new JMenu("Entraîneurs");
        JMenu menuMatch = new JMenu("Matchs");

        // Items des menus
        JMenuItem itemJoueur = new JMenuItem("Gérer les Joueurs");
        JMenuItem itemEquipe = new JMenuItem("Gérer les Équipes");
        JMenuItem itemEntraineur = new JMenuItem("Gérer les Entraîneurs");
        JMenuItem itemMatch = new JMenuItem("Gérer les Matchs");

        // Ajout des items aux menus
        menuJoueur.add(itemJoueur);
        menuEquipe.add(itemEquipe);
        menuEntraineur.add(itemEntraineur);
        menuMatch.add(itemMatch);

        // Ajout des menus à la barre de menu
        menuBar.add(menuJoueur);
        menuBar.add(menuEquipe);
        menuBar.add(menuEntraineur);
        menuBar.add(menuMatch);

        // Ajout de la barre de menu à la fenêtre
        setJMenuBar(menuBar);

        // Panel principal (va changer selon la section choisie)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // Ajout des actions pour changer le contenu de la fenêtre
        itemJoueur.addActionListener(e -> setContent(mainPanel, new JoueurPanel()));
        itemEquipe.addActionListener(e -> setContent(mainPanel, new EquipePanel()));
        itemEntraineur.addActionListener(e -> setContent(mainPanel, new EntraineurPanel()));
        itemMatch.addActionListener(e -> setContent(mainPanel, new MatchPanel()));
    }

    // Méthode pour changer le contenu de la fenêtre
    private void setContent(JPanel mainPanel, JPanel newPanel) {
        mainPanel.removeAll();
        mainPanel.add(newPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Méthode pour lancer l'application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
