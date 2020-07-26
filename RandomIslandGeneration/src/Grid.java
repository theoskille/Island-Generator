import java.util.Random;

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

    public void randomWalkSet(int i,int j,int setVal, int loops){
        Random rand=new Random();
        grid[i][j]=setVal;
        int direction=rand.nextInt(4);
        for(int n=0;n<loops;n++){
            switch(direction){
                case 0://north
                    j--;
                    break;
                case 1://east
                    i++;
                    break;
                case 2://south
                    j++;
                    break;
                case 3://west
                    i--;
                    break;
                default:
                    System.out.println("direction error");
            }
            if(j<gridSize && i<gridSize&& j>0 && i>0)
                grid[i][j]=setVal;
            direction=rand.nextInt(4);
        }

    }
    public int[] randomWalkFind(int val){
        int[] cood=new int[2];
        Random rand=new Random();
        int i=rand.nextInt(gridSize);
        int j=rand.nextInt(gridSize);
        int direction=rand.nextInt(4);
        while(grid[i][j]!=val){
            switch(direction){
                case 0://north
                    if(j>0)
                        j--;
                    break;
                case 1://east
                    if(i<=gridSize)
                        i++;
                    break;
                case 2://south
                    if(j<=gridSize)
                        j++;
                    break;
                case 3://west
                    if(i>0)
                        i--;
                    break;
                default:
                    System.out.println("direction error");
            }
            direction=rand.nextInt(4);
        }
        cood[0]=i;
        cood[1]=j;
        return cood;
    }



}
