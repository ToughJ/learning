package vrp_multiD;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class FileLoader {
	public ArrayList<Node> arrNode=new ArrayList();
	public int numCus = 0, numDep = 0;
	public double carDem =0.0;

    public FileLoader() {
    }
    
    public void loadNodeInfo(String url) throws FileNotFoundException, IOException{
    	url = "C:/learning part//operationResearchh//code/learning/vrp-data/multi_depot/" + url;
        BufferedReader br = new BufferedReader(new java.io.FileReader(url));
        String line;
        String[] splitLine;
        line = br.readLine();
        splitLine=line.split("\\s+");
        numDep = Integer.parseInt(splitLine[0]);
        numCus = Integer.parseInt(splitLine[1]);
        carDem = Double.valueOf(splitLine[2]);
        ArrayList<Node> tempArrNode=new ArrayList<>();
        int i = 0;
        while ((line = br.readLine()) != null) {
            splitLine=line.split("\\s+");
            Node tempNode = new Node();
            tempNode.setIndex(splitLine[0]);
            tempNode.setX(Double.valueOf(splitLine[1]));
            tempNode.setY(Double.valueOf(splitLine[2]));
            tempNode.setDemand(Double.valueOf(splitLine[3]));
            //  IIf Node * is a depot, the demand will be 0.
            tempArrNode.add(tempNode);
            i++;
        }
        arrNode.clear();
        arrNode.addAll(tempArrNode);
    }

    public ArrayList<Node> getArrNode() {
        return arrNode;
    }

    public void setArrNode(ArrayList<Node> arrNode) {
        this.arrNode = arrNode;
    }
    
    public void testPrint(){
        for(int i=0; i<arrNode.size(); i++){
            System.out.println(arrNode.get(i).getIndex()+" "+arrNode.get(i).getX()+" "+arrNode.get(i).getY()+" "+arrNode.get(i).getDemand());
        }
    }
}