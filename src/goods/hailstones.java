package goods;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/* As a running example, we're going to explore the hailstone sequence, 
 * which is defined as follows. Starting with a number n, 
 * the next number in the sequence is n/2 if n is even, or 3n+1 if n is odd. 
 * The sequence ends when it reaches 1. Here are some examples:
 * 2, 1         
 * 3, 10, 5, 16, 8, 4, 2, 1     
 * 4, 2, 1
 * 2^n, 2^n-1 , ... , 4, 2, 1
 * 5, 16, 8, 4, 2, 1
 * 7, 22, 11, 34, 17, 52, 26, 13, 40, ...? (where does this stop?)
 */

public class hailstones {

	public static void main(String[] args) throws IOException {
		int n;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("please write in a number: ");
		String numLine = br.readLine();
		n = Integer.parseInt(numLine);
		assert (n > 0) : "n is " + n;
		System.out.println();
		while (n != 1) {
			System.out.print(n + " ");
			if (n % 2 == 0) {
				n = n / 2;
			} else {
				n = 3 * n + 1;
			}
		}
		System.out.println(n);
	}

	/**
	 * Compute a hailstone sequence.
	 * 
	 * @param n
	 *            Starting number for sequence. Assumes n > 0.
	 * @return hailstone sequence starting with n and ending with 1.
	 */
	public static List<Integer> hailstoneSequence(int n) {
		final List<Integer> list = new ArrayList<Integer>();
		while (n != 1) {
			list.add(n);
			if (n % 2 == 0) {
				n = n / 2;
			} else {
				n = 3 * n + 1;
			}
		}
		list.add(n);
		return list;
	}

}
