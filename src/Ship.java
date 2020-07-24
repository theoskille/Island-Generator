import java.util.*;

public class Ship extends Entity {
    private int i;
    private int j;
    private int[][] globalGrid;
    private boolean hasPath;
    private ArrayList<Node> path;
    private int pathPos;
    public static int numShips;
    private int id;

    private Port lastVisitedPort;


    public Ship(int i,int j,int[][] globalGrid){
        this.i=i;
        this.j=j;
        this.globalGrid=globalGrid;
        hasPath=false;
        numShips++;
        id=numShips;
    }

    //port methods
    public boolean checkPortArrival(){
        for(Port p:GameApp.ports){
            if(p.getI()==i && p.getJ()==j){
                lastVisitedPort=p;
                //System.out.println(p.id);
                return true;
            }
        }
        return false;
    }
    public Port findClosestPort(){//super sloppy method, magic number is totally arbitrary
        Port closest = GameApp.ports.get(0);
        int closDis=1000000000;
        for(Port p:GameApp.ports){
            int dis=p.distanceTo(this);
            if(dis<closDis && !p.equals(lastVisitedPort)) {
                closest = p;
                closDis=dis;
            }
        }
            //System.out.println("ship "+id+" last port visited "+lastVisitedPort.id+" closest to port "+closest.id);
        return closest;
    }

    //path methods
    public void addPath(ArrayList<Node> path){
        this.path=path;
        hasPath=true;
        pathPos=0;
    }
    public void followPath(){
        if(hasPath && pathPos<path.size()) {
            i = path.get(pathPos).i;
            j = path.get(pathPos).j;
            pathPos++;
        }else {
            resetPath();
        }
    }
    public void resetPath(){
        pathPos = 0;
        path = null;
        hasPath = false;
    }

    //move methods
    public void moveTo(int i,int j,Node[][] nodes){
        if(!hasPath) {
            path = pathTo(i, j, nodes);
            pathPos=0;
            hasPath=true;
        }
        if(pathPos<path.size()) {
            i = path.get(pathPos).i;
            j = path.get(pathPos).j;
            pathPos++;
            return;
        }
        path=null;
        hasPath=false;


    }
    public void randomMove(){
        Random rand=new Random();
        int direction=rand.nextInt(4);
        switch(direction){
            case 0://north
                if(j!=0 && globalGrid[i][j-1]!=1)
                    j--;
                break;
            case 1://east
                if(i!=globalGrid.length-1 && globalGrid[i+1][j]!=1)
                    i++;
                break;
            case 2://south
                if(j!=globalGrid.length-1 && globalGrid[i][j+1]!=1)
                    i++;
                break;
            case 3://west
                if(i!=0 && globalGrid[i-1][j]!=1)
                    i--;
                break;
            default:
                System.out.println("direction error");
        }

    }//for testing purposes


    //path utilites
    public ArrayList<Node> pathTo(int k,int l,Node[][] grid){
        for(int p=0;p<grid.length;p++){
            for(int q=0;q<grid[0].length;q++){
                grid[p][q].reset();
            }
        }
        Node startPoint=grid[i][j];
        Node endPoint=grid[k][l];
        List<Node> openSet=new ArrayList<Node>();
        HashSet<Node> closedSet=new HashSet<Node>();

        openSet.add(startPoint);
        while(openSet.size()>0){
            Node current=openSet.get(0);
            for(int n=1;n<openSet.size();n++){
                if(openSet.get(n).getFCost()<current.getFCost() || openSet.get(n).getFCost()==current.getFCost() && openSet.get(n).hCost<current.hCost){
                    current=openSet.get(n);
                }
            }
            openSet.remove(current);
            closedSet.add(current);

            if(current == endPoint) {
                return retracePath(startPoint, endPoint);
            }
            ArrayList<Node> neighbours=current.getNeighbours(grid);
            for(Node node: neighbours){
                if(closedSet.contains(node) || node.objectVal==1)
                    continue;

                int newMovementCostToNeighbour=current.gCost+current.getDistanceTo(node);
                if(newMovementCostToNeighbour < node.gCost || !openSet.contains(node)){
                    node.gCost=newMovementCostToNeighbour;
                    node.hCost=node.getDistanceTo(endPoint);
                    node.parent=current;

                    if(!openSet.contains(node))
                        openSet.add(node);
                }
            }

        }
        return new ArrayList<>();
    }
    public ArrayList<Node> retracePath(Node startPoint, Node endPoint){
        ArrayList<Node> path=new ArrayList<Node>();
        Node current=endPoint;
        while(current!=startPoint){
            path.add(current);
            current=current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    public int getI(){return i;}
    public int getJ(){return j;}
    public boolean hasPath(){return hasPath;}
    public ArrayList<Node> getPath(){return path;}



}
