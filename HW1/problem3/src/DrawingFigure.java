public class DrawingFigure {
    public static void drawFigure(int n){
        String[] figures = new String[n+1];

        for(int i=0; i<=n; i++){
            figures[i] = " ".repeat(2*n - 2*i);

            for(int j=0; j < 2*i+1; j++){
                figures[i] += "*";
                if(j != 2*i){
                    figures[i] += " ";
                }
            }

            figures[i] += " ".repeat(2*n - 2*i);
        }

        for(int k=0; k <= n; k++){
            System.out.println(figures[k]);
        }
        for(int k=n-1; k >= 0; k--){
            System.out.println(figures[k]);
        }
    }
}