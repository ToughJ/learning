package vrp_withJ;

public class Node {
	String index;
	int ID;
	double x, y, demand;
	boolean isRouted = false; // true/false flag indicating if a customer has
								// been inserted in the solution

	public Node() {
	}

	public Node(String index, double x, double y, double demand) {
		this.index = index;
		this.x = x;
		this.y = y;
		this.demand = demand;
		this.ID = Integer.parseInt(index);
	}

	public Node(String index, double x, double y, double demand, boolean isRouted) {
		this.index = index;
		this.x = x;
		this.y = y;
		this.demand = demand;
		this.isRouted = isRouted;
		this.ID = Integer.parseInt(index);
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

	public String getIndex() {
		return index;
	}

}