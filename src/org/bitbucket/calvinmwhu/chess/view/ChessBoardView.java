package org.bitbucket.calvinmwhu.chess.view;

import org.bitbucket.calvinmwhu.chess.controller.ChessBoardController;
import org.bitbucket.calvinmwhu.chess.game.Game;
import org.bitbucket.calvinmwhu.chess.pieces.Piece;
import org.bitbucket.calvinmwhu.chess.values.PieceName;
import org.bitbucket.calvinmwhu.chess.values.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

/**
 * Created by calvinmwhu on 2/20/15.
 */
public class ChessBoardView extends JPanel {
    private final int height;
    private final int width;
    private TextField[] scores;
    private JPanel centerPanel;
    private JPanel southPanel;
    private JPanel northPanel;
    private JPanel westPanel;
    private JPanel eastPanel;
    private HashMap<String, ImageIcon> pieceImages = new HashMap<String, ImageIcon>();

    private JButton start = new JButton("Start");
    private JButton restart = new JButton("Restart");
    private JButton forfeit = new JButton("Forfeit");
    private JButton undo = new JButton("Undo");
    private JButton redo = new JButton("Redo");
    private JButton Customized = new JButton("Customized");

    private Image whiteTile;
    private Image blackTile;

    private JLabel[][] centerLabels;
    private ImagePanel[][] imagePanels;

    public ChessBoardView(ChessBoardController controller, int height, int width) throws Exception {
        this.height = height;
        this.width = width;
        this.scores = new TextField[2];
        centerPanel = new JPanel();
        southPanel = new JPanel();
        northPanel = new JPanel();
        westPanel = new JPanel();
        eastPanel = new JPanel();
        centerLabels = new JLabel[this.height][this.width];
        imagePanels = new ImagePanel[this.height][this.width];
        loadPieceImages();
        setUpChessBoardUI(controller);
    }

    private void loadPieceImages() throws Exception {
        for (Player player : Player.values()) {
            if (player != Player.UNOCCUPIED) {
                String color = player.getColor();
                for (PieceName pieceName : PieceName.values()) {
                    String name = color + pieceName.getName();
                    String filename = name + ".png";
                    pieceImages.put(name, new ImageIcon(this.getClass().getResource(filename)));
                }
            }
        }
        whiteTile = ImageIO.read(this.getClass().getResource("whiteTile.jpg"));
        blackTile = ImageIO.read(this.getClass().getResource("blackTile.jpeg"));
    }

    private void setUpChessBoardUI(ChessBoardController controller) {
        //the commented out code is for setting up without using a java applet
//        setTitle("CS242 Chess Game");
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLayout(new BorderLayout());
//        setSize(800, 800);
//        setLocationRelativeTo(null);
//        constructContentPane(getContentPane());
//        setVisible(true);
        constructContentPane(controller.getContentPane());
    }

    private void constructContentPane(Container contentPane) {

        constructCenterPanel();
        constructSouthPanel();
        constructWestPanel();
        constructNorthPanel();
        constructEastPanel();

        contentPane.add(centerPanel, BorderLayout.CENTER);
        contentPane.add(southPanel, BorderLayout.SOUTH);
        contentPane.add(westPanel, BorderLayout.WEST);
        contentPane.add(northPanel, BorderLayout.NORTH);
        contentPane.add(eastPanel, BorderLayout.EAST);
    }

    private void constructNorthPanel() {
        GridLayout gridLayout = new GridLayout(0, 2);
        northPanel.setLayout(gridLayout);
        JPanel[] panels = new JPanel[2];
        for (int i = 0; i < panels.length; i++) {
            panels[i] = new JPanel(new FlowLayout());
            JLabel name = new JLabel("Player_" + i + "'s score");
            scores[i] = new TextField("");
            panels[i].add(name);
            panels[i].add(scores[i]);
            northPanel.add(panels[i]);
        }
    }

    private void constructEastPanel() {
        eastPanel.setLayout(new GridLayout(5, 0));
        eastPanel.add(start);
        eastPanel.add(restart);
        eastPanel.add(forfeit);
        eastPanel.add(undo);
        eastPanel.add(redo);
    }

    private void constructWestPanel() {
        GridLayout gridLayout = new GridLayout(this.height, 0);

        westPanel.setLayout(gridLayout);
        JLabel[] labels = new JLabel[8];
        for (int i = 0; i < this.height; i++) {
            labels[i] = new JLabel(String.valueOf(this.height - i));
            labels[i].setVerticalAlignment(JLabel.CENTER);
            westPanel.add(labels[i]);
        }
    }


    private void constructSouthPanel() {
        GridLayout gridLayout = new GridLayout(0, this.width);
        southPanel.setLayout(gridLayout);

        JLabel[] labels = new JLabel[8];
        String[] values = {"A", "B", "C", "D", "E", "F", "G", "H"};
        for (int i = 0; i < this.width; i++) {
            labels[i] = new JLabel(values[i]);
            labels[i].setHorizontalAlignment(JLabel.CENTER);
            southPanel.add(labels[i]);
        }
    }

    private void constructCenterPanel() {
        GridLayout gridLayout = new GridLayout(height, width);
        centerPanel.setLayout(gridLayout);

        String[] files = {"A", "B", "C", "D", "E", "F", "G", "H"};
        int[] ranks = {1, 2, 3, 4, 5, 6, 7, 8};

        for (int rank = ranks.length - 1; rank >= 0; rank--) {
            for (int file = 0; file < files.length; file++) {
                centerLabels[rank][file] = new JLabel();
                imagePanels[rank][file] = (rank + file) % 2 == 1 ? new ImagePanel(whiteTile, centerLabels[rank][file], rank, file) : new ImagePanel(blackTile, centerLabels[rank][file], rank, file);
                imagePanels[rank][file].setName(files[file] + ranks[rank]);
                centerLabels[rank][file].setName(imagePanels[rank][file].getName());
                imagePanels[rank][file].add(centerLabels[rank][file]);

                centerPanel.add(imagePanels[rank][file]);
            }
        }
    }

    public void refreshBoard(Game game) {
        for (int rank = 0; rank < height; rank++) {
            for (int file = 0; file < width; file++) {
                centerLabels[rank][file].setIcon(null);
            }
        }
        updatePiecesConfiguration(game);
    }

    public void updatePiecesConfiguration(Game game) {
        for (Piece piece : game.getPlayers(Player.WHITE).values()) {
            addPieceToBoard(piece);
        }
        for (Piece piece : game.getPlayers(Player.BLACK).values()) {
            addPieceToBoard(piece);
        }
    }

    private void addPieceToBoard(Piece piece) {
        if (piece.removedFromBoard()) return;
        int rank = piece.getRank();
        int file = piece.getFile();
        String color = piece.getPlayer().getColor();
        String name = piece.getName().getName();
        centerLabels[rank][file].setIcon(pieceImages.get(color + name));
    }

    public void markPosition() {
        for (int rank = 0; rank < height; rank++) {
            for (int file = 0; file < width; file++) {
                centerLabels[rank][file].setText("(" + String.valueOf(rank) + "," + String.valueOf(file) + ")");
            }
        }

    }

    public int getDimensionHeight() {
        return height;
    }

    public int getDimensionWidth() {
        return width;
    }

    public ImagePanel getImagePanelAtPosition(int rank, int file) {
        return imagePanels[rank][file];
    }


    public void addStartListener(ActionListener a) {
        try {
            start.addActionListener(a);
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addRestartListener(ActionListener a) {
        try {
            restart.addActionListener(a);
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addForfeitListener(ActionListener a) {
        try {
            forfeit.addActionListener(a);
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addUndoListener(ActionListener a) {
        try {
            undo.addActionListener(a);
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addRedoListener(ActionListener a) {
        try {
            redo.addActionListener(a);
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addMouthActionListener(int rank, int file, MouseAdapter m) {
        try {
            imagePanels[rank][file].addMouseListener(m);
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            System.err.println(e.getMessage());
        }
    }


}
