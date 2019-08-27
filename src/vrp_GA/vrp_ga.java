package vrp_GA;

import java.io.IOException;


public class vrp_ga {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
    	
        FileLoader fL=new FileLoader();
        fL.loadNodeInfo("Node-info - 01.txt"); 
//        fL.testPrint();
//        System.out.println("--------");
        Population pop=new Population(0.8, 0.2, 50);
        pop.setArrNode(fL.getArrNode());
        pop.setIniNode(fL.getArrNode().size()-1);
        pop.createPopulation();
        GA_driver gaDrive=new GA_driver();
        gaDrive.setPopulation(pop);
        gaDrive.setUrlSaveFile("Best Route Per Generation - 01.txt"); 
        gaDrive.evolve(50); 
    }
    
}