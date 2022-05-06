public class MatrixFlip {
    public static void printFlippedMatrix(char[][] matrix) {
		// DO NOT change the skeleton code.
		// You can add codes anywhere you want.

        int nrow = matrix.length;
        int ncol = matrix[0].length;

        char[][] flipped = new char[nrow][ncol];

        for(int i=0; i < nrow; i++){
            for(int j=0; j < ncol; j++){
                flipped[i][j] = matrix[nrow-i-1][ncol-j-1];
            }
        }

        printMatrix(flipped);
    }

    public static void printMatrix(char[][] mat){
        int nrow = mat.length;
        int ncol = mat[0].length;

        for(int i=0; i < nrow; i++){
            for(int j=0; j < ncol; j++){
                System.out.print(mat[i][j]);
            }
            System.out.print("\n");
        }
    }
}
