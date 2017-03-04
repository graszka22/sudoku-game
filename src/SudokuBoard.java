import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

/*
 * Created by angela on 05.12.16.
 */
public class SudokuBoard implements VetoableChangeListener, PropertyChangeListener {

    private NumberField numberFields[][];
    private JTextField textFields[][];
    private int cols;
    private JFrame frame;

    public SudokuBoard(String boardFilename) {
        JSONTokener tokener = new JSONTokener(getClass().getResourceAsStream("/" + boardFilename));
        JSONObject o = new JSONObject(tokener);
        JSONArray initialNumbers = o.getJSONArray("initial_numbers");
        JSONArray areas = o.getJSONArray("areas");
        cols = o.getInt("cols");
        numberFields = new NumberField[cols][];
        for (int i = 0; i < cols; ++i) {
            numberFields[i] = new NumberField[cols];
            for (int j = 0; j < cols; ++j) {
                numberFields[i][j] = new NumberField(i, j);
                numberFields[i][j].setArea(areas.getJSONArray(i).getInt(j));
                try {
                    int num = initialNumbers.getJSONArray(i).getInt(j);
                    if (num != 0) {
                        numberFields[i][j].setNumber(num);
                        numberFields[i][j].setInitial(true);
                    }
                } catch (PropertyVetoException e) {
                }
                numberFields[i][j].addVetoableChangeListener(this);
                numberFields[i][j].addPropertyChangeListener(this);
            }
        }
    }

    public JPanel getPanel(JFrame frame) {
        this.frame = frame;
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(cols, cols));
        textFields = new JTextField[cols][];
        for (int i = 0; i < cols; ++i) {
            textFields[i] = new JTextField[cols];
            for (int j = 0; j < cols; ++j) {
                JTextField field = textFields[i][j] = new JTextField();
                field.setPreferredSize(new Dimension(500 / cols, 500 / cols));
                field.setMaximumSize(new Dimension(500 / cols, 500 / cols));
                field.setFont(new Font("Monospace", Font.BOLD, 300 / cols));
                if (!numberFields[i][j].isNumberSet())
                    field.setText("");
                else
                    field.setText("" + numberFields[i][j].getNumber());
                if (numberFields[i][j].isInitial()) {
                    field.setBackground(Color.LIGHT_GRAY);
                    field.setDisabledTextColor(Color.BLACK);
                    field.setEnabled(false);
                }
                final int x = i, y = j;
                field.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {

                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        handle(x, y);
                    }
                });
                field.addActionListener(e -> handle(x, y));
                panel.add(field);
            }
        }
        Border borderBottom = BorderFactory.createMatteBorder(1, 1, 5, 1, Color.BLACK);
        Border borderRight = BorderFactory.createMatteBorder(1, 1, 1, 5, Color.BLACK);
        Border borderBottomRight = BorderFactory.createMatteBorder(1, 1, 5, 5, Color.BLACK);
        Border borderNone = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK);
        for (int i = 0; i < cols; ++i) {
            for (int j = 0; j < cols; ++j) {
                int a1 = numberFields[i][j].getArea();
                int a2 = (j == cols - 1 ? 0 : numberFields[i][j + 1].getArea());
                int a3 = (i == cols - 1 ? 0 : numberFields[i + 1][j].getArea());
                if (i != cols - 1 && j != cols - 1 && a1 != a2 && a1 != a3)
                    textFields[i][j].setBorder(borderBottomRight);
                else if (j != cols - 1 && a1 != a2)
                    textFields[i][j].setBorder(borderRight);
                else if (i != cols - 1 && a1 != a3)
                    textFields[i][j].setBorder(borderBottom);
                else
                    textFields[i][j].setBorder(borderNone);
                textFields[i][j].setHorizontalAlignment(SwingConstants.CENTER);
            }
        }
        return panel;
    }

    private void handle(int x, int y) {
        JTextField field = textFields[x][y];
        if (field.getText().equals("")) {
            numberFields[x][y].emptyNumber();
        } else {
            try {
                numberFields[x][y].setNumber(Integer.parseInt(field.getText()));
            } catch (PropertyVetoException | NumberFormatException ex) {
                if (numberFields[x][y].isNumberSet())
                    field.setText(String.valueOf(numberFields[x][y].getNumber()));
                else
                    field.setText("");
            }
        }
    }

    private boolean checkCollisions(int x, int y, int v, boolean initial) {
        if (v == 0) return false;
        for (int i = 0; i < cols; ++i) {
            if (i == x)
                continue;
            if (numberFields[i][y].getNumber() == v && numberFields[i][y].isInitial() == initial)
                return true;
        }
        for (int i = 0; i < cols; ++i) {
            if (i == y)
                continue;
            if (numberFields[x][i].getNumber() == v && numberFields[x][i].isInitial() == initial)
                return true;
        }
        int area = numberFields[x][y].getArea();
        for (int i = 0; i < cols; ++i) {
            for (int j = 0; j < cols; ++j) {
                if (i == x && j == y) continue;
                if (numberFields[i][j].getArea() != area || numberFields[i][j].isInitial() != initial) continue;
                if (numberFields[i][j].getNumber() == v)
                    return true;
            }
        }
        return false;
    }

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        NumberField field = (NumberField) evt.getSource();
        int x = field.getX(), y = field.getY();
        int newval = (int) evt.getNewValue();
        if (newval < 1 || newval > cols || checkCollisions(x, y, newval, true))
            throw new PropertyVetoException("Value not allowed", evt);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean collision = false;
        for (int x = 0; x < cols; ++x) {
            for (int y = 0; y < cols; ++y) {
                if (numberFields[x][y].isInitial()) continue;
                if (checkCollisions(x, y, numberFields[x][y].getNumber(), false)) {
                    textFields[x][y].setBackground(Color.RED);
                    collision = true;
                } else {
                    textFields[x][y].setBackground(Color.WHITE);
                }
            }
        }
        if (collision) return;
        for (int x = 0; x < cols; ++x)
            for (int y = 0; y < cols; ++y)
                if (!numberFields[x][y].isNumberSet())
                    return;
        JOptionPane.showMessageDialog(frame, "Congratulations! You've won!");

    }

}
