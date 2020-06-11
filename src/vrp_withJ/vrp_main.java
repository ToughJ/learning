package vrp_withJ;

import java.io.FileNotFoundException;
import java.io.IOException;

public class vrp_main {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		FileLoader fL = new FileLoader();
		fL.loadNodeInfo("./vrp-data/delta/data-info- 5.txt");
	}
}