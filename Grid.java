import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

public class Grid implements ActionListener{

    JFrame frame;
    JComponent[][] comps;
    JButton buttonFillBoard, buttonBruteForce, buttonConstraintPropagation;
    GridPanel panelGrid;
    JPanel panelInput;

    public static Board board=new Board();

    public static int slotX =10;
    public static int slotY =10;

    public static int[][] grid = {
        {5, 3, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 0, 1, 9, 5, 0, 0, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},
        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},
        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {0, 0, 0, 0, 8, 0, 0, 7, 9}}; //I'll add more stuff here




    public Grid(){
        board.frame.setVisible(false);
        frame = new JFrame("Sudoku Solver");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panelInput = new JPanel();
        buttonFillBoard=new JButton("Fill Board");
        panelInput.add(buttonFillBoard);
        buttonBruteForce = new JButton("bruteSolve");
        panelInput.add(buttonBruteForce);
        buttonConstraintPropagation = new JButton("Constraint Propagation");
        panelInput.add(buttonConstraintPropagation);

        frame.add(panelInput, BorderLayout.NORTH);

        comps = new JComponent[9][9];

        panelGrid = new GridPanel();
        frame.add(panelGrid);

        frame.setSize(750, 850);

        frame.setVisible(true);

        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                int finalI = i;
                int finalJ = j;
                ((JButton) comps[i][j]).addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        highlight(((JButton)comps[finalI][finalJ]));
                        for(int i=0;i<9;i++){
                            for(int j=0;j<9;j++){
                                if(i!=finalI||j!=finalJ){
                                    clearHighlight((JButton) comps[i][j]);
                                }
                            }
                        }
                    }
                });
            }
        }
        buttonFillBoard.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

                fillBoard(grid);
            }
        });
        buttonBruteForce.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

                boolean solve=bruteSolve(grid);
                if(!solve){
                    System.out.println("No Solution");
                }
            }
        });
        buttonConstraintPropagation.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){


                //I'll add this method
            }
        });
    }
    private void printGrid(int[][] x){ //Bug helper
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                System.out.print(x[i][j]);
            }
            System.out.println();
        }
    }
    private boolean isDone(int[][] grid){ //CHeck if the graph is done
        for (int row = 0; row< grid.length; row++){
            for (int col = 0; col<grid.length; col++){
                if (grid[row][col]==0)
                    return false;
            }
        }
        return true;
    }
    private boolean bruteSolve(int[][] grid){
        int size=grid.length;

        // Check if the puzzle is already solved
        if (isDone(grid))
            return true;

        for (int row=0; row<size; row++){
            for (int col=0; col<size; col++){
                if (grid[row][col]==0){
                    for (int num=1; num<size+1; num++){
                        Timer timer = new Timer(500, new ActionListener(){
                            @Override
                            public void actionPerformed(ActionEvent e){
                                //What should I add?
                            }
                        });
                        timer.setRepeats(false); // Run the task only once
                        timer.start();
                        if (isValid(grid, row, col, num)){
                            grid[row][col]=num;

                            ((JButton) comps[row][col]).setText(""+grid[row][col]);

                            if (bruteSolve(grid))
                                return true;

                            grid[row][col]=0;
                            ((JButton) comps[row][col]).setText("");
                        }
                    }
                    return false;
                }
            }
        }

        return false;  //If it's not able to solve
    }






    private boolean isValid(int[][] grid, int row, int col, int num){

        int size=grid.length;
        for (int i=0; i<size; i++){
            if (grid[i][col]==num || grid[row][i]==num){
                return false;
            }
        }
        int boxRow=3*(row/3);
        int boxCol=3*(col/3);
        for (int i=0; i<3; i++){
            for (int j=0; j<3; j++){
                if (grid[boxRow+i][boxCol+j]==num){
                    return false;
                }
            }
        }

        return true;
    }
    public void fillBoard(int[][] grid){
        slotX=10;
        slotY=10;
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                if(grid[i][j]!=0){
                    ((JButton) comps[i][j]).setForeground(Color.BLACK);
                    ((JButton) comps[i][j]).setText(""+grid[i][j]);
                }
                else{
                    ((JButton) comps[i][j]).setForeground(Color.BLACK);
                    ((JButton) comps[i][j]).setText("");
                }
            }
        }
    }
    public static void main(String[] args){
    }
    public void highlight(JButton b){
        b.setBackground(Color.BLACK);
        b.setOpaque(true);
    }
    public void clearHighlight(JButton b){
        b.setBackground(Color.GRAY);
        b.setOpaque(false);
    }


    @Override
    public void actionPerformed(ActionEvent e){
    }

    class GridPanel extends JPanel{
        public GridPanel(){
            super();
            this.setLayout(new GridLayout(9,9));
            for (int i=0; i<9; i++){
                for (int j=0; j<9; j++){

                    comps[i][j]=new JButton("");


                    comps[i][j].setPreferredSize(new Dimension(50,50));
                    comps[i][j].setBackground(Color.RED);
                    this.add(comps[i][j]);
                    int finalI = i;
                    int finalJ = j;
                    ((JButton)comps[i][j]).addActionListener(new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent e){

                            slotX=finalI;
                            slotY=finalJ;

                        }
                    });

                }
            }
        }

        //Draw the lines in between the board
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            g.setColor(Color.BLACK);

            //Drawing the lines
            g.drawLine(250, 0, 250, 850);
            g.drawLine(499, 0, 499, 850);
            g.drawLine(0, 250, 1000, 250);
            g.drawLine(0, 500, 1000, 500);
        }
    }
}
