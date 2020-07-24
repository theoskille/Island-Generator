public class Port extends Entity{
    private int i;
    private int j;
    public static int numPorts;
    private int id;
    public Port(int i,int j){
        this.i=i;
        this.j=j;
        numPorts++;
        id=numPorts;
    }

    public int distanceTo(Ship ship){
        int disX=Math.abs(i-ship.getI());
        int disY=Math.abs(j-ship.getJ());
        if(disX>disY)
            return 14*disY + 10*(disX-disY);
        return 14*disX+ 10*(disY-disX);
    }

    public int getI(){return i;}
    public int getJ(){return j;}
    public int getId(){return id;}
}
