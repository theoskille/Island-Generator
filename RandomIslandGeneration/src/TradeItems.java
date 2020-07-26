public class TradeItems {
    private double baseValue;
    private double pricePaid;
    public TradeItems(int baseValue){
        this.baseValue=baseValue;
    }

    public double getBaseValue() {
        return baseValue;
    }
    public void setPricePaid(double pricePaid){this.pricePaid=pricePaid;}
    public double getPricePaid(){return pricePaid;}
}
