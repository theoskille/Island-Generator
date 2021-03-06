import java.util.*;

public class Ship extends Entity {

    private boolean hasPath;
    private ArrayList<Node> path;
    private int pathPos;
    public static int numShips;
    private int id;

    private Port lastVisitedPort;

    private double gold;
    private ArrayList<TradeItems> cloth;
    private ArrayList<TradeItems> spices;

    public Ship(int i,int j){
        super(i,j);
        hasPath=false;
        numShips++;
        id=numShips;

        gold=20;
        cloth=new ArrayList<TradeItems>();
        spices=new ArrayList<TradeItems>();
    }

    //port methods
    public boolean checkPortArrival(){
        for(Port p:GameApp.ports){
            if(p.getI()==getI() && p.getJ()==getJ() && lastVisitedPort!=p){
                lastVisitedPort=p;
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
    public Port randomPort(){
        Random rand=new Random();
        int portNum=rand.nextInt(GameApp.ports.size());
        return GameApp.ports.get(portNum);
    }
    public Port findProfitablePort(){
        double bestProfitMargin=0;
        Port bestPort=null;
        for(Port p:GameApp.ports){
            if(spices.size()>0){
                double portPrice=p.getSpiceInfCost()+spices.get(0).getBaseValue();
                if(spices.get(0).getPricePaid()<portPrice)
                    if(portPrice-spices.get(0).getPricePaid()>bestProfitMargin) {
                        bestProfitMargin = portPrice - spices.get(0).getPricePaid();
                        bestPort=p;
                    }
            }
            if(cloth.size()>0){
                double portPrice=p.getClothInfCost()+cloth.get(0).getBaseValue();
                if(cloth.get(0).getPricePaid()<portPrice)
                    if(portPrice-cloth.get(0).getPricePaid()>bestProfitMargin) {
                        bestProfitMargin = portPrice - cloth.get(0).getPricePaid();
                        bestPort=p;
                    }
            }
        }
        return bestPort;
    }



    //path methods
    public void addPath(ArrayList<Node> path){
        this.path=path;
        hasPath=true;
        pathPos=0;
    }
    public void followPath(){
        if(hasPath && pathPos<path.size()) {
            setI(path.get(pathPos).i);
            setJ(path.get(pathPos).j);
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



    //path utilites
    public ArrayList<Node> pathTo(int k,int l,Node[][] grid){
        for(int p=0;p<grid.length;p++){
            for(int q=0;q<grid[0].length;q++){
                grid[p][q].reset();
            }
        }
        Node startPoint=grid[getI()][getJ()];
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
                if(closedSet.contains(node) || node.objectVal==1 || node.objectVal==5 || node.objectVal==6 || node.objectVal==7)
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


    public boolean hasPath(){return hasPath;}
    public ArrayList<Node> getPath(){return path;}

    public double getGold(){
        return gold;
    }
    public int getAmountOfTradeItems(){return cloth.size()+spices.size();}

    public Port getCurrentPort(){return lastVisitedPort;}

    public void tradeWith(Port other){
        if(other.getClothInfCost()<other.getSpiceInfCost() && other.getCloth().size()>1){
            //buy
            if(other.getCloth().size()>0) {
                TradeItems item = other.getCloth().remove(other.getCloth().size() - 1);
                cloth.add(item);
                gold -= item.getBaseValue() + other.getClothInfCost();
                other.setGold(other.getGold() + item.getBaseValue() + other.getClothInfCost());
            }
            //sell
            if(spices.size()>0) {
                TradeItems item = spices.remove(spices.size() - 1);
                other.getSpices().add(item);
                gold += item.getBaseValue() + other.getSpiceInfCost();
                other.setGold(other.getGold() - item.getBaseValue() + other.getSpiceInfCost());
            }
        }else{
            //buy
            if(other.getSpices().size()>0) {
                TradeItems item = other.getSpices().remove(other.getSpices().size() - 1);
                spices.add(item);
                gold -= item.getBaseValue() + other.getSpiceInfCost();
                other.setGold(other.getGold() + item.getBaseValue() + other.getSpiceInfCost());
            }
            //sell
            if(cloth.size()>0) {
                TradeItems item = cloth.remove(cloth.size() - 1);
                other.getCloth().add(item);
                gold += item.getBaseValue() + other.getClothInfCost();
                other.setGold(other.getGold() - item.getBaseValue() + other.getClothInfCost());
            }
        }


    }

}
