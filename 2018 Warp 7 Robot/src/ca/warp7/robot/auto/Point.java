package ca.warp7.robot.auto;

import org.json.simple.JSONObject;

public class Point {
	public int x;
	public int y;
	public String[] methods;
	public double[] args;
	
	public Point(JSONObject json){
		int[] a = (int[]) json.get("cord");
		x = a[0];
		y = a[1];
		methods = (String[])  json.get("methods");
		args = (double[]) json.get("args");
	}
}
