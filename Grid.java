import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Grid implements ActionListener{

    JFrame frame;
    JComponent[][] comps;
    JButton buttonFillBoard, buttonBruteForce, buttonCrossHatch, buttonBitMasks, buttonClear, buttonGenerate ;
    GridPanel panelGrid;
    JPanel panelInput;

    public static Board board=new Board();

    public static int slotX =10;
    public static int slotY =10;

    private int[][] originalGrid;
    private static int[][] grid=null;




    public Grid(){
        board.frame.setVisible(false);
        frame = new JFrame("Sudoku Solver");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panelInput = new JPanel();
        buttonFillBoard=new JButton("Fill Board");
        panelInput.add(buttonFillBoard);
        buttonBruteForce = new JButton("BruteSolve");
        panelInput.add(buttonBruteForce);
        buttonCrossHatch = new JButton("CrossHatch");
        panelInput.add(buttonCrossHatch);
        buttonClear=new JButton("Clear");
        panelInput.add(buttonClear);
        buttonBitMasks=new JButton("BitMasks");
        panelInput.add(buttonBitMasks);
        buttonGenerate=new JButton("Generate");
        panelInput.add(buttonGenerate);

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
                if(grid!=null) {
                    fillBoard(grid);
                }
                else{
                    JOptionPane.showMessageDialog(frame, "Please generate a new grid!");
                }
            }
        });
        buttonGenerate.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                int N = 9, K = 20;
                Generate sudoku = new Generate(N, K);
                sudoku.fillValues();
                grid=sudoku.mat;
            }
        });

        buttonClear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                int[][] g=new int[9][9];
                fillBoard(g);
            }
        });
        buttonBruteForce.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                long start = System.nanoTime();

                boolean solve = bruteSolve(grid);
                if(!solve){
                    System.out.println("No Solution");
                }
                long end = System.nanoTime();

                double execution = end - start;
                System.out.println(execution/1000000000+" Seconds");
            }
        });

        buttonCrossHatch.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){


                //I'll add this method
                //Currently still testing
            }
        });
        buttonBitMasks.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

                long start = System.nanoTime();
                if(grid!=null) {
                    boolean solve=bitMasks(grid, 0, 0);
                    if (!solve){

                        System.out.println("No solution exists");

                    }
                }
                else{
                    JOptionPane.showMessageDialog(frame, "Please generate a new grid!");
                }

                long end = System.nanoTime();

                double execution = end - start;
                System.out.println(execution/1000000000+" Seconds");

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
    private boolean bruteSolve(int[][] grid)  {
        int size=grid.length;

        // Check if the puzzle is already solved
        if (isDone(grid))
            return true;

        for (int row=0; row<size; row++){
            for (int col=0; col<size; col++){
                if (grid[row][col]==0){
                    for (int num=1; num<size+1; num++){

                        if (isValid(grid, row, col, num)){
                            grid[row][col]=num;
                            ((JButton) comps[row][col]).setText(""+grid[row][col]);
//                            SwingUtilities.invokeLater(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        Thread.sleep(1000);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });

                            if (bruteSolve(grid)) {
                                return true;
                            }

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

    //The BitMasks Method

    //Helper Methods:
    private int getBox(int i, int j)
    {
        return i/3*3+j/3;
    }

    private void setInitialValues(int grid[][])
    {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                row[i] |= 1 << grid[i][j];
                col[j] |= 1 << grid[i][j];
                box[getBox(i, j)] |= 1 << grid[i][j];
            }
        }
    }
    private Boolean isSafe(int i, int j, int number)
    {
        return ((row[i] >> number) & 1) == 0
                && ((col[j] >> number) & 1) == 0
                && ((box[getBox(i, j)] >> number) & 1) == 0;
    }

    private int N = 9;
    private int row[] = new int[N], col[] = new int[N], box[] = new int[N];
    private Boolean set=false;
    private Boolean bitMasks(int grid[][], int i, int j)
    {
        if (!set) {
            set = true;
            setInitialValues(grid);
        }

        if (i==N-1 && j==N) {
            return true;
        }
        if (j==N) {
            j=0;
            i++;
        }

        if (grid[i][j] > 0)
            return bitMasks(grid, i, j + 1);

        for (int k = 1; k <= N; k++) {
            if (isSafe(i, j, k)) {

                grid[i][j] = k;

                ((JButton) comps[i][j]).setText(""+grid[i][j]);

                row[i] = row[i] + (int) Math.pow(2, k);
                col[j] = col[j] + (int) Math.pow(2, k);

                box[getBox(i, j)] = box[getBox(i, j)] + (int) Math.pow(2, k);

                if (bitMasks(grid, i, j + 1)) {
                    return true;
                }

                row[i] = row[i] - (int) Math.pow(2, k);
                col[j] = col[j] - (int) Math.pow(2, k);
                box[getBox(i, j)] = box[getBox(i, j)] - (int) Math.pow(2, k);
            }

            grid[i][j] = 0;
            ((JButton) comps[i][j]).setText(""+grid[i][j]);

        }

        return false;
    }




    private boolean human(int[][] grid) {




        return false;
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
