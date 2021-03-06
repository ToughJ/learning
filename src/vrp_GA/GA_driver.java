package vrp_GA;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class GA_driver {
    ArrayList<Chromosom> arrChrom;
    String urlSaveFile;
    Population population;
    int numPop = 50;
    
    public GA_driver() {
        arrChrom=new ArrayList<>();
        population=new Population();
    }

    public ArrayList<Chromosom> getArrChrom() {
        return arrChrom;
    }

    public void setArrChrom(ArrayList<Chromosom> arrChrom) {
        this.arrChrom.clear();
        this.arrChrom.addAll(arrChrom);
    }

    public Population getPopulation() {
        return population;
    }

    public void setPopulation(Population population) {
        this.population = population;
    }

    public String getUrlSaveFile() {
        return urlSaveFile;
    }

    public void setUrlSaveFile(String urlSaveFile) {
        this.urlSaveFile = urlSaveFile;
    }
    
    public void evolve(int n) throws FileNotFoundException{
        PrintWriter printer = new PrintWriter(urlSaveFile);
        for(int i=0; i<n; i++){
            printer.println("====Generation -"+i+"====");

            population.crossOverAll();
            population.mutateAll();
            population.selectBestChroms(numPop);
            
//            this.population.bestChromosom.testPrint();

            for(int j=0; j<this.population.bestChromosom.getArrNode().size(); j++){
                printer.print(this.population.bestChromosom.getArrNode().get(j).getIndex()+"-");
            }
            printer.println("\tTotalDistance : "+this.population.bestChromosom.getTotalDistance()+
            		"\tFitness : "+this.population.bestChromosom.getFitness());
            printer.println();
        }
        printer.close();
    }
    

    
}