import java.util.ArrayList;
import java.util.Random;

public class Port extends Entity{
    public static int numPorts;
    private int id;

    private double gold;
    private ArrayList<TradeItems> cloth;
    private double clothInfCost;
    private ArrayList<TradeItems> spices;
    private double spiceInfCost;


    public Port(int i,int j){
        super(i,j);
        numPorts++;
        id=numPorts;
        cloth=new ArrayList<TradeItems>();
        spices=new ArrayList<TradeItems>();
        Random rand=new Random();
        gold=50;
        int numItems1=rand.nextInt(10);
        for(int item=0;item<numItems1;item++)
            spices.add(new Spice());
        numItems1=rand.nextInt(10);
        for(int item=0;item<numItems1;item++)
            cloth.add(new Cloth());
    }

    public int distanceTo(Ship ship){
        int disX=Math.abs(getI()-ship.getI());
        int disY=Math.abs(getJ()-ship.getJ());
        if(disX>disY)
            return 14*disY + 10*(disX-disY);
        return 14*disX+ 10*(disY-disX);
    }

    public int getId(){return id;}

    public double getGold(){
        return gold;
    }
    public int getAmountOfTradeItems(){return cloth.size()+spices.size();}
    public ArrayList<TradeItems> getSpices(){return spices;}
    public ArrayList<TradeItems> getCloth(){return cloth;}
    public void setGold(double gold){this.gold=gold;}

    public void calcInflationCosts(){
        clothInfCost=cloth.size()*.1;
        spiceInfCost=spices.size()*.1;
    }

    public double getClothInfCost() {
        return clothInfCost;
    }

    public double getSpiceInfCost() {
        return spiceInfCost;
    }
}
