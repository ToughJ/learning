package vrp_twoEwithPnD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class Chromosom {
	ArrayList<Node> nodeList;
	ArrayList<Node> arrNode;
//	ArrayList<Integer> truckEnd;
	double totalDistance, fitness;
	int carCount;
	int truckCount;
	double carDemand = 0.0, truckDemand = 0.0;
	int numCus = 0, numSat = 0;
	double[] capDem;

	public Chromosom(int numCus, int numSat, double carDem, double truckDem) {
		arrNode = new ArrayList<>();
		nodeList = new ArrayList<>();
//		truckEnd = new ArrayList<>();
		this.carDemand = carDem;
		this.truckDemand = truckDem;
		this.numCus = numCus;
		this.numSat = numSat;
		carCount = 0;
		truckCount = 0;
		totalDistance = 0;
		fitness = 0;
	}

	public Chromosom(int numCus, int numSat, double carDem, double truckDem, ArrayList<Node> nodeList) {
		arrNode = new ArrayList<>();
//		truckEnd = new ArrayList<>();
		this.nodeList = nodeList;
		this.carDemand = carDem;
		this.truckDemand = truckDem;
		this.numCus = numCus;
		this.numSat = numSat;
		carCount = 0;
		truckCount = 0;
		totalDistance = 0;
		fitness = 0;
	}

	public Chromosom(Chromosom chrome) {
		this.carDemand = chrome.carDemand;
		this.truckDemand = chrome.truckDemand;
		this.numCus = chrome.numCus;
		this.numSat = chrome.numSat;
		this.arrNode = chrome.arrNode;
		this.carCount = chrome.carCount;
//		this.truckEnd = chrome.truckEnd;
		this.truckCount = chrome.truckCount;
		this.totalDistance = chrome.totalDistance;
		this.fitness = chrome.fitness;
		this.nodeList = chrome.nodeList;
	}

	public ArrayList<Node> getArrNode() {
		return arrNode;
	}

	public void setNodeList(ArrayList<Node> arrNode) {
		this.nodeList.clear();
		this.nodeList.addAll(arrNode);
	}

	public void setArrNode(ArrayList<Node> arrNode) {
		this.arrNode.clear();
		this.arrNode.addAll(arrNode);
	}

	public double getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public void calFitness() {
		// this.fitness=1/(totalDistance+0.00001);
		this.fitness = totalDistance + carCount * 100 + 1000 * truckCount;
	}

	public double calDistNode(Node aNode, Node bNode) {
		double tempDistance = 0;
		tempDistance = Math.sqrt(Math.pow(aNode.getX() - bNode.getX(), 2) + Math.pow(aNode.getY() - bNode.getY(), 2));
		return tempDistance;
	}

	public void carCountInc() {
		carCount++;
	}

	public int getCarCountInc() {
		return carCount;
	}

	public void truckCountInc() {
		truckCount++;
	}

	public int getTruckCountInc() {
		return truckCount;
	}

	// to get where the SAT start in the chromosome
	public int getSatStart() {
		boolean[] checkSat = new boolean[numSat + 2];
		int i = 1;
		while (i < arrNode.size()) {
			int index = Integer.parseInt(arrNode.get(i).index);
			if (index <= numSat) {
				if (!checkSat[index])
					checkSat[index] = true;
				else {
					break;
				}
			}
			i++;
		}
		return i;
	}

	public void calTotalDistance() {
		Random rand = new Random();
		double tempDist = 0, tempTotalDist = 0;
		capDem = new double[numSat + 1];

		carCount = 0;
		truckCount = 0;
//		truckEnd.clear();
		double currentDemand = 0;
		int start = 0;
		int index = 0, satStart = getSatStart();
		ArrayList<Node> tempArrNode1 = new ArrayList<Node>(), tempArrNode2 = new ArrayList<Node>();

		// in order to know the demand of each satellite, we have to calculate
		// the car route first
		int from = satStart, to;
		if (from < arrNode.size() - 1) {
			start = Integer.parseInt(arrNode.get(from).index);
			carCountInc();

			while (from < arrNode.size()) {
				tempDist = 0;
				to = from + 1;
				if (to < arrNode.size())
					index = Integer.parseInt(arrNode.get(to).index);
				// if demand is 0, the node should be a satellite, this
				// procedure is to admmit the repetitive two satellite, which is
				// not possible
				while (to < arrNode.size() && arrNode.get(from).demand == 0 && arrNode.get(to).demand == 0) {
					to++;
					if (to < arrNode.size())
						index = Integer.parseInt(arrNode.get(to).index);
				}
				if (to >= arrNode.size()){
					tempDist += calDistNode(arrNode.get(from), nodeList.get(start));
					break;
				}
				// if it is still a sattellite, another route arrise. and start
				// from the "index"
				if (index <= numSat) {
					if (to == arrNode.size() - 1)
						break;
					tempDist += calDistNode(arrNode.get(from), nodeList.get(start));
					capDem[start] += currentDemand;
					start = index;
					from = to;
					currentDemand = 0;
					carCountInc();
				} else {
					// TODO this one should be change, cause bug arrise during
					// the route overcapacity
					if (currentDemand + arrNode.get(to).demand > this.carDemand
							|| currentDemand + arrNode.get(to).demand < -this.carDemand) {
						int newStart = rand.nextInt(numSat) + 1;
						tempArrNode2.add(nodeList.get(newStart));
						capDem[start] += currentDemand; // to rememeber the
														// demand of the
														// sattelite
						currentDemand = 0;
						tempDist += calDistNode(arrNode.get(from), nodeList.get(start))
								+ calDistNode(arrNode.get(to), nodeList.get(newStart));
						start = newStart;
						carCountInc();
					} else {
						tempDist += calDistNode(arrNode.get(from), arrNode.get(to));
					}
					currentDemand += arrNode.get(to).demand;
				}
				tempTotalDist = tempTotalDist + tempDist;
				tempArrNode2.add(arrNode.get(to));
				from = to;
			}
			tempDist = calDistNode(arrNode.get(from), nodeList.get(start));
			tempTotalDist = tempTotalDist + tempDist;
			totalDistance = tempTotalDist;
		}

		// a new arrNode to make the truck route at the beginning
		tempArrNode1.add(arrNode.get(0));
		start = Integer.parseInt(arrNode.get(0).index);
		to = 1;
		from = 0;
		truckCountInc();
		boolean[] tmpFlag = new boolean[numSat+1];
		while (from < satStart) {

			tempDist = 0;
			to = from + 1;
			if (to < satStart)
				index = Integer.parseInt(arrNode.get(to).index);
			// no consecutive two 0 in line
			while (to < satStart && Integer.parseInt(arrNode.get(from).index) == 0
					&& Integer.parseInt(arrNode.get(to).index) == 0) {
				to++;
				if (to < satStart)
					index = Integer.parseInt(arrNode.get(to).index);
			}
			
			// if the sat is no need, we no need to calculate it or remember it
			while (to < satStart && index <= numSat && capDem[index] == 0) {
				// tempArrNode1.add(arrNode.get(to));
				to++;
				if (to >= satStart)
					break;
				index = Integer.parseInt(arrNode.get(to).index);
			}
			// if the from and the to is the same, skip it 
			while (to < satStart && index == Integer.parseInt(arrNode.get(from).index)) {
				to++;
				if (to < satStart)
					index = Integer.parseInt(arrNode.get(to).index);
			}

			if (to >= satStart) { // truck route done
				for (int k=1;k<=numSat;k++) 
					if(!tmpFlag[k] && capDem[k] != 0){
						tempArrNode1.add(nodeList.get(0));
						tempArrNode1.add(nodeList.get(k));
					}
				tempDist += calDistNode(arrNode.get(from), nodeList.get(start));
				tempTotalDist = tempTotalDist + tempDist;
				// start = index;
				// from = to;
				// tempArrNode1.add(arrNode.get());
				break;
			}

			// TODO
			else if (currentDemand + arrNode.get(to).demand > this.truckDemand
					|| currentDemand + arrNode.get(to).demand < -this.truckDemand) {
				currentDemand = 0;
//				truckEnd.add(from);
				tempDist += calDistNode(arrNode.get(from), nodeList.get(0))
						+ calDistNode(arrNode.get(to), nodeList.get(0));
				truckCountInc();
			}
			tempDist += calDistNode(arrNode.get(from), arrNode.get(to));
			if (index > numSat)
				currentDemand += arrNode.get(to).demand;
			else if (index > 0)
				currentDemand += capDem[index];
			tempTotalDist = tempTotalDist + tempDist;
			tempArrNode1.add(arrNode.get(to));
			if (index <= numSat) tmpFlag[index] = true;
			from=to;
		}
		totalDistance = tempTotalDist;
		arrNode.clear();
		arrNode.addAll(tempArrNode1);
		arrNode.addAll(tempArrNode2);
	}

	public void createGen() {
		Random rand = new Random();
		ArrayList<Integer> randSatIndex = new ArrayList<>();
		ArrayList<Integer> randCusIndex = new ArrayList<>();
		for (int i = 0; i < numCus; i++)
			randCusIndex.add(numSat + i + 1);
		for (int i = 0; i < numSat; i++)
			randSatIndex.add(i + 1);
		Collections.shuffle(randSatIndex, rand);
		Collections.shuffle(randCusIndex, rand);

		int start = 0;
		double[] currentDemand = new double[numSat + 1];
		this.arrNode.add(nodeList.get(start));
		int i = 0, j = 0; // counter
		int toCus;
		while (i < numSat || j < numCus) {
			if (start == 0) {
				double randPro = rand.nextDouble();
				if (i >= numSat && randPro > 0.2) {
					start = rand.nextInt(numSat) + 1;
					this.arrNode.add(nodeList.get(start));
					continue;
				}
				randPro = rand.nextDouble();
				if (j >= numCus || randPro > 0.4 && i < numSat) {
					toCus = randSatIndex.get(i);
					i++;
				} else {
					toCus = randCusIndex.get(j);
					j++;
				}
				this.arrNode.add(nodeList.get(toCus));
			} else if (j < numCus) {
				toCus = randCusIndex.get(j);
				j++;
				if (currentDemand[start] + nodeList.get(toCus).demand > this.carDemand
						|| currentDemand[start] + nodeList.get(toCus).demand < -this.carDemand) {
					currentDemand[start] = 0;
					start = rand.nextInt(numSat) + 1;
					this.arrNode.add(nodeList.get(start));
				}
				currentDemand[start] += nodeList.get(toCus).demand;
				this.arrNode.add(nodeList.get(toCus));
			}
		}
	}

	public void testPrint() {
		// for (int i = 0; i < arrNode.size(); i++) {
		// System.out.println(arrNode.get(i).getIndex() + " " +
		// arrNode.get(i).getX() + " " + arrNode.get(i).getY()
		// + " " + arrNode.get(i).getDemand());
		// }
		boolean[] flagInPrint = new boolean[nodeList.size()];
		System.out.println("size is " + arrNode.size());
		for (int i = 0; i < arrNode.size(); i++) {
			int index = Integer.parseInt(arrNode.get(i).index);
			if (flagInPrint[index]) {
				if (index > numSat) {
					System.err.println("there is some customer node appears more than once");
//					throw new Exception();
				} else if (index > 0) {
					for (int k = 1; k <= numSat; k++) {
						if (!flagInPrint[k] && capDem[k] != 0) {
							System.err.println("there is some sattlite node did not appear in truck route");
//							throw new Exception();
						}
					}
				}
			}
			flagInPrint[index] = true;

			System.out.print(arrNode.get(i).getIndex() + "-");
		}
		for (int i = 1; i < nodeList.size(); i++) {
			if (!flagInPrint[i]) {
				if (i <= numSat && capDem[i] == 0) {
					continue;
				} else {
					System.err.println("there is node did not appear");
//					throw new Exception();
				}
			}
		}
		System.out.println();
	}
}