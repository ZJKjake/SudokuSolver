import java.lang.*;

public class Generate {
    int[] mat[];
    int numRow;
    int mid;
    int empty;

    Generate(int r, int e) {
        this.numRow = r;
        this.empty = e;

        Double rr = Math.sqrt(r);
        mid = rr.intValue();

        mat = new int[r][r];
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
                if (mat[rowStart+i][colStart+j]==num)
                    return false;

        return true;
    }

    void fillBox(int row,int col){
        int num=0;
        for (int i=0; i<mid; i++)
        {
            for (int j=0; j<mid; j++)
            {
                num = randomGenerator(numRow);
                while (!usedBox(row, col, num)){
                    num=randomGenerator(numRow);
                }
                mat[row +i][col+j]= num;
            }
        }
    }

    int randomGenerator(int num){
        return (int)Math.floor((Math.random()*num+1));
    }

    boolean isSafe(int i,int j,int num) {
        return (existRow(i, num) && existCol(j, num) && usedBox(i-i%mid, j-j%mid, num));
    }

    boolean existRow(int i,int num){
        for (int j = 0; j<numRow; j++) {
            if (mat[i][j] == num) {
                return false;
            }
        }
        return true;
    }

    boolean existCol(int j,int num) {
        for (int i = 0; i<numRow; i++)
            if (mat[i][j] == num)
                return false;
        return true;
    }

    public void removeExtra() {
        int count = empty;
        while (count != 0) {
            int rand = randomGenerator(numRow*numRow)-1;
            int i = (rand/numRow);
            int j = rand% 9;
            if (j != 0){
                j=j-1;
            }
            if (mat[i][j] != 0) {
                count--;
                mat[i][j]=0;
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
                mat[i][j] = num;
                if (fillRemain(i, j+1)) {
                    return true;
                }

                mat[i][j]=0;
            }
        }
        return false;
    }
}
