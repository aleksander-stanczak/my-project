package util;

import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;

public enum Direction {
	O,N,NE,E,SE,S,SW,W,NW,CN,CNE,CE,CSE,CS,CSW,CW,CNW,FN,FNE,FE,FSE,FS,FSW,FW,FNW,C,F;
	
	public ObservationDiscrete<Direction> observation() {
		return new ObservationDiscrete<Direction>(this);
	}
}
