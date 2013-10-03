package geometry;

import processing.core.PVector;

public class Point3D {
	
	double x;
	double y;
	double z;
	
	public Point3D(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point3D(PVector vector) {
		super();
		this.x = vector.x;
		this.y = vector.y;
		this.z = vector.z;
	}

	// normalizes a 3D vector
	public void normalize(){
		
		double len = length();
		x /= len;
		y /= len;
		z /= len;
		
	}
	
	public void print(){
		System.out.println("["+x+","+y+","+z+"]");
	}
	
	// calculates vector lenght or distance from point to [0,0,0]
	public double length(){
		
		return Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2));
	}
	
	// returns
	public static double calcDistance(Point3D p1, Point3D p2){
		
		return Math.sqrt(Math.pow(p1.x-p2.x, 2)+Math.pow(p1.y-p2.y, 2)+Math.pow(p1.z-p2.z, 2));
	}
	
	
	public static Point3D add(Point3D p1, Point3D p2){
		
		return new Point3D(p1.x+p2.x,p1.y+p2.y,p1.z+p2.z);
	}

	public static Point3D subtract(Point3D p1, Point3D p2){
		
		return new Point3D(p1.x-p2.x,p1.y-p2.y,p1.z-p2.z);
	}
	
	public static Point3D cross(Point3D p1, Point3D p2){
		
		return new Point3D(p1.y*p2.z-p1.z*p2.y,p1.z*p2.x-p1.x*p2.z,p1.x*p2.y-p1.y*p2.x);
	}
}
