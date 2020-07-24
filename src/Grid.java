public class Grid {
    private int i;
    private int j;
    private int gridSize;
    private int[][] grid;

    public Grid(){}
    public Grid(int i,int j){
        this.i=i;
        this.j=j;
    }
    public Grid(int i,int j,int gridSize){
        this.i=i;
        this.j=j;
        this.gridSize=gridSize;
        grid=new int[gridSize][gridSize];
    }
    public void set(int i,int j,int val){
        grid[i][j]=val;
    }
    public int get(int i,int j){return grid[i][j];}
    public int size(){return gridSize;}
    public int getI(){return i;}
    public int getJ(){return j;}
    public void transpose(Grid other){
        for(int p=0;p<other.size();p++){
            for(int q=0;q<other.size();q++){
                if(other.getI()+p<gridSize && other.getI()+p>=0 && other.getJ()+q<gridSize && other.getJ()+q>=0)
                    grid[other.getI()+p][other.getJ()+q]=other.get(p,q);
            }
        }
    }



}
