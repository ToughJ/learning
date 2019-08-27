package vrp_GA;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class FileLoader {
    ArrayList<Node> arrNode=new ArrayList();

    public FileLoader() {
    }
    
    public void loadNodeInfo(String url) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new java.io.FileReader(url));
        String line;
        String[] splitLine;
        ArrayList<Node> tempArrNode=new ArrayList<>();
        while ((line = br.readLine()) != null) {
            splitLine=line.split("\\s+");
            Node tempNode = new Node();
            tempNode.setIndex(splitLine[0]);
            tempNode.setX(Double.valueOf(splitLine[1]));
            tempNode.setY(Double.valueOf(splitLine[2]));
            tempNode.setDemand(Double.valueOf(splitLine[3]));
            tempArrNode.add(tempNode);
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