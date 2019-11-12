package vrp_twoEwithPnD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Population {
	ArrayList<Node> nodeList = new ArrayList<>();
	ArrayList<Chromosom> arrChrom = new ArrayList<>();
	double probCross, probMutate;
	int totalPopulation, curGeneration;
	Chromosom bestChromosom;
	int numCus = 0, numSat = 0;
	double carDem = 0.0, truckDem = 0.0;
	int iniChrome = 50;
	
	public Population() {
	}

	public Population(double probCross, double probMutate, int totalPopulation) {
		this.probCross = probCross;
		this.probMutate = probMutate;
		this.totalPopulation = totalPopulation;
	}

	public Population(double probCross, double probMutate, int totalPopulation, int generation) {
		this.probCross = probCross;
		this.probMutate = probMutate;
		this.totalPopulation = totalPopulation;
		this.curGeneration = generation;
	}

	// Customer
	public void setNumCus(int numCus) {
		this.numCus = numCus;
	}

	public double getNumCus() {
		return numCus;
	}

	// Satellite
	public void setNumSat(int numSat) {
		this.numSat = numSat;
	}

	public double getNumSat() {
		return numSat;
	}

	public void setCarDem(double carDem) {
		this.carDem = carDem;
	}

	public double getCarDem() {
		return carDem;
	}

	public void setTruckDem(double truckDem) {
		this.truckDem = truckDem;
	}

	public double getTruckDem() {
		return truckDem;
	}

	public ArrayList<Chromosom> getarrChrom() {
		return arrChrom;
	}

	public void setarrChrom(ArrayList<Chromosom> arrChrom) {
		this.arrChrom.addAll(arrChrom);
	}

	public ArrayList<Node> getNodeList() {
		return nodeList;
	}

	public void setNodeList(ArrayList<Node> arrNode) {
		this.nodeList.addAll(arrNode);
	}

	public double getProbCross() {
		return probCross;
	}

	public void setProbCross(double probCross) {
		this.probCross = probCross;
	}

	public double getProbMutate() {
		return probMutate;
	}

	public void setProbMutate(double probMutate) {
		this.probMutate = probMutate;
	}

	public int getGeneration() {
		return curGeneration;
	}

	public void setGeneration(int generation) {
		this.curGeneration = generation;
	}

	public int getTotalPopulation() {
		return totalPopulation;
	}

	public void setTotalPopulation(int totalPopulation) {
		this.totalPopulation = totalPopulation;
	}

	public void setPopulation(ArrayList<Chromosom> arrChrom) {
		this.arrChrom.addAll(arrChrom);
	}

	public void setParameter(FileLoader fL) {
		this.carDem = fL.carDem;
		this.truckDem = fL.truckDem;
		this.numCus = fL.numCus;
		this.numSat = fL.numSat;
	}

	public void createPopulation() {
		for (int i = 0; i < totalPopulation; i++) {
			Chromosom chrom = new Chromosom(numCus, numSat, carDem, truckDem);
			arrChrom.add(chrom);
		}
		for (int i = 0; i < arrChrom.size(); i++) {
			arrChrom.get(i).setNodeList(nodeList);
			arrChrom.get(i).createGen();
			arrChrom.get(i).calTotalDistance();
			arrChrom.get(i).calFitness();
			// arrChrom.get(i).testPrint();
		}
	}

	// method crossover between Chromosome A and Chromosome B
	public Chromosom crossOverChromosom(Chromosom aChrom, Chromosom bChrom) {
		int numCar = aChrom.carCount + bChrom.carCount;
		Chromosom tempChrom = new Chromosom(numCus, numSat, carDem, truckDem, nodeList),
				crossedChrom = new Chromosom(numCus, numSat, carDem, truckDem, nodeList);
		int i = 0, j = 0;
		Random rand = new Random();
		boolean[] flag = new boolean[numCus + numSat + 1];
		int tott=0;
		ArrayList<Node> tempSec;

		while (i < aChrom.arrNode.size() || j < bChrom.arrNode.size()) {
			double randPro = rand.nextDouble();
			boolean isCopy = true;
			tempSec = new ArrayList<Node>();
			int id1, id2; // these two var is to help to classify to make sure
							// sat all arrise before car route
			if (i < aChrom.arrNode.size())
				id1 = Integer.parseInt(aChrom.arrNode.get(i).index);
			else {
				id1 = 1; // no matter what it is 
			}
			if (j < bChrom.arrNode.size())
				id2 = Integer.parseInt(bChrom.arrNode.get(j).index);
			else {
				id2 = 1;
			}
			if (j >= bChrom.arrNode.size() || (id1 == 0 && id2 > 0) || ((id1 * id2 > 0 || (id1 == 0 && id2 == 0))
					&& (randPro > 0.4 * probCross && i < aChrom.arrNode.size()))) {
				// if bChrom is done or it is ok (aChrom is not done) but we do
				// not want to cross, then we should put i to tempSec
				boolean[] tmpFlag = new boolean[numCus + numSat + 1];
				for (int k =0;k<=numCus+numSat;k++) tmpFlag[k]=flag[k];
				tempSec.add(aChrom.arrNode.get(i));
				i++;
				while (i < aChrom.arrNode.size()) {
					int index = Integer.parseInt(aChrom.arrNode.get(i).index);
					if (index <= numSat && tmpFlag[index]) {
						// this means another route begins
						break;
					}
					if (!tmpFlag[index]) {
						tempSec.add(aChrom.arrNode.get(i));
						tmpFlag[index]=true; 
					} else {
						// there are some node is not the first time to show up
						isCopy = false;
					}
					i++;
				}
				if (isCopy) {
					// then copy to TempChrom
//					System.out.print("");
					for (int k = 0; k < tempSec.size(); k++) {
						int index = Integer.parseInt(tempSec.get(k).index);
						flag[index] = true;tott++;
						tempChrom.arrNode.add(tempSec.get(k));
					}
				}
			} else {
				// in this situation, we choose bChrom
				boolean[] tmpFlag = new boolean[numCus + numSat + 1];
				for (int k =0;k<=numCus+numSat;k++) tmpFlag[k]=flag[k];
				tempSec.add(bChrom.arrNode.get(j));
				j++;
				while (j < bChrom.arrNode.size()) {
					int index = Integer.parseInt(bChrom.arrNode.get(j).index);
					if (index <= numSat && tmpFlag[index]) {
						break;
					}
					if (!tmpFlag[index]) {
						tmpFlag[index]=true; 
						tempSec.add(bChrom.arrNode.get(j));
					} else {
						isCopy = false;
					}
					j++;
				}
				if (isCopy) {
//					System.out.print("dsa");
					for (int k = 0; k < tempSec.size(); k++) {
						int index = Integer.parseInt(tempSec.get(k).index);
						flag[index] = true;tott++;
						tempChrom.arrNode.add(tempSec.get(k));
					}
				}
			}
		}
		return completeChrom(flag, tempChrom);
	}

	private Chromosom completeChrom(boolean[] flag, Chromosom tempChrom) {
		int i = tempChrom.arrNode.size();
		for (int j = numSat + 1; j < nodeList.size(); j++) {
			int index = Integer.parseInt(nodeList.get(j).index);
			if (!flag[index]) {
				tempChrom.arrNode.add(nodeList.get(j));
			}
		}

		Collections.shuffle(tempChrom.arrNode.subList(i, tempChrom.arrNode.size()));
		tempChrom.calTotalDistance();
		tempChrom.calFitness();
		return tempChrom;
	}

	// method mutate 1 Chromosome
	public Chromosom mutateChromosom(Chromosom Chrom) {
		Chromosom tempChrom = new Chromosom(numCus, numSat, carDem, truckDem, nodeList),
				mutatedChrom = new Chromosom(numCus, numSat, carDem, truckDem, nodeList);
		tempChrom = Chrom;
		Node tempNodeA = null, tempNodeB = null;
		Random rand = new Random();
		double randDouble = rand.nextDouble();
		double prob = 0.95;
		int carStart = Chrom.getSatStart();
		int numNode = Chrom.arrNode.size();
		int randomNumA, randomNumB;
		while (carStart < Chrom.arrNode.size() && randDouble < prob) { // carRoute
			prob = prob * prob;
			if ((numNode) - carStart <= 0) {
				System.err.println("dasdas");
			}
			randomNumA = rand.nextInt(numNode - carStart) + carStart;
			randomNumB = rand.nextInt(numNode - carStart) + carStart;
			tempNodeA = Chrom.getArrNode().get(randomNumA);
			tempNodeB = Chrom.getArrNode().get(randomNumB);
			tempChrom.getArrNode().set(randomNumA, tempNodeB);
			tempChrom.getArrNode().set(randomNumB, tempNodeA);
			randDouble = rand.nextDouble();
		}
		// the truck route changing
		prob = 0.8;
		randDouble = rand.nextDouble();
		while (carStart > 2 && randDouble < prob) {
			prob = prob * prob;
			randomNumA = rand.nextInt(carStart - 1) + 1;
			randomNumB = rand.nextInt(carStart - 1) + 1;
			tempNodeA = Chrom.getArrNode().get(randomNumA);
			tempNodeB = Chrom.getArrNode().get(randomNumB);
			tempChrom.getArrNode().set(randomNumA, tempNodeB);
			tempChrom.getArrNode().set(randomNumB, tempNodeA);
			randDouble = rand.nextDouble();
		}
		tempChrom.calTotalDistance();
		tempChrom.calFitness();
		mutatedChrom = tempChrom;
		return mutatedChrom;
	}

	public void crossOverAll() {
		ArrayList<Chromosom> temparrChrom = new ArrayList<>();
		int currentMax = arrChrom.size();
		// add in the firstNode
		// first 21 is the elite chromosome
		for (int i = 0; i < currentMax || i < 21; i++) {
			Chromosom tempChrom = new Chromosom(numCus, numSat, carDem, truckDem, nodeList);
			Random rand = new Random();
			for (int j = currentMax - 1; j > i; j--) {
				double randDouble = rand.nextDouble();
				if (randDouble < probCross) {
					int randomInt = rand.nextInt(j - i) + i + 1;
					tempChrom = crossOverChromosom(arrChrom.get(i), arrChrom.get(randomInt));
					if (tempChrom.getFitness() != arrChrom.get(i).getFitness()
							|| tempChrom.getFitness() != arrChrom.get(randomInt).getFitness()) {
						temparrChrom.add(tempChrom);
					}
				}
			}
			arrChrom.addAll(temparrChrom);
		}
	}

	public void mutateAll() {
		int currentMax = arrChrom.size();
		for (int i = 0; i < currentMax || i < 10 * this.iniChrome; i++) {
			Chromosom toDoChrome = new Chromosom(arrChrom.get(i));
			Random rand = new Random();
			double randDouble = rand.nextDouble();
			if (randDouble < probMutate) {
				Chromosom tempChrom = mutateChromosom(toDoChrome);
				if (tempChrom.getFitness() != arrChrom.get(i).getFitness()) {
					arrChrom.add(tempChrom);
				}
			}
		}
	}

	public void selectBestChroms(int startMinIndex) {
		bestChromosom = new Chromosom(numCus, numSat, carDem, truckDem, nodeList);
		ArrayList<Double> arrFitness = new ArrayList<>();
		ArrayList<Chromosom> arrTempChrom = new ArrayList<>();
		boolean[] flag = new boolean[arrChrom.size() + 1];
		for (int i = 0; i < arrChrom.size(); i++) {
			arrFitness.add(arrChrom.get(i).getFitness());
		}
		// Collections.sort(arrFitness, Collections.reverseOrder());
		Collections.sort(arrFitness);
		int count = 0;
		for (int i = 0; i < arrChrom.size(); i++) {
			for (int j = 0; j < arrChrom.size(); j++) {
				if (!flag[j] && arrChrom.get(j).getFitness() == arrFitness.get(i)) {
					flag[j] = true;
					if (i == 0) {
						bestChromosom = arrChrom.get(j);
						arrTempChrom.add(arrChrom.get(j));
					} else {
						Random rand = new Random();
						if (rand.nextDouble() < 0.5 + 1.0 / (i + 2)) {
							arrTempChrom.add(arrChrom.get(j));
							count++;
						}
					}
					break;
				}
			}
			if (count >= startMinIndex)
				break;
		}

		arrChrom.clear();
		arrChrom.addAll(arrTempChrom);
		// bestChromosom.calTotalDistance();
	}

	public void testPrint()  {
//		for (int i = 0; i < arrChrom.size(); i++) {
//			System.out.print(i + " ");
//			for (int j = 0; j < arrChrom.get(i).getArrNode().size(); j++) {
//				System.out.print(arrChrom.get(i).getArrNode().get(j).getIndex() + "-");
//			}
//			System.out.println("\tTotalDistance : " + arrChrom.get(i).getTotalDistance() + "\tFitness : "
//					+ arrChrom.get(i).getFitness());
//		}
		
		for (int i=0;i<arrChrom.size();i++){
			arrChrom.get(i).testPrint();
		}
	}

	public void testPrintWBestChrom() {
		for (int i = 0; i < arrChrom.size(); i++) {
			System.out.print(i + " ");
			for (int j = 0; j < arrChrom.get(i).getArrNode().size(); j++) {
				System.out.print(arrChrom.get(i).getArrNode().get(j).getIndex() + "-");
			}
			System.out.println("\tTotalDistance : " + arrChrom.get(i).getTotalDistance() + "\tFitness : "
					+ arrChrom.get(i).getFitness());
		}
		System.out.println("Best Chromosom/Route : ");
		for (int i = 0; i < bestChromosom.getArrNode().size(); i++) {
			System.out.print(bestChromosom.getArrNode().get(i).getIndex() + "-");
		}
		System.out.println(
				"\tTotalDistance : " + bestChromosom.getTotalDistance() + "\tFitness : " + bestChromosom.getFitness());
		System.out.println();
	}

}