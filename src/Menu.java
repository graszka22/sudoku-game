import javax.swing.*;
import java.awt.*;

/**
 * Created by angela on 04.01.17.
 */
public class Menu {
    private String boardFiles[] = {"board0.json", "board1.json", "board2.json", "board3.json"};
    private JComboBox<String> comboBox;
    private JFrame frame;
    private int currentBoard;

    public void createAndShowGui() {
        frame = new JFrame("Sudoku");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(200, 200));
        frame.getContentPane().add(createMenu());
        frame.pack();
        frame.setVisible(true);
    }

    private void fireBoard() {
        SudokuBoard board = new SudokuBoard(boardFiles[currentBoard]);
        JPanel main = new JPanel();
        JPanel panel = board.getPanel(frame);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(main);
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        JButton button = new JButton("← back to menu");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> backToMenu());
        JButton button1 = new JButton("clear");
        button1.setAlignmentX(Component.CENTER_ALIGNMENT);
        button1.addActionListener(e -> clearBoard());
        leftPanel.add(button);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(button1);
        main.add(panel);
        main.add(leftPanel);
        frame.pack();
    }

    private JPanel createMenu() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel("Select the board:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboBox = new JComboBox<>();
        for (String s : boardFiles)
            comboBox.addItem(s.substring(0, s.length() - 5));
        comboBox.setMaximumSize(new Dimension(300, comboBox.getPreferredSize().height));
        JButton okButton = new JButton("PLAY");
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.addActionListener(e -> {
            currentBoard = comboBox.getSelectedIndex();
            fireBoard();
        });
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(comboBox);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(okButton);
        return panel;
    }

    private void backToMenu() {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(createMenu());
        frame.pack();
    }

    private void clearBoard() {
        fireBoard();
    }
}