package vrp_twoEwithPnD;

import java.util.ArrayList;
import java.util.Random;

//TODO more changing rules  for example  reverse,  the following changing with, only the node and so on
public class Tabu_search {
	double TOLERANCE = 0.000001;
	double taburand = 0.3;
	int[][] tabuArcs;
	int TABU;
	Random globalRandom = new Random(1);
	int MAX_ITERATIONS = 200;
	
	double carcost=100,truckcost=1000;
	
	// a flag to show if this iter tabu works?
	boolean ifTabu = false;

	ArrayList<Node> nodeList = new ArrayList<>();
	int numCus = 0, numSat = 0;
	double carDem = 0.0, truckDem = 0.0;
	double dem = 0.0;
	int numberOfCars, numberOfTrucks;
	double[][] distanceMatrix;

	// initialize solution
	Solution s = new Solution();
	// array list of ALL routes
	ArrayList<Route> Croutes = s.crt;
	ArrayList<Route> Troutes = s.trt;

	public Tabu_search() {

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

	public void TS() {
		nearest_neighbor nn = new nearest_neighbor(carDem, truckDem, numCus, numSat, nodeList);
		nn.NN();
		numberOfTrucks = nn.numberOfTrucks;
		numberOfCars = nn.numberOfCars;
		s = nn.s;
		Croutes = s.crt;
		Troutes = s.trt;
		// here can print again to test if nearest-neighbor algorithms works
		// well.

		// START OF TABU SEARCH CODE///
		System.out.println(" ");
		System.out.println("* * * * * * * * * *");
		System.out.println("TABU Search Starts !");
		System.out.println("* * * * * * * * * *");
		System.out.println(" ");
		// Tabu Search

		// This is a 2-D array which will hold the iterator until which an is
		// tabu
		// The [i][j] element of this array is the arc beginning from the i-th
		// node of allNodes (node with id : i)
		// and ending at the j-th node of allNodes list (node with id : j)
		tabuArcs = new int[nodeList.size()][nodeList.size()];
		for (int i = 0; i < nodeList.size(); i++) {
			Node from = nodeList.get(i);
			for (int j = 0; j < nodeList.size(); j++) {
				Node to = nodeList.get(j);
				tabuArcs[from.ID][to.ID] = -1;
			}
		}

		// this is a counter for holding the tabu search iterator
		int tabuSearchIterator = 0;
		int bestsolutioniter = 0; // tag1

		// the TABU horizon: for how many iterations something declared tabu
		TABU = 18;

		// This will hold an object containing the best solution found through
		// the search process
		Solution bestSolution = cloneSolution(s);

		// Here we apply the best relocation move local search scheme
		// This is an object for holding the best relocation move that can be
		// applied to the candidate solution
		RelocationMove rm = new RelocationMove(); // in order to apply one
													// relocation move for all
													// routes - dont want to
													// lose previous if i change
													// vehicle

		// Initialize the relocation move rm
		rm.positionOfRelocated = -1;
		rm.positionToBeInserted = -1;
		rm.fromRoute = new Route();
		rm.toRoute = new Route();
		rm.moveCostFrom = Double.MAX_VALUE;
		rm.moveCostTo = Double.MAX_VALUE;
		rm.moveCost = Double.MAX_VALUE;
		// Until the termination condition is set to true repeat the following
		// block of code

		while (tabuSearchIterator < MAX_ITERATIONS) {
			// With this function we look for the best relocation move
			// the characteristics of this move will be stored in the object rm
			tabuSearchIterator = tabuSearchIterator + 1;

			ifTabu = false;
			findBestRelocationMove(rm, s, distanceMatrix, tabuSearchIterator, bestSolution, numberOfCars,
					numberOfTrucks);
			if (ifTabu) {
			applyRelocationMove(rm, s, distanceMatrix, tabuSearchIterator);
			// renew the demand of each sat
			for (int i = 0; i < numSat; i++)
				nodeList.get(i + 1).demand = 0;
			for (int i = 0; i < numberOfCars; i++) {
				Node dep = s.crt.get(i).nodes.get(0);
				for (Node node : s.crt.get(i).nodes)
					if (dep.ID != node.ID) {
						dep.demand += node.demand;
					}
			}

			if (s.cost < bestSolution.cost - TOLERANCE) {
				bestSolution = cloneSolution(s);
				bestsolutioniter = tabuSearchIterator;
			}
			System.out.println("Iteration: " + tabuSearchIterator + " Best Cost: " + bestSolution.cost
					+ " Current Cost: " + s.cost);
			}
			else {
				System.out.println("Iteration: " + tabuSearchIterator + " Best Cost: " + bestSolution.cost
						+ " Current Cost: " + s.cost + " No imporving or changing!!! this iter");
			}

		}
		System.out.println("=====");

		// Print the results of the VRP
		for (int j = 0; j < numberOfTrucks; j++)
			if (bestSolution.trt.get(j).cost > 0) {
				int vehicle = j + 1;
				System.out.print("Assignment to Truck " + vehicle + ": ");
				for (int k = 0; k < bestSolution.trt.get(j).nodes.size(); k++) {
					System.out.print(bestSolution.trt.get(j).nodes.get(k).ID + "  ");
				}
				System.out.println("");
				System.out.println(
						"Truck Route Cost: " + s.trt.get(j).cost + " - Truck Route Load: " + bestSolution.trt.get(j).load);
				System.out.println("");

			}
		for (int j = 0; j < numberOfCars; j++)
			if (bestSolution.crt.get(j).cost > 0) {
				int vehicle = j + 1;
				System.out.print("Assignment to Car " + vehicle + ": ");
				for (int k = 0; k < bestSolution.crt.get(j).nodes.size(); k++) {
					System.out.print(bestSolution.crt.get(j).nodes.get(k).ID + "  ");
				}
				System.out.println("");
				System.out.println("Car Route Cost: " + bestSolution.crt.get(j).cost + " - Car Route Load: " + bestSolution.crt.get(j).load);
				System.out.println("");

			}
		System.out.println("Best Total Cost: " + bestSolution.cost);
		System.out.println("Best Total Distance: " + bestSolution.distance);
		System.out.println("Total iterations performed: " + tabuSearchIterator);
		System.out.println("Best Solution found at itertion: " + bestsolutioniter);

	}

	private Solution cloneSolution(Solution sol) {
		Solution out = new Solution();

		out.cost = sol.cost;
		out.distance = sol.distance;
		out.crt = (ArrayList<Route>) sol.crt.clone();
		out.trt = (ArrayList<Route>) sol.trt.clone();
		return out;
	}

	private void findBestRelocationMove(RelocationMove rm, Solution s, double[][] distanceMatrix, int iter,
			Solution bestSol, int numberOfCars, int numberOfTrucks) {
		// Arc object (for the arc tabu strategy)
		Arc cr;
		// List of Arcs to be Created (for the arc tabu strategy)
		ArrayList<Arc> toBeCreated = new ArrayList<Arc>();

		// This is a variable that will hold the cost of the best relocation
		// move
		double bestMoveCost = Double.MAX_VALUE;

		// here we can change the depot(satellite) of car route if better
		for (int i = 0; i < numberOfCars; i++) {
			Route aRoute = s.crt.get(i);
			if (aRoute.nodes.size() <= 2)
				continue;
			int dep = aRoute.nodes.get(0).ID;
			int ans = dep;
			double org = distanceMatrix[dep][aRoute.nodes.get(1).ID]
					+ distanceMatrix[dep][aRoute.nodes.get(aRoute.nodes.size() - 2).ID];
			aRoute.cost = aRoute.cost - org;
			for (int j = 1; j <= numSat; j++)
				if (j != dep) {
					if (distanceMatrix[j][aRoute.nodes.get(1).ID]
							+ distanceMatrix[j][aRoute.nodes.get(aRoute.nodes.size() - 2).ID] < org) {
						ans = j;
						org = distanceMatrix[j][aRoute.nodes.get(1).ID]
								+ distanceMatrix[j][aRoute.nodes.get(aRoute.nodes.size() - 2).ID];
					}
				}
			if (ans != dep) {
				aRoute.nodes.remove(aRoute.nodes.size() - 1);
				aRoute.nodes.add(nodeList.get(ans));
				aRoute.nodes.remove(0);
				aRoute.nodes.add(0, nodeList.get(ans));
			}
			aRoute.cost = aRoute.cost + org;
		}

		// We will iterate through all available nodes to be relocated
		Route fromRoute = new Route(), toRoute = new Route();
		// Here are boolean parameters
		// Flag is true if the route is truck route, otherwise car route
		boolean fromFlag = false, toFlag = false;
		int fromDep = 0, toDep = 0;
		// the demand of satellite
		double[] satdem = new double[numSat + 1];
		for (int i = 1; i <= numSat; i++)
			satdem[i] = nodeList.get(i).demand;
		// from route for
		for (int from = 0; from < numberOfTrucks + numberOfCars; from++) {
			fromFlag = false;
			if (from < numberOfTrucks) {
				fromRoute = s.trt.get(from);
				fromFlag = true;
				fromDep = 0;
			} else {
				fromRoute = s.crt.get(from - numberOfTrucks);
				fromDep = fromRoute.nodes.get(0).ID;
			}
			// to route for
			for (int to = 0; to < numberOfTrucks + numberOfCars; to++) {
				toFlag = false;
				if (to < numberOfTrucks) {
					toRoute = s.trt.get(to);
					toFlag = true;
					toDep = 0;
				} else {
					toRoute = s.crt.get(to - numberOfTrucks);
					toDep = toRoute.nodes.get(0).ID;
				}
				for (int relFromIndex = 1; relFromIndex < fromRoute.nodes.size() - 1; relFromIndex++) {
					// Node A is the predecessor of B
					Node A = fromRoute.nodes.get(relFromIndex - 1);

					// Node B is the relocated node
					Node B = fromRoute.nodes.get(relFromIndex);
					// check if node B should not change to toRoute
					if ((!toFlag || !fromFlag) && B.ID <= numSat) {
						continue;
					}
					// the demand of sat change
					if (B.ID > numSat) {
						if (!fromFlag)
							satdem[fromDep] -= B.demand;
						if (!toFlag)
							satdem[toDep] += B.demand;
					}

					// Node C is the successor of B
					Node C = fromRoute.nodes.get(relFromIndex + 1);

					// We will iterate through all possible re-insertion
					// positions for B
					for (int afterToInd = 0; afterToInd < toRoute.nodes.size() - 1; afterToInd++) {
						// Why do we have to write this line?
						// This line has to do with the nature of the 1-0
						// relocation
						// If afterInd == relIndex -> this would mean the
						// solution remains unaffected
						// If afterInd == relIndex - 1 -> this would mean the
						// solution remains unaffected
						// this why ??? TODO to check
						// CHECKED and found that without relIndex-1 is WRONG
						if (afterToInd != relFromIndex && afterToInd != relFromIndex - 1) {
							// Node F the node after which B is going to be
							// reinserted
							Node F = toRoute.nodes.get(afterToInd);

							// Node G the successor of F
							Node G = toRoute.nodes.get(afterToInd + 1);

							// The arcs A-B, B-C, and F-G break
							double costRemovedFrom = distanceMatrix[A.ID][B.ID] + distanceMatrix[B.ID][C.ID];
							double costRemovedTo = distanceMatrix[F.ID][G.ID];
							// double costRemoved = costRemoved1 + costRemoved2;

							// The arcs A-C, F-B and B-G are created
							double costAddedFrom = distanceMatrix[A.ID][C.ID];
							double costAddedTo = distanceMatrix[F.ID][B.ID] + distanceMatrix[B.ID][G.ID];
							// double costAdded = costAdded1 + costAdded2;
							
							// fixed cost to check the fixed cost change
							double costAddedFixed=0,costRemovedFixed=0;
						    if (toRoute.nodes.size()==2) {
						    	if (toRoute.nodes.get(0).ID ==0) costAddedFixed = truckcost;
						    	else costAddedFixed = carcost;
						    }
						    if (fromRoute.nodes.size()==3 && fromRoute.ID != toRoute.ID) {
						    	if (toRoute.nodes.get(0).ID ==0) costRemovedFixed = truckcost;
						    	else costRemovedFixed = carcost;
						    }

							// This is the cost of the move, or in other words
							// the change that this move will cause if applied
							// to the current solution
							// double moveCost = costAdded - costRemoved;
							double moveCostFrom = costAddedFrom - costRemovedFrom;
							double moveCostTo = costAddedTo - costRemovedTo;

							// If this move is the best found so far
							double moveCost = moveCostFrom + moveCostTo + costAddedFixed - costRemovedFixed;

							// Here we generate the list of arcs to be created
							// by this move
							toBeCreated.clear();
							cr = new Arc(A.ID, C.ID);
							toBeCreated.add(cr);
							cr = new Arc(F.ID, B.ID);
							toBeCreated.add(cr);
							cr = new Arc(B.ID, G.ID);
							toBeCreated.add(cr);

							// CHECK IF THE MOVE IS TABU (select one of the two
							// developed tabu schemes)
							if (isTabu_Arcs(toBeCreated, moveCost, s, iter, bestSol) == false) {
								if ((moveCost < bestMoveCost) && (from == to || (toRoute.load
										+ fromRoute.nodes.get(relFromIndex).demand <= toRoute.capacity))) {
									// set the best cost equal to the cost of
									// this solution
									bestMoveCost = moveCost;
									ifTabu = true;

									// store its characteristics
									rm.positionOfRelocated = relFromIndex;
									rm.positionToBeInserted = afterToInd;
									rm.moveCostTo = moveCostTo;
									rm.moveCostFrom = moveCostFrom;
									rm.movefixedcost= costAddedFixed-costRemovedFixed;
									rm.fromRoute = fromRoute;
									rm.toRoute = toRoute;
									rm.moveCost = moveCost;
									if (from != to) {
										rm.newLoadFrom = fromRoute.load - fromRoute.nodes.get(relFromIndex).demand;
										rm.newLoadTo = toRoute.load + fromRoute.nodes.get(relFromIndex).demand;
									} else {
										rm.newLoadFrom = fromRoute.load;
										rm.newLoadTo = toRoute.load;
									}
								}
							}
						}
					}
					if (B.ID > numSat) {
						if (!fromFlag)
							satdem[fromDep] += B.demand;
						if (!toFlag)
							satdem[toDep] -= B.demand;
					}
				}
				// }
			}
		}
	}

	private boolean isTabu_Arcs(ArrayList<Arc> toBeCrt, double moveCost, Solution s, int iter, Solution bestSol) {
		// The aspiration criterion: if the move leads to the best solution ever
		// encountered this move is NOT tabu
		if (s.cost + moveCost < bestSol.cost - TOLERANCE) {
			return false;
		}

		// If any of the generated arcs is still considered TABU the move is
		// TABU
		for (int i = 0; i < toBeCrt.size(); i++) {
			Arc arc = toBeCrt.get(i);

			if (iter < tabuArcs[arc.n1][arc.n2] && new Random().nextDouble() > taburand) {
				return true;
			}
		}

		// If none of the generated arcs is TABU the move is not TABU
		return false;
	}

	// rm, s, distanceMatrix, tabuSearchIterator

	// This function applies the relocation move rm to solution s
	private void applyRelocationMove(RelocationMove rm, Solution s, double[][] distanceMatrix, int iter) {

		Node relocatedNode = rm.fromRoute.nodes.get(rm.positionOfRelocated);
		// relocatedNode.tabuIterator = iter + TABU;

		// Node A is the predecessor of B
		Node A = rm.fromRoute.nodes.get(rm.positionOfRelocated - 1);
		// Node B is the relocated node
		Node B = rm.fromRoute.nodes.get(rm.positionOfRelocated);
		// Node C is the successor of B
		Node C = rm.fromRoute.nodes.get(rm.positionOfRelocated + 1);

		// Node F the node after which B is going to be reinserted
		Node F = rm.toRoute.nodes.get(rm.positionToBeInserted);
		// Node G the successor of F
		Node G = rm.toRoute.nodes.get(rm.positionToBeInserted + 1);

		tabuArcs[A.ID][B.ID] = iter + globalRandom.nextInt(TABU);
		tabuArcs[B.ID][C.ID] = iter + globalRandom.nextInt(TABU);
		tabuArcs[F.ID][G.ID] = iter + globalRandom.nextInt(TABU);

		// This is the node to be relocated
		// Node relocatedNode =
		// s.rt.get(rm.fromRoute).nodes.get(rm.positionOfRelocated);

		// Take out the relocated node
		rm.fromRoute.nodes.remove(rm.positionOfRelocated);

		// Reinsert the relocated node into the appropriate position
		// Where??? -> after the node that WAS (!!!!) located in the
		// rm.positionToBeInserted of the route

		// Watch out!!!
		// If the relocated customer is reinserted backwards we have to
		// re-insert it in (rm.positionToBeInserted + 1)
		if (((rm.positionToBeInserted < rm.positionOfRelocated) && (rm.toRoute.ID == rm.fromRoute.ID))
				|| (rm.toRoute.ID != rm.fromRoute.ID)) {
			rm.toRoute.nodes.add(rm.positionToBeInserted + 1, relocatedNode);
		}
		//// else (if it is reinserted forward) we have to re-insert it in
		//// (rm.positionToBeInserted)
		else {
			rm.toRoute.nodes.add(rm.positionToBeInserted, relocatedNode);
		}

		// update the cost of the solution and the corresponding cost of the
		// route object in the solution
		s.cost = s.cost + rm.moveCost;
		s.distance = s.distance + rm.moveCostTo+rm.moveCostFrom;
		rm.toRoute.cost = rm.toRoute.cost + rm.moveCostTo;
		rm.fromRoute.cost = rm.fromRoute.cost + rm.moveCostFrom;
		if (rm.toRoute.ID != rm.fromRoute.ID) {
			rm.toRoute.load = rm.newLoadTo;
			rm.fromRoute.load = rm.newLoadFrom;
		} else {
			rm.toRoute.load = rm.newLoadTo;
		}
	}

}// end


class Arc {
	int n1;
	int n2;

	Arc() {
	}

	Arc(int a, int b) {
		n1 = a;
		n2 = b;
	}
}

class RelocationMove {
	int positionOfRelocated;
	int positionToBeInserted;
	Route fromRoute;
	Route toRoute;
	double moveCostTo;
	double moveCostFrom;
	double movefixedcost;
	double moveCost;
	double newLoadFrom;
	double newLoadTo;

	RelocationMove() {
	}
}