package vrp_twoEwithPnD;

import java.util.ArrayList;

public class Solution {
	double cost;
	double distance;
	ArrayList<Route> crt;
	ArrayList<Route> trt;

	// This is the Solution constructor. It is executed every time a new
	// Solution object is created (new Solution)
	Solution() {
		// A new route object is created addressed by crt
		// The constructor of route is called
		crt = new ArrayList<Route>();
		trt = new ArrayList<Route>();
		cost = 0;
		distance=0;
	}
}
