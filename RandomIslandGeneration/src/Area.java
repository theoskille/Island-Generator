import java.awt.*;
import java.util.Random;

public class Area extends Grid{
    private int numIslands;

    public Area(int localGridSize, int i,int j){
        super(i,j,localGridSize);

        Random rand=new Random();
        numIslands=rand.nextInt(2);
        for(int is=0;is<numIslands;is++){
            generateIsland();
        }


    }
    public void generateIsland(){
        Random rand=new Random();
        int islandSize=rand.nextInt(size()/2)+size()/4;
        int buffer=size()/10;
        int i=rand.nextInt(size()-buffer*2-islandSize)+buffer;
        int j=rand.nextInt(size()-buffer*2-islandSize)+buffer;
        Grid islandGrid=new Grid(i,j,islandSize);
        generateLand(islandGrid);
        generatePort(islandGrid);

        transpose(islandGrid);
    }
    public void generateLand(Grid grid){
        double[][] noiseGrid=PerlinNoise2D.getNoiseGrid(grid.size());
        maskNoise(noiseGrid);
        for(int i=0;i<grid.size();i++){
            for(int j=0;j<grid.size();j++){
                if(noiseGrid[i][j]>0.25)
                    grid.set(i,j,7);
                else if(noiseGrid[i][j]>0.15)
                    grid.set(i,j,5);
                else if(noiseGrid[i][j]>0.1)
                    grid.set(i,j,1);
                else if(noiseGrid[i][j]>0.075)
                    grid.set(i,j,6);
            }
        }
    }
    public void generatePort(Grid grid){
        for(int p=0;p<grid.size();p++){
            for(int q=0;q<grid.size();q++){
                if(grid.get(p,q)==1){
                    for(int m=-1;m<=1;m++){
                        for(int n=-1;n<=1;n++){
                            if(p+m>=0 && p+m<grid.size() && q+n>=0 && q+n<grid.size() && grid.get(p+m,q+n)==0){
                                grid.set(p,q,3);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    /*
    public void generateTrees(Grid grid) {
        Random rand = new Random();
        int p = rand.nextInt(grid.size());
        int q = rand.nextInt(grid.size());
        int forestSize = rand.nextInt(grid.size() / 2)+1;
        Grid forestGrid = new Grid(p,q, forestSize);
        double[][] noiseGrid=PerlinNoise2D.getNoiseGrid(forestSize);
        maskNoise(noiseGrid);
        for(int i=0;i<forestGrid.size();i++){
            for(int j=0;j<forestGrid.size();j++){
                if(noiseGrid[i][j]>0)
                    forestGrid.set(i,j,5);
            }
        }
        grid.transpose(forestGrid);
    }
    */




    //utilities
    public void printArea(){
        for(int i=0;i<size();i++) {
            System.out.println();
            for (int j = 0; j < size(); j++) {
                System.out.print(get(i,j));
            }
        }
    }

    //public int[][] getLocalGrid(){return ;}

    public void maskNoise(double[][] noiseGrid){
        double bias=.2;
        for(int i=0;i<noiseGrid.length;i++){
            for(int j=0;j<noiseGrid.length;j++){
                double distance = Math.hypot((noiseGrid.length/2)-i, (noiseGrid.length/2)-j);
                if(distance>noiseGrid.length/2)
                    noiseGrid[i][j]-=bias;
            }
        }
    }

}
