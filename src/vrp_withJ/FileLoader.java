package vrp_withJ;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class FileLoader {
	public ArrayList<Node> nodeList = new ArrayList();
	public int numCus = 0, numSat = 0;
	public double carDem = 0.0, truckDem = 0.0;

	public FileLoader() {
	}

	public void loadNodeInfo(String url) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new java.io.FileReader(url));
		String line;
		String[] splitLine;
		line = br.readLine();
		splitLine = line.split("\\s+");
		numSat = Integer.parseInt(splitLine[0]);
		numCus = Integer.parseInt(splitLine[1]);
		carDem = Double.valueOf(splitLine[2]);
		truckDem = Double.valueOf(splitLine[3]);
		ArrayList<Node> tempArrNode = new ArrayList<>();
		int i = 0;
		while ((line = br.readLine()) != null) {
			splitLine = line.split("\\s+");
			Node tempNode = new Node();
			tempNode.setIndex(splitLine[0]);
			tempNode.ID=Integer.valueOf(tempNode.index);
			tempNode.setX(Double.valueOf(splitLine[1]));
			tempNode.setY(Double.valueOf(splitLine[2]));
			if (i > numSat)
				tempNode.setDemand(Double.valueOf(splitLine[3]));
			else
				tempNode.setDemand(0);
			tempArrNode.add(tempNode);
			i++;
		}
		nodeList.clear();
		nodeList.addAll(tempArrNode);
	}

	public ArrayList<Node> getNodeList() {
		return nodeList;
	}

	public void setNodeList(ArrayList<Node> nodeList) {
		this.nodeList = nodeList;
	}

	public void testPrint() {
		for (int i = 0; i < nodeList.size(); i++) {
			System.out.println(nodeList.get(i).getIndex() + " " + nodeList.get(i).getX() + " " + nodeList.get(i).getY()
					+ " " + nodeList.get(i).getDemand());
		}
	}
}