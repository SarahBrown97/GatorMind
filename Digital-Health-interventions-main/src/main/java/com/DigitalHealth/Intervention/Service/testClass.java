package com.DigitalHealth.Intervention.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

public class testClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Clusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<DoublePoint>(2);
	    List<DoublePoint> temp = new ArrayList<>();
	    
		temp.add(new DoublePoint(new int[]{2,2}));
		temp.add(new DoublePoint(new int[]{2,2}));
		temp.add(new DoublePoint(new int[]{10,4}));
		temp.add(new DoublePoint(new int[]{3,7}));
		temp.add(new DoublePoint(new int[]{12,6}));
		temp.add(new DoublePoint(new int[]{20,3}));
		temp.add(new DoublePoint(new int[]{30,5}));
		temp.add(new DoublePoint(new int[]{11,8}));
		temp.add(new DoublePoint(new int[]{25,5}));
		
		List<? extends Cluster<DoublePoint>> res = clusterer.cluster(temp);
		
		//System.out.println(res);
		System.out.println("!!!");
	    System.out.println(res.size());
	    for (Cluster<DoublePoint> re : res) {
	        System.out.println(re.getPoints());
	    }
		
		
		
		
		
	}

}
