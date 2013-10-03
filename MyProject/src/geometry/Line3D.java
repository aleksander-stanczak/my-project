package geometry;

import processing.core.PVector;


public class Line3D {
	
	// start and end points
	public PVector p1;
	public PVector p2;
	
	//directionVector
	PVector u;
	

	public Line3D(PVector p1, PVector p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;

		u = PVector.sub(p2, p1);
		u.normalize();
	}


	// parameter called t or lambda
	public Point3D findCoordinatesGivenT(double t){
			
		return new Point3D(u.x*t+p1.x, u.y*t+p1.y, u.z*t+p1.z);
	}
	
	public void print(){
		System.out.println("Line directional form:");
		System.out.println("|x|"+"   "+"|"+p1.x+"|"+"    |"+u.x+"|");
		System.out.println("|y|"+" = "+"|"+p1.y+"|"+" + t|"+u.y+"|");
		System.out.println("|z|"+"   "+"|"+p1.z+"|"+"    |"+u.z+"|");
	}
	
	public static Line3D findParallelLine(Line3D basicLine, PVector p){
		
		return new Line3D(p, PVector.add(p, basicLine.u));
	}
	
	public double distanceToPoint(PVector p){
		
		PVector ap = PVector.sub(p, p1);
		
		return (double)ap.cross(u).mag();
	}
	
	// finds t parameter on line where point cast intersects with line
	public double findT(PVector p0){
		
		double t = -1 * PVector.dot(PVector.sub(p1, p0), PVector.sub(p2, p1));
		t = t / Math.pow(PVector.sub(p2, p1).mag(),2);

		return t;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

		System.out.println("Hello");
		Line3D line = new Line3D(new PVector(0, 0, 0), new PVector(1, 1, 0));
		line.print();
		//line = findPerpendicularLine(line, new Point3D(3, 4, -1));
		PVector px = new PVector(1, 0, 0);
		System.out.println(line.distanceToPoint(px));
		System.out.println(line.findT(px));
	}

}
