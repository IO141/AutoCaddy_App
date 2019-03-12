package com.cbrmm.autocaddy.util;


public interface ControlUtils {
	
	default boolean modSettGrp1(Data data) {
		return false;
	}
	
	default boolean modSettGrp2(Data data) {
		return false;
	}
	
	default boolean modSettGrp3(Data data) {
		return false;
	}
	
	default boolean getStatus(Data data) {
		return false;
	}
	
	default boolean sendStatus(Data data) {
		return false;
	}
	
}
