package vrp_consistency;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
/*
 * Nd Ns Nc small_cap big_cap
   Nv_small Nv_large Nt Ng
   up_s up_l Tmax L 
   i x y h d_i_0 d_i_1...I_i_0_0,I_i_1_0,...
 */

public class auto_gen_data {

	static void autoGen(String urlSaveFile) throws FileNotFoundException {
		PrintWriter printer = new PrintWriter(urlSaveFile);
		DecimalFormat df = new DecimalFormat("0");
		int nd = 1, ns = 1, nc = 10;
		int  small_cap=600, big_cap=3000;
		int Nv_small=3, Nv_large=1, nt = 2, ng = 3;
		int up_s=3, up_l=3, Tmax=1000, L=100; 
		int scale = 250; // 0.1km
		
		printer.println(nd + " " + ns + " " + nc+" "+small_cap+" "+big_cap);
		printer.println(Nv_small + " " + Nv_large + " " + nt+" "+ng);
		printer.println(up_s + " " + up_l + " " + Tmax+" "+L);
		// depot, which actually means the supply the factory
		// printer.println(0+0+0);
		// we don't need supply position, it seems like that.
		// so we regard that (0,0) is the centre of the city.
		
		//supplier  meaningless
		printer.print(0 + " " + 0 + " " + 0 + " "+0+" ");
		for (int k = 0; k < nt; k++) {
			printer.print(0 + " ");
		}
		// I^g0
		for (int j = 0; j < ng; j++) {
			printer.print(0 + " ");
		}
		printer.println();

		for (int i = 1; i <= ns; i++) {
			double supplyDis = Math.random() * 10 + scale - 10;
			double randx = Math.random() * scale*2 - scale;
			if (randx > supplyDis) //TODO
				randx = supplyDis;
			double randy = Math.sqrt(supplyDis * supplyDis - randx * randx);
			// i x y
			printer.print(i + " " + df.format(randx) + " " + df.format(randy) + " ");
			// h
			printer.print("5000 "); //h=500
			//d_i_t
			for (int k = 0; k < nt; k++) {
				printer.print(0 + " ");
			}
			// I^g0
			for (int j = 0; j < ng; j++) {
//				printer.print(df.format(Math.random() * 10) + " ");
				printer.print("0 ");
			}
			printer.println();
		}
		
		for (int i = ns + 1; i <= ns + nc; i++) {
			double randx = Math.random() * scale*2 - scale;
			double randy = Math.random() * scale*2 - scale;
			// i x y
			printer.print(i + " " + df.format(randx) + " " + df.format(randy) + " ");
			// h
			printer.print("500 ");
			// d^t
			for (int j = 0; j < nt; j++) {
				printer.print(df.format(Math.random() * 45 + 5) + " "); // 5-50
			}
			// I^g0
			for (int j = 0; j < ng; j++) {
//				printer.print(df.format(Math.random() * 10) + " ");
				printer.print("0 ");
			}
			printer.println();
		}
		printer.close();
	}

	public static void main(String[] args) throws FileNotFoundException {
		String urlSaveFile = "./vrp-con-data/auto-gen/" + "at_int_02.txt";
		autoGen(urlSaveFile);
	}
}

