package ca.warp7.robot.auto;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class Path{
	private static final int MAX_POINTS = 5;
	
	private Point[] points;
	private String sides;
	private double[] groupX;
	private double[] groupY;
	private AkimaSplineInterpolator spline = new AkimaSplineInterpolator();
	
	public Path(JSONObject json) {
		JSONArray data = (JSONArray) json.get("data");
		sides = (String) json.get("sides");
		
		List<Integer> groupXtemp = new ArrayList<>();
		List<Integer> groupYtemp = new ArrayList<>();
		points = new Point[data.size()+MAX_POINTS];
		for(int i=0; i<data.size();i++) {
			points[i] = new Point((JSONObject)data.get(i));
			groupXtemp.add(points[i].x);
			groupYtemp.add(points[i].y);
		}
		
		for(int i=data.size(); i<data.size()+MAX_POINTS;i++) {
			points[i] = points[data.size()-1];
			groupXtemp.add(points[i].x);
			groupYtemp.add(points[i].y);
		}
		groupX = toDoubleArray(groupXtemp);
		groupY = toDoubleArray(groupYtemp);
		
	}
	
	private double[] toDoubleArray(List<Integer> list) {
		return list.stream().mapToDouble(i->i).toArray();
	}
	
	public void calculateSpline() {
		PolynomialSplineFunction a = AkimaSplineInterpolator.interpolate(groupX,groupY);
	}
}

class Point {
	public int x;
	public int y;
	public Method[] methods;
	
	public Point(JSONObject json){
		int[] a = (int[]) json.get("cord");
		x = a[0];
		y = a[1];
		JSONArray methodArr = (JSONArray) json.get("methods");
		methods = new Method[methodArr.size()];
		for(int i=0; i<methodArr.size();i++)
			methods[i] = new Method((JSONObject)methodArr.get(i));
	}
}

class Method{
	public String name;
	public JSONArray args;
	public Method(JSONObject json) {
		name = (String) json.get("name");
		args = (JSONArray) json.get("args");
	}
}
