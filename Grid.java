import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Grid implements ActionListener{

    JFrame frame;
    JComponent[][] comps;
    JButton buttonFillBoard, buttonBruteForce, buttonBitMasks, buttonClear, buttonGenerate ;
    GridPanel panelGrid;
    JPanel panelInput;

    public static Board board=new Board();

    public static int slotX =10;
    public static int slotY =10;

    private int[][] ogGrid;
    private static int[][] grid=null;

    public Grid(){
        board.frame.setVisible(false);
        frame = new JFrame("Sudoku Solver");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panelInput = new JPanel();
//        buttonFillBoard=new JButton("Fill Board");
//        panelInput.add(buttonFillBoard);

        buttonGenerate=new JButton("Generate");
        panelInput.add(buttonGenerate);

        buttonBruteForce = new JButton("BruteSolve");
        panelInput.add(buttonBruteForce);

        buttonBitMasks=new JButton("BitMasks");
        panelInput.add(buttonBitMasks);
        buttonClear=new JButton("Clear");
        panelInput.add(buttonClear);


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

//        buttonFillBoard.addActionListener(new ActionListener(){
//            public void actionPerformed(ActionEvent e){
//                if(grid!=null) {
//                    fillBoard(grid);
//                }
//                else{
//                    JOptionPane.showMessageDialog(frame, "Please generate a new grid!");
//                }
//            }
//        });
        buttonGenerate.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                clearAllHighlight();
                int empty = -1;

                // Keep prompting the user until a valid number of empty cells is entered
                while (empty < 0 || empty > 81 ) {
                    String input = JOptionPane.showInputDialog(
                            null,
                            "Enter the number of empty cells (0-81):",
                            "Empty Cells",
                            JOptionPane.PLAIN_MESSAGE);

                    empty = Integer.parseInt(input);

                }

                // Generate the sudoku grid
                Generate sudoku = new Generate(9, empty);
                sudoku.fillValues();
                grid=sudoku.grid;
                if(grid!=null) {
                    fillBoard(grid);
                }
                else{
                    JOptionPane.showMessageDialog(frame, "Please generate a new grid!");
                }
            }
        });

        buttonClear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                int[][] g=new int[9][9];
                fillBoard(g);
            }
        });

        buttonBruteForce.addActionListener(e -> {
            long start = System.nanoTime();
            SwingWorker<Boolean, int[][]> worker = new SwingWorker<Boolean, int[][]>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    if (grid != null) {
                        int[][] solution = new int[9][9];
                        boolean solve = bruteSolve(grid);
                        if (solve) {
                            publish(solution);
                        }
                        long end   = System.nanoTime();
                        double total = end - start;


                        JOptionPane.showMessageDialog(frame, "The runtime is: " + total / 1000000000 + " seconds.");

                        return solve;
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please generate a new grid!");
                        return false;
                    }
                }

                @Override
                protected void done() {
                    try {
                        boolean solve = get();
                        if (!solve) {
                            //JOptionPane.showMessageDialog(frame, "No solution!");
                        }
                        buttonBruteForce.setEnabled(true);
                        buttonBitMasks.setEnabled(true);
                        buttonClear.setEnabled(true);
                        buttonGenerate.setEnabled(true);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };

            buttonBruteForce.setEnabled(false);
            buttonBitMasks.setEnabled(false);
            buttonClear.setEnabled(false);
            buttonGenerate.setEnabled(false);
            worker.execute();

        });



        buttonBitMasks.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long start= System.nanoTime();
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        boolean solve = bitMasks(grid, 0, 0);

                        long end = System.nanoTime();
                        double totalRuntime = end - start;

                        JOptionPane.showMessageDialog(frame, "The runtime is: " + totalRuntime/1000000000 + " seconds.");

                        return solve;

                    }

                    @Override
                    protected void done() {
                        try {
                            boolean solve = get();
                            if (!solve) {
                                //JOptionPane.showMessageDialog(frame, "No solution!");
                            }
                            buttonBruteForce.setEnabled(true);
                            buttonBitMasks.setEnabled(true);
                            buttonClear.setEnabled(true);
                            buttonGenerate.setEnabled(true);


                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                };

                worker.execute();
                buttonBruteForce.setEnabled(false);
                buttonBitMasks.setEnabled(false);
                buttonClear.setEnabled(false);
                buttonGenerate.setEnabled(false);

            }
        });
    }


    private void printGrid(int[][] x){ //Debug helper
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

    private boolean bruteSolve(int[][] grid) {

        int size = grid.length;

        // Check if the puzzle is already solved
        if (isDone(grid)) {
            return true;
        }

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col] == 0) {
                    for (int num = 1; num <= size; num++) {
                        if (isValid(grid, row, col, num)) {
                            grid[row][col] = num;
                            highlight((JButton) comps[row][col]);
                            ((JButton) comps[row][col]).setText("" + grid[row][col]);
                            try {
                                Thread.sleep(500); //Delay 0.5 seconds
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (bruteSolve(grid)) {
                                return true;
                            }

                            grid[row][col] = 0;
                            ((JButton) comps[row][col]).setText("");
                            clearHighlight((JButton) comps[row][col]);

                        }
                    }
                    return false;
                }
                clearHighlight((JButton) comps[row][col]);
            }

        }


        return false; // If it's not able to solve
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
                box[getBox(i, j)] |= 1 <<grid[i][j];
            }

        }
    }

    private int row[] = new int[9], col[] = new int[9], box[] = new int[9];
    private Boolean set=false;
    private boolean bitMasks(int[][] grid, int i, int j) {
        if (!set) {
            set = true;
            setInitialValues(grid);

        }
        if (i ==8 && j == 9) {
            return true;
        }
        if (j == 9) {
            j = 0;
            i++;
        }

        if (grid[i][j] > 0) {
            return bitMasks(grid, i, j + 1);
        }

        for (int k = 1; k < 10; k++) {
            if (isValid(grid, i, j, k)) {
                grid[i][j] = k;
                ((JButton) comps[i][j]).setText("" + grid[i][j]);
                highlight((JButton) comps[i][j]);

                try {
                    Thread.sleep(500); // Delay in milliseconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                row[i] = row[i] + (int) Math.pow(2, k);
                col[j] = col[j] + (int) Math.pow(2, k);
                clearHighlight((JButton) comps[i][j]);

                box[getBox(i, j)] = box[getBox(i, j)] + (int) Math.pow(2, k);

                if (bitMasks(grid, i, j + 1)) {
                    return true;
                }

                row[i] = row[i] - (int) Math.pow(2, k);
                col[j] = col[j] - (int) Math.pow(2, k);
                box[getBox(i, j)] = box[getBox(i, j)] - (int) Math.pow(2, k);

            }

            grid[i][j] = 0;
            ((JButton) comps[i][j]).setText("");
        }

        return false;
    }

        //Helper Method
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

    public static void main(String[] args){}
    public void highlight(JButton b){
        b.setBackground(Color.BLACK);
        b.setOpaque(true);
    }
    public void clearHighlight(JButton b){
        b.setBackground(Color.GRAY);
        b.setOpaque(false);
    }
    public void clearAllHighlight(){

        for(int row=0;row<9;row++){
            for(int col=0;col<9;col++){
                clearHighlight((JButton)comps[row][col]);
            }

        }
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

        //Visualization:
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
