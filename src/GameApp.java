import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class GameApp extends GameLoop{
    //frame data
    JFrame frame;
    public static final int WIDTH=600;
    public static final int HEIGHT=600;
    //area grid data
    private Area[][] areaGrid;
    private int areaGridSize;
    private int areaSize;
    private int numAreas;
    private int numTiles;
    private int localGridSize;
    //global grid data
    private int[][] globalGrid;
    private int globalGridSize;
    private int tileSize;
    //node grid for entity movement
    Node[][] nodes;
    //entity data structures
    public static ArrayList<Ship> ships;
    public static ArrayList<Port> ports;
    //gameSpeed
    private int updateInterval;
    private int updateIntervalMax;
    private int tickCounter;

    public GameApp(){
        updateIntervalMax=20;
        updateInterval=10;
        tickCounter=0;
        createAndShowGui();
        scaleWorldData();
        createEntityDataStructures();
        generateWorld();
        meshGrids();
        createNodeGrid();

        generateShip();
        generateShip();
        generateShip();
        generatePorts();

        //printMesh();
        this.begin();

    }

    @Override
    public void update() {
        if(tickCounter>=updateInterval){
            for(Ship s:ships){
                if(s.checkPortArrival()){

                }
                if(!s.hasPath()) {
                    Port closest = s.findClosestPort();
                    s.addPath(s.pathTo(closest.getI(), closest.getJ(), nodes));
                    for(Node n:s.getPath()){
                        globalGrid[n.i][n.j]=4;
                    }
                }
                eraseFromGrid(s.getI(),s.getI());
                s.followPath();

            }
            updateWorld();
            tickCounter=0;
        }
        tickCounter++;
    }


    public void paint(Graphics g) {
        if(isGameRunning()) {
            //colors
            Color customBlue = new Color(38, 138, 236);
            Color customGreen = new Color(0, 184, 73);
            Color customGreen2=new Color(0, 99,0);
            //create background
            g.setColor(customBlue);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            for(int i=0;i<globalGrid.length;i++){
                for(int j=0;j<globalGrid[0].length;j++){
                    switch (globalGrid[i][j]){
                        case 1:
                            g.setColor(customGreen);
                            g.fillRect(tileSize*j,tileSize*i,tileSize,tileSize);
                            break;
                        case 4:
                            g.setColor(Color.YELLOW);
                            g.fillRect(tileSize*j,tileSize*i,tileSize,tileSize);
                            break;
                        case 5:
                            g.setColor(customGreen2);
                            g.fillRect(tileSize*j,tileSize*i,tileSize,tileSize);
                            break;
                    }
                }
            }
            for(Port p:ports){
                g.setColor(Color.MAGENTA);
                g.fillRect(tileSize*p.getJ(),tileSize*p.getI(),tileSize,tileSize);
            }
            for(Ship s:ships){
                g.setColor(Color.BLACK);
                g.fillRect(tileSize*s.getJ(),tileSize*s.getI(),tileSize,tileSize);
            }
        }
    }

    public void updateWorld(){
        for(Ship s:ships){
            globalGrid[s.getI()][s.getJ()]=2;
        }
    }

    public void createAndShowGui(){
        frame=new JFrame("Island Generation");
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(WIDTH,HEIGHT));

        JPanel buttonPanel=new JPanel();
        buttonPanel.setPreferredSize(new Dimension(WIDTH,HEIGHT/4));
        buttonPanel.setLayout(new GridLayout(0,4));
        buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        //buttonPanel.setBackground(Color.LIGHT_GRAY);
        TitledBorder simControlTitle = BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(),"Simulation Controls");
        simControlTitle.setTitleJustification(TitledBorder.CENTER);
        buttonPanel.setBorder(simControlTitle);


        JPanel gameSpeedButtonPanel=new JPanel();
        gameSpeedButtonPanel.setLayout(new GridLayout(4,2,10,10));
        gameSpeedButtonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        //gameSpeedButtonPanel.setBackground(Color.GRAY);
        TitledBorder speedControlTitle = BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(),"Speed Controls");
        speedControlTitle.setTitleJustification(TitledBorder.CENTER);
        gameSpeedButtonPanel.setBorder(speedControlTitle);
        buttonPanel.add(gameSpeedButtonPanel);

        JLabel speedLabel=new JLabel("Speed "+(updateIntervalMax-updateInterval));
        JButton speedInc=new JButton("Speed +");
        JButton speedDec=new JButton("Speed -");
        speedInc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(updateInterval>0) {
                    updateInterval--;
                    speedLabel.setText("Speed " + (updateIntervalMax-updateInterval));
                }
            }
        });
        speedDec.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(updateInterval<updateIntervalMax) {
                    updateInterval++;
                    speedLabel.setText("Speed " + (updateIntervalMax-updateInterval));
                }
            }
        });
        gameSpeedButtonPanel.add(speedInc);
        gameSpeedButtonPanel.add(speedDec);
        gameSpeedButtonPanel.add(speedLabel);

        frame.getContentPane().add(this,BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel,BorderLayout.SOUTH);
        frame.pack();
    }
    public void scaleWorldData(){
        numAreas=4;//in a row/column
        numTiles=50;
        areaSize=WIDTH/numAreas;
        areaGridSize=WIDTH/areaSize;
        areaGrid=new Area[areaGridSize][areaGridSize];
        tileSize=areaSize/numTiles;
        localGridSize=areaSize/tileSize;

    }
    public void createEntityDataStructures(){
        ships=new ArrayList<Ship>();
        ports=new ArrayList<Port>();
    }
    public void generateWorld(){
        for(int i = 0; i< areaGridSize; i++){
            for(int j = 0; j< areaGridSize; j++){
                areaGrid[i][j]=new Area(localGridSize, i, j);
            }
        }
    }
    public void meshGrids(){
        int localGridSize=areaGrid[0][0].size();
        globalGridSize=localGridSize*areaGridSize;
        globalGrid=new int[globalGridSize][globalGridSize];
        for(int i=0;i<areaGridSize;i++){
            for(int j=0;j<areaGridSize;j++){
                //loop through local grid
                for(int m=0;m<localGridSize;m++){
                    for(int n=0;n<localGridSize;n++){
                        globalGrid[m+i*localGridSize][n+j*localGridSize]=areaGrid[i][j].get(m,n);
                    }
                }
            }
        }
    }
    public void createNodeGrid(){
        nodes=new Node[globalGridSize][globalGridSize];
        for(int i=0;i<nodes.length;i++){
            for(int j=0;j<nodes[0].length;j++){
                nodes[i][j]=new Node(i,j,globalGrid[i][j]);
            }
        }
    }

    public void generateShip(){
        int x=0;
        int y=0;
        Random rand=new Random();
        do {
            x = rand.nextInt(globalGridSize);
            y = rand.nextInt(globalGridSize);
        }while(globalGrid[x][y]!=0);
        globalGrid[x][y]=2;
        ships.add(new Ship(x,y,globalGrid));
    }
    public void generatePorts(){
        for(int i=0;i<globalGridSize;i++){
            for(int j=0;j<globalGridSize;j++){
                if(globalGrid[i][j]==3){
                    GameApp.addPort(new Port(i,j));
                }
            }
        }
    }


    //utilities
    public void printMesh(){
        for(int i=0;i<globalGrid.length;i++) {
            System.out.println();
            for (int j = 0; j < globalGrid[0].length; j++) {
                System.out.print(globalGrid[i][j]);
            }
        }
    }
    public void eraseFromGrid(int i, int j){
        globalGrid[i][j]=0;
    }
    public void resetNodes(){
        for(int i=0;i<nodes.length;i++){
            for(int j=0;j<nodes[0].length;j++){
                nodes[i][j].reset();
            }
        }
    }

    public static void addPort(Port p){
        ports.add(p);
    }


    public static void main(String[] args){
        GameApp app=new GameApp();
    }
}
