package vrp_GA;

public class Node {
    String index;
    double x,y, demand;
    double consDemand = 0;

    public Node() {
    }

    public Node(String index, double x, double y, double demand) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.demand = demand;
    }
    
//    public Node(String index, double x, double y) {
//        this.index = index;
//        this.x = x;
//        this.y = y;
//        this.demand = consDemand;
//    }

//    public void setConsDemand(double consDemand) {
//    	this.consDemand = consDemand;
//    }
//    
//    public double getConsDemand() {
//    	return consDemand;
//    }
    
    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getDemand() {
        return demand;
    }

    public void setDemand(double demand) {
        this.demand = demand;
    }
    
}