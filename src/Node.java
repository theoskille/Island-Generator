import java.util.ArrayList;
import java.util.List;

public class Node {
    int i;
    int j;
    int objectVal;

    Node parent;
    int gCost;
    int hCost;

    public Node(int i, int j,int objectVal){
        this.i=i;
        this.j=j;
        this.objectVal=objectVal;
    }

    public int getFCost(){
        return gCost+hCost;
    }

    public ArrayList<Node> getNeighbours(Node[][] grid){
        ArrayList<Node> neighbours=new ArrayList<Node>();
        for(int m=-1;m<=1;m++){
            for(int n=-1;n<=1;n++){
                if(m==0 && n==0)
                    continue;

                if(i+m>=0 && i+m<grid.length && j+n>=0 && j+n<grid[0].length){
                    neighbours.add(grid[i+m][j+n]);
                }
            }
        }
        return neighbours;
    }

    public int getDistanceTo(Node node){
        int disX=Math.abs(i-node.i);
        int disY=Math.abs(j-node.j);
        if(disX>disY)
            return 14*disY + 10*(disX-disY);
        return 14*disX+ 10*(disY-disX);
    }

    public void reset(){
        parent=null;
        gCost=0;
        hCost=0;
    }
}
