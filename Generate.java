import java.lang.*;

public class Generate {
    int[] grid[]; 
    int numRow;
    int mid;
    int empty;

    Generate(int r, int e) {
        this.numRow = r;
        this.empty = e;

        Double rr = Math.sqrt(r);
        mid = rr.intValue();

        grid = new int[r][r];
    }

    public void fillValues() {
        fillDiagonal();
        fillRemain(0, mid);
        removeExtra();
    }

    void fillDiagonal() {
        for (int i = 0; i<numRow; i=i+mid){
            fillBox(i, i);
        }
    }

    boolean usedBox(int rowStart, int colStart, int num) {
        for (int i = 0; i<mid; i++)
            for (int j = 0; j<mid; j++)
                if (grid[rowStart+i][colStart+j]==num)
                    return false;

        return true;
    }

    void fillBox(int row,int col){ //Fills the 3*3 box with random numbers that satisfy the sudoku rules
        int num=0;
        for (int i=0; i<mid; i++)
        {
            for (int j=0; j<mid; j++)
            {
                num = randomGenerator(numRow);
                while (!usedBox(row, col, num)){
                    num=randomGenerator(numRow);
                }
                grid[row +i][col+j]= num;
            }
        }
    }

    int randomGenerator(int num){
        return (int)Math.floor((Math.random()*num+1));
    }

    boolean isSafe(int i,int j,int num) {
        return (existRow(i, num) && existCol(j, num) && usedBox(i-i%mid, j-j%mid, num));
    }

    boolean existRow(int i,int num){ //method checks if the number num already exists in the row i of the grid 
        for (int j = 0; j<numRow; j++) {
            if (grid[i][j] == num) {
                return false;
            }
        }
        return true;
    }

    boolean existCol(int j,int num) { //Similar as existRow
        for (int i = 0; i<numRow; i++)
            if (grid[i][j] == num)
                return false;
        return true;
    }

    public void removeExtra() { //removes extra numbers from the grid to create empty cells
        int count = empty;
        while (count != 0) {
            int rand = randomGenerator(numRow*numRow)-1;
            int i = (rand/numRow);
            int j = rand% 9;
            if (j != 0){
                j=j-1;
            }
            if (grid[i][j] != 0) {
                count--;
                grid[i][j]=0;
            }
        }
    }

    boolean fillRemain(int i, int j) {

        if (j>=numRow && i<numRow-1){
            i++;
            j=0;
        }
        if (i>=numRow && j>=numRow) {
            return true;
        }

        if (i < mid){
            if (j < mid)
                j = mid;
        }
        else if (i < numRow-mid){
            if (j==(int)(i/mid)*mid) {
                j = j + mid;
            }
        }
        else {
            if (j == numRow-mid) {
                i++;
                j=0;
                if (i>=numRow) {
                    return true;
                }
            }
        }

        for (int num = 1; num<numRow+1; num++){
            if (isSafe(i, j, num)){
                grid[i][j] = num;
                if (fillRemain(i, j+1)) {
                    return true;
                }

                grid[i][j]=0;
            }
        }
        return false;
    }
}
