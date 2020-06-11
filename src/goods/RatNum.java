package goods;

public class RatNum {
	private final int numer;
	private final int denom;

	// Rep invariant:
	// denom > 0
	// numer/denom is in reduced form

	// Abstraction Function:
	// represents the rational number numer / denom

	/**
	 * Make a new Ratnum == n.
	 * 
	 * @param n
	 *            value
	 */
	public RatNum(int n) {
		numer = n;
		denom = 1;
		checkRep();
	}

	/**
	 * Make a new RatNum == (n / d).
	 * 
	 * @param n
	 *            numerator
	 * @param d
	 *            denominator
	 * @throws ArithmeticException
	 *             if d == 0
	 */
	public RatNum(int n, int d) throws ArithmeticException {
		// reduce ratio to lowest terms
		int g = gcd(n, d);
		n = n / g;
		d = d / g;

		// make denominator positive
		if (d < 0) {
			numer = -n;
			denom = -d;
		} else {
			numer = n;
			denom = d;
		}
		checkRep();
	}

	// Check that the rep invariant is true
	// *** Warning: this does nothing unless you turn on assertion checking
	// by passing -enableassertions to Java
	private void checkRep() {
		assert denom > 0;
		assert gcd(numer, denom) == 1;
	}

	public int gcd(int a, int b) {
		int r;
		while (b > 0) {
			r = a % b;
			a = b;
			b = r;
		}
		return a;
	}
}
