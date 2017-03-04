/**
 * Created by angela on 05.12.16.
 */

public class Sudoku {

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //SudokuBoard board = new SudokuBoard();
                Menu menu = new Menu();
                menu.createAndShowGui();
            }
        });
    }
}