import java.awt.*;
import java.util.Random;

public class Area extends Grid{
    private int numIslands;

    public Area(int localGridSize, int i,int j){
        super(i,j,localGridSize);

        Random rand=new Random();
        numIslands=rand.nextInt(3);
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
        generateTrees(islandGrid);
        transpose(islandGrid);
    }
    public void generateLand(Grid grid){
        int islandGenerationLoops=400;
        Random rand=new Random();
        int buffer=grid.size()/10;
        int i=rand.nextInt(grid.size()-buffer*2)+buffer;
        int j=rand.nextInt(grid.size()-buffer*2)+buffer;
        int direction=rand.nextInt(4);
        grid.set(i,j,1);
        for(int n=0;n<islandGenerationLoops;n++){
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
            if(j<grid.size() && i<grid.size() && j>0 && i>0)
                grid.set(i,j,1);
            direction=rand.nextInt(4);
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
    public void generateTrees(Grid grid) {
        Random rand = new Random();
        int forestGenerationLoops = 100;
        for (int p = 0; p < grid.size(); p++) {
            for (int q = 0; q < grid.size(); q++) {
                if (grid.get(p, q) == 1) {
                    int forestSize = rand.nextInt(grid.size() / 2)+1;
                    Grid forestGrid = new Grid(p - forestSize / 2, q - forestSize / 2, forestSize);
                    int i = rand.nextInt(forestSize);
                    int j = rand.nextInt(forestSize);
                    int direction = rand.nextInt(4);
                    forestGrid.set(i, j, 5);
                    for (int n = 0; n < forestGenerationLoops; n++) {
                        switch (direction) {
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
                        if (j < forestGrid.size() && i < forestGrid.size() && j > 0 && i > 0)
                            forestGrid.set(i, j, 5);
                        direction = rand.nextInt(4);
                    }
                    grid.transpose(forestGrid);
                    return;
                }
            }
        }
    }


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

}
