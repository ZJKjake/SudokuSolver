import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Board {
    JFrame frame;

    public Board() {
        frame = new JFrame("Sudoku Solver 1.0");
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.pack();
        frame.setVisible(true);

    }
    public static void main(String[] args) {
    } 
}

