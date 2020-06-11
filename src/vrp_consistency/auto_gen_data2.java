package vrp_consistency;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class auto_gen_data2 {

	static void autoGen(String urlSaveFile) throws FileNotFoundException {
		PrintWriter printer = new PrintWriter(urlSaveFile);
		DecimalFormat df = new DecimalFormat("0");
		int nd = 1, ns = 2, nc = 20;
		int capL = 100, capS = 40;
		int nv = 5, nt = 7, ng = 3;
		printer.println(nd + " " + ns + " " + nc + " " + capS + " " + capL);
		printer.println("1 " + nv + " " + nt + " " + ng);
		// depot, which actually means the supply the factory
		// printer.println(0+0+0);
		// we don't need supply position, it seems like that.
		// so we regard that (0,0) is the centre of the city.
		for (int i = 1; i <= ns; i++) {
			double supplyDis = Math.random() * 10 + 40;
			double randx = Math.random() * 100 - 50;
			if (randx > supplyDis)
				randx = supplyDis;
			double randy = Math.sqrt(supplyDis * supplyDis - randx * randx);
			// i x y
			printer.print(i + " " + df.format(randx) + " " + df.format(randy) + " ");
			// h
			printer.print("300 ");
			// I^g0
			for (int j = 0; j < ng; j++) {
				printer.print(df.format(Math.random() * 10) + " ");
			}
			printer.println();
		}
		for (int i = ns + 1; i <= ns + nc; i++) {
			double randx = Math.random() * 100 - 50;
			double randy = Math.random() * 100 - 50;
			// i x y
			printer.print(i + " " + df.format(randx) + " " + df.format(randy) + " ");
			// h
			printer.print("50 ");
			// d^t
			for (int j = 0; j < nt; j++) {
				printer.print(df.format(Math.random() * 30) + " ");
			}
			// I^g0
			for (int j = 0; j < ng; j++) {
				printer.print(df.format(Math.random() * 10) + " ");
			}
			printer.println();
		}
		printer.close();
	}

	public static void main(String[] args) throws FileNotFoundException {
		String urlSaveFile = "./vrp-con-data/auto-gen/" + "at_int_01.txt";
		autoGen(urlSaveFile);
	}
}
