package goods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class construction {

	public static void main(String[] args) {
		final char[] vowels = new char[] { 'a', 'e', 'i', 'o', 'u' };
		// vowels = new char[] {'x', 'y','z'};
		vowels[0] = 'z';
		for (char c : vowels) {
			System.out.print(c + " ");
		}
		System.out.println();

		List<Integer> myData = Arrays.asList(-5, -3, -2);
		System.out.println(sum(myData));
		System.out.println(sumOfAbsoluteValues(myData));
		System.out.println(sum(myData)); // mutation of the variable makes bug!!

		ArrayList<String> StringList = new ArrayList<>();
		StringList.add("6.045");
		StringList.add("6.005");
		StringList.add("6.813");
		dropCourse6(StringList);
		for (String s : StringList) {
			System.out.print(s + " ");
		}
		System.out.println(); // should be null instead of 6.005
		
		Map<String, Integer> a = new HashMap<>(), b = new HashMap<>();
		a.put("c", 130); // put ints into the map
		b.put("c", 130);
		System.out.println(a.get("c") == b.get("c")); // what do we get out of the map?
		
	}

	/**
	 * Drop all subjects that are from Course 6. Modifies subjects list by
	 * removing subjects that start with "6."
	 * 
	 * @param subjects
	 *            list of MIT subject numbers
	 */
	public static void dropCourse6(ArrayList<String> subjects) {
		 MyIterator iter = new MyIterator(subjects);
		 while (iter.hasNext()) {
		 String subject = iter.next();
		 	if (subject.startsWith("6.")) {
		 		subjects.remove(subject);
		 	}
		 }

		// The built-in iterator detects that you're changing the list under its
		// feet and cries foul.
//		for (String subject : subjects) {
//			if (subject.startsWith("6.")) {
//				subjects.remove(subject);
//			}
//		}
	}
	// Testing strategy:
	// subjects.size: 0, 1, n
	// contents: no 6.xx, one 6.xx, all 6.xx
	// position: 6.xx at start, 6.xx in middle, 6.xx at end

	// Test cases:
	// [] => []
	// ["8.03"] => ["8.03"]
	// ["14.03", "9.00", "21L.005"] => ["14.03", "9.00", "21L.005"]
	// ["2.001", "6.01", "18.03"] => ["2.001", "18.03"]
	// ["6.045", "6.005", "6.813"] => []

	/** @return the sum of the absolute values of the numbers in the list */
	public static int sumOfAbsoluteValues(List<Integer> list) {
		// let's reuse sum() because DRY, so first we take absolute values
		for (int i = 0; i < list.size(); ++i)
			list.set(i, Math.abs(list.get(i)));
		return sum(list);
	}

	/** @return the sum of the numbers in the list */
	public static int sum(List<Integer> list) {
		int sum = 0;
		for (int x : list)
			sum += x;
		return sum;
	}

}
