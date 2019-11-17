package vrp_twoEwithPnD;

import java.util.ArrayList;
import java.util.Random;

public class nearest_neighbor {
	ArrayList<Node> nodeList = new ArrayList<>();
	int numCus = 0, numSat = 0;
	double carDem = 0.0, truckDem = 0.0;
	double dem = 0.0;
	double carcost = 100, truckcost = 1000;
	int numberOfCars, numberOfTrucks;
	double[][] distanceMatrix;
	// initialize solution
	Solution s = new Solution();
	// array list of ALL routes
	ArrayList<Route> Croutes = s.crt;
	ArrayList<Route> Troutes = s.trt;

	public nearest_neighbor() {

	}

	public nearest_neighbor(double carDem, double truckDem, int numCus, int numSat, ArrayList<Node> arrNode) {
		this.carDem = carDem;
		this.truckDem = truckDem;
		this.numCus = numCus;
		this.numSat = numSat;
		setNodeList(arrNode);
	}

	public void setParameter(FileLoader fL) {
		this.carDem = fL.carDem;
		this.truckDem = fL.truckDem;
		this.numCus = fL.numCus;
		this.numSat = fL.numSat;
	}

	public void setNodeList(ArrayList<Node> arrNode) {
		this.nodeList.addAll(arrNode);
		for (Node a : nodeList) {
			dem += a.demand;
		}

		distanceMatrix = new double[nodeList.size()][nodeList.size()];
		for (int i = 0; i < nodeList.size(); i++) {
			Node from = nodeList.get(i);

			for (int j = 0; j < nodeList.size(); j++) {
				Node to = nodeList.get(j);

				double Delta_x = (from.x - to.x);
				double Delta_y = (from.y - to.y);
				double distance = Math.sqrt((Delta_x * Delta_x) + (Delta_y * Delta_y));

				distance = Math.round(distance);

				distanceMatrix[i][j] = distance;

			}
		}
	}

	public void NN() {
		// a temporary demand array for sat
		double[] satdem = new double[numSat + 1];
		// routes initialization - one for every vehicle
		// estimate a number of cars
		numberOfCars = (int) (dem / carDem * 1.5 + 1);
		numberOfTrucks = (int) (dem / truckDem * 1.5 + 1);
		for (int i = 1; i <= numberOfCars; i++) {
			Route route_nodes = new Route();
			route_nodes.ID = i + numberOfTrucks;
			route_nodes.setCap(carDem);
			Croutes.add(route_nodes);
		}
		for (int i = 1; i <= numberOfTrucks; i++) {
			Route route_nodes = new Route();
			route_nodes.ID = i;
			route_nodes.setCap(truckDem);
			Troutes.add(route_nodes);
		}

		// toRoute indicate how many customers are have been routed yet
		int toRoute = numCus;
		// customer assignment to vehicles
		for (int j = 1; j <= numberOfCars; j++) {
			ArrayList<Node> nodeSequence = Croutes.get(j - 1).nodes;
			double remaining = Croutes.get(j - 1).capacity;
			double load = Croutes.get(j - 1).load;
			int tmpSat = j % numSat;
			if (tmpSat == 0)
				tmpSat += numSat;
			nodeSequence.add(nodeList.get(tmpSat));
			boolean finalized = false;
			// If all customers are routed then add depot as the end point to
			// the vehicles that did not travel at all
			if (toRoute == 0) {
				finalized = true;
				nodeSequence.add(nodeList.get(tmpSat));
			}
			while (finalized == false) {
				// this will be the position of the nearest neighbor customer --
				// initialization to -1
				int positionOfTheNextOne = -1;
				// This will hold the minimal cost for moving to the next
				// customer - initialized to something very large
				double bestCostForTheNextOne = Double.MAX_VALUE;
				// This is the last customer of the route (or the depot if the
				// route is empty)
				Node lastInTheRoute = nodeSequence.get(nodeSequence.size() - 1);
				// identify nearest non-routed customer
				for (int k = numSat + 1; k < nodeList.size(); k++) {
					// The examined node is called candidate
					Node candidate = nodeList.get(k);
					// if this candidate has not been pushed in the solution
					if (candidate.isRouted == false) {
						// This is the cost for moving from the last to the
						// candidate one
						double trialCost = distanceMatrix[lastInTheRoute.ID][candidate.ID];
						// If this is the minimal cost found so far -> store
						// this cost and the position of this best candidate
						if (trialCost < bestCostForTheNextOne && candidate.demand <= remaining) {
							positionOfTheNextOne = k;
							bestCostForTheNextOne = trialCost;
						}
					}
				} // end of for

				if (positionOfTheNextOne != -1) {
					Node insertedNode = nodeList.get(positionOfTheNextOne);
					// Push him
					nodeSequence.add(insertedNode);
					satdem[tmpSat] += insertedNode.demand;
					nodeList.get(tmpSat).demand += insertedNode.demand;
					s.cost = s.cost + bestCostForTheNextOne;
					Croutes.get(j - 1).cost = Croutes.get(j - 1).cost + bestCostForTheNextOne;
					// routes.get(j-1).load = load ;
					insertedNode.isRouted = true;
					remaining = remaining - insertedNode.demand;
					load = load + insertedNode.demand;
					Croutes.get(j - 1).load = load;
					toRoute = toRoute - 1;
					// System.out.println(j + " / " + insertedNode.demand + " /
					// " + load);
				} else { // this adds the depot at the end of every route when
							// it is full and cannot take up more customers
					nodeSequence.add(nodeList.get(tmpSat));
					s.cost = s.cost + distanceMatrix[lastInTheRoute.ID][tmpSat];
					Croutes.get(j - 1).cost = Croutes.get(j - 1).cost + distanceMatrix[lastInTheRoute.ID][tmpSat];
					finalized = true;

				} // end of if
					// this adds the depot as the end point in case when
					// although the vehicle is not completely full yet, there
					// are no more customers to serve

			} // end of while loop
		} // end of for loop
			// arrange the truck route then
		int cos = 1; // count of sat
		for (int j = 1; j <= numberOfTrucks; j++) {
			ArrayList<Node> nodeSequence = Troutes.get(j - 1).nodes;
			double remaining = Troutes.get(j - 1).capacity;
			double load = Troutes.get(j - 1).load;
			nodeSequence.add(nodeList.get(0));
			// add a constraint here, that is only if the demand of the cos is
			// not 0, we'll arrange it
			// satdem[cos] <=remaining
			while (cos <= numSat && nodeList.get(cos).demand != 0 && nodeList.get(cos).demand <= remaining) {
				double cost = distanceMatrix[nodeSequence.get(nodeSequence.size() - 1).ID][cos];
				nodeSequence.add(nodeList.get(cos));
				s.cost += cost;
				remaining -= nodeList.get(cos).demand;
				load += nodeList.get(cos).demand;
				Troutes.get(j - 1).cost += cost;
				Troutes.get(j - 1).load = load;
//				Troutes.get(j - 1).capacity = remaining;
				cos++;
			}
			double cost = distanceMatrix[nodeSequence.get(nodeSequence.size() - 1).ID][0];
			nodeSequence.add(nodeList.get(0));
			s.cost += cost;
			Troutes.get(j - 1).cost += cost;
		}
		s.distance = s.cost;
		// the fixed cost of cars and trucks;
		for (int i = 0; i < numberOfCars; i++) {
			if (s.crt.get(i).nodes.size() > 2)
				s.cost += carcost;
		}
		for (int i = 0; i < numberOfTrucks; i++) {
			if (s.trt.get(i).nodes.size() > 2)
				s.cost += truckcost;
		}

		// Print the results of the VRP
		for (int j = 0; j < numberOfTrucks; j++)
			if (s.trt.get(j).cost > 0) {
				int vehicle = j + 1;
				System.out.print("Assignment to Truck " + vehicle + ": ");
				for (int k = 0; k < s.trt.get(j).nodes.size(); k++) {
					System.out.print(s.trt.get(j).nodes.get(k).ID + "  ");
				}
				System.out.println("");
				System.out.println(
						"Truck Route Cost: " + s.trt.get(j).cost + " - Truck Route Load: " + s.trt.get(j).load);
				System.out.println("");

			}
		for (int j = 0; j < numberOfCars; j++)
			if (s.crt.get(j).cost > 0) {
				int vehicle = j + 1;
				System.out.print("Assignment to Car " + vehicle + ": ");
				for (int k = 0; k < s.crt.get(j).nodes.size(); k++) {
					System.out.print(s.crt.get(j).nodes.get(k).ID + "  ");
				}
				System.out.println("");
				System.out.println("Car Route Cost: " + s.crt.get(j).cost + " - Car Route Load: " + s.crt.get(j).load);
				System.out.println("");

			}
		System.out.println("Total Distance: " + s.distance);
		System.out.println("Total Cost: " + s.cost);
	}
}
