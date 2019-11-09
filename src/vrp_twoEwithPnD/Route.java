package vrp_twoEwithPnD;

import java.util.ArrayList;

public class Route {
	ArrayList<Node> nodes;
	double cost;
	int ID;
	double load;
	double capacity;

	// This is the Route constructor. It is executed every time a new Route
	// object is created (new Route)
	Route() {
		cost = 0;
		ID = -1;
		capacity = 30;
		load = 0;

		// A new array list of nodes is created
		nodes = new ArrayList<Node>();
	}

	public void setCap(double cap) {
		this.capacity = cap;
	}
}
