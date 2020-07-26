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
    private Grid globalGrid;
    private int globalGridSize;
    private int tileSize;
    //node grid for entity movement
    Node[][] nodes;
    //entity data structures
    public static ArrayList<Ship> ships;
    public static ArrayList<Port> ports;
    public static ArrayList<Tree> trees;
    public static ArrayList<Mountain> mountains;
    public static ArrayList<Land> land;
    public static ArrayList<Sand> sand;
    //gameSpeed
    private int updateInterval;
    private int updateIntervalMax;
    private int tickCounter;
    private boolean toggleEntityInformation;
    public GameApp(){
        updateIntervalMax=20;
        updateInterval=10;
        tickCounter=0;
        toggleEntityInformation=true;
        createAndShowGui();
        scaleWorldData();
        createEntityDataStructures();
        generateWorld();
        meshGrids();
        createNodeGrid();

        generateShip();
        generateShip();
        generateShip();
        generateEntities();

        //printMesh();
        this.begin();

    }

    @Override
    public void update() {
        if(tickCounter>=updateInterval){
            for(Port p:ports){
                p.calcInflationCosts();
            }
            for(Ship s:ships){
                if(s.checkPortArrival()){
                    s.tradeWith(s.getCurrentPort());
                }
                if(!s.hasPath()) {
                    Port destination = s.randomPort();
                    s.addPath(s.pathTo(destination.getI(), destination.getJ(), nodes));
                    for(Node n:s.getPath()){
                        globalGrid.set(n.i,n.j,4);
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
        //g.drawImage(PerlinNoise2D.getNoiseImage(), 0,0,null); //show perlin noise
        if(isGameRunning()) {
            //colors
            Color customBlue = new Color(38, 138, 236);
            Color customGreen = new Color(0, 184, 73);
            Color customGreen2=new Color(0, 99,0);
            Color customSand=new Color(178, 162, 102);
            Color customMountain=new Color(81, 90, 83);
            //create background
            g.setColor(customBlue);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            for(int i=0;i<globalGrid.size();i++){
                for(int j=0;j<globalGrid.size();j++){
                    switch (globalGrid.get(i,j)){
                        case 4:
                            g.setColor(Color.YELLOW);
                            g.fillRect(tileSize*j,tileSize*i,tileSize,tileSize);
                            break;
                    }
                }
            }
            for(Port p:ports){
                g.setColor(Color.MAGENTA);
                g.fillRect(tileSize*p.getJ(),tileSize*p.getI(),tileSize,tileSize);
                g.setColor(Color.BLACK);
                if (toggleEntityInformation) {
                    g.drawString("Port Gold: " + Double.toString(p.getGold()), tileSize * p.getJ() - tileSize * 10, tileSize * p.getI() - tileSize * 5);
                    g.drawString("Port Trade Items: " + Double.toString(p.getAmountOfTradeItems()), tileSize * p.getJ() - tileSize * 10, tileSize * p.getI() - tileSize);
                }
            }
            for(Ship s:ships){
                g.setColor(Color.BLACK);
                g.fillRect(tileSize*s.getJ(),tileSize*s.getI(),tileSize,tileSize);
                g.setColor(Color.BLACK);
                if(toggleEntityInformation) {
                    g.drawString("Ship Gold: " + Double.toString(s.getGold()), tileSize * s.getJ() - tileSize * 10, tileSize * s.getI() - tileSize * 5);
                    g.drawString("Ship Trade Items: " + Double.toString(s.getAmountOfTradeItems()), tileSize * s.getJ() - tileSize * 10, tileSize * s.getI() - tileSize);
                }
            }
            for(Tree t:trees){
                g.setColor(customGreen2);
                g.fillRect(tileSize*t.getJ(),tileSize*t.getI(),tileSize,tileSize);
            }
            for(Mountain m:mountains){
                g.setColor(customMountain);
                g.fillRect(tileSize*m.getJ(),tileSize*m.getI(),tileSize,tileSize);
            }
            for(Land l:land){
                g.setColor(customGreen);
                g.fillRect(tileSize*l.getJ(),tileSize*l.getI(),tileSize,tileSize);
            }
            for(Sand s:sand){
                g.setColor(customSand);
                g.fillRect(tileSize*s.getJ(),tileSize*s.getI(),tileSize,tileSize);
            }
        }
    }

    public void updateWorld(){
        for(Ship s:ships){
            globalGrid.set(s.getI(),s.getJ(),2);
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

        JPanel entityInfoToggleButtonPanel=new JPanel();
        entityInfoToggleButtonPanel.setLayout(new GridLayout(4,2,10,10));
        entityInfoToggleButtonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        //gameSpeedButtonPanel.setBackground(Color.GRAY);
        TitledBorder toggleControlTitle = BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(),"Toggle Controls");
        toggleControlTitle.setTitleJustification(TitledBorder.CENTER);
        entityInfoToggleButtonPanel.setBorder(toggleControlTitle);
        buttonPanel.add(entityInfoToggleButtonPanel);

        JButton toggleEntityInfoButton=new JButton("Ship/Port info");
        toggleEntityInfoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleEntityInformation=!toggleEntityInformation;
            }
        });
        entityInfoToggleButtonPanel.add(toggleEntityInfoButton);

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
        trees=new ArrayList<Tree>();
        mountains=new ArrayList<Mountain>();
        land=new ArrayList<Land>();
        sand=new ArrayList<Sand>();
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
        globalGrid=new Grid(0,0,globalGridSize);
        for(int i=0;i<areaGridSize;i++){
            for(int j=0;j<areaGridSize;j++){
                //loop through local grid
                for(int m=0;m<localGridSize;m++){
                    for(int n=0;n<localGridSize;n++){
                        globalGrid.set(m+i*localGridSize,n+j*localGridSize,areaGrid[i][j].get(m,n));
                    }
                }
            }
        }
    }
    public void createNodeGrid(){
        nodes=new Node[globalGridSize][globalGridSize];
        for(int i=0;i<nodes.length;i++){
            for(int j=0;j<nodes[0].length;j++){
                nodes[i][j]=new Node(i,j,globalGrid.get(i,j));
            }
        }
    }

    public void generateShip(){
        int i=0;
        int j=0;
        Random rand=new Random();
        do {
            i = rand.nextInt(globalGridSize);
            j = rand.nextInt(globalGridSize);
        }while(globalGrid.get(i,j)!=0);
        globalGrid.set(i,j,2);
        ships.add(new Ship(i,j));
    }
    public void generateEntities(){
        for(int i=0;i<globalGridSize;i++){
            for(int j=0;j<globalGridSize;j++){
                if(globalGrid.get(i,j)==3){
                    GameApp.ports.add(new Port(i,j));
                }
                if(globalGrid.get(i,j)==5){
                    GameApp.trees.add(new Tree(i,j));
                }
                if(globalGrid.get(i,j)==7){
                    GameApp.mountains.add(new Mountain(i,j));
                }
                if(globalGrid.get(i,j)==1){
                    GameApp.land.add(new Land(i,j));
                }
                if(globalGrid.get(i,j)==6){
                    GameApp.sand.add(new Sand(i,j));
                }

            }
        }
    }


    //utilities
    public void printMesh(){
        for(int i=0;i<globalGrid.size();i++) {
            System.out.println();
            for (int j = 0; j < globalGrid.size(); j++) {
                System.out.print(globalGrid.getI());
            }
        }
    }
    public void eraseFromGrid(int i, int j){
        globalGrid.set(i,j,0);
    }
    public void resetNodes(){
        for(int i=0;i<nodes.length;i++){
            for(int j=0;j<nodes[0].length;j++){
                nodes[i][j].reset();
            }
        }
    }



    public static void main(String[] args){
        GameApp app=new GameApp();
    }
}
