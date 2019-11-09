package vrp_twoEwithPnD;

import java.io.IOException;

public class vrp_main {

	public static void main(String[] args) throws IOException {

		FileLoader fL = new FileLoader();
		fL.loadNodeInfo("./vrp-data/conference/data-info- 4.txt");
		
//		// GA
//		// fL.testPrint();
//		// System.out.println("--------");
//		Population pop = new Population(0.8, 0.2, 50);
//		pop.setNodeList(fL.getArrNode());
//		pop.setParameter(fL);
//		pop.createPopulation();
//		// pop.testPrint();
//		GA_driver gaDrive = new GA_driver();
//		gaDrive.setPopulation(pop);
//		gaDrive.setUrlSaveFile("Best Route - 4.txt");
//		gaDrive.evolve(50);
		
//		// NN
//		nearest_neighbor nn = new nearest_neighbor();
//		nn.setNodeList(fL.getArrNode());
//		nn.setParameter(fL);
//		nn.NN();
		
		// TS
		Tabu_search ts = new Tabu_search();
		ts.setNodeList(fL.getArrNode());
		ts.setParameter(fL);
		ts.TS();
	}

}
