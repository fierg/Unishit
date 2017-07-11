package u8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class KMeans {

	private final static String FILEPATH = "data/kMeans.txt";

	private List<Double[]> points;
	private List<Double[]> centroids;
	private Map<Double[], List<Double[]>> clusters;

	public static void doKMeansClustering(int k, double delta, boolean log) {
		KMeans kMeans = new KMeans(new File(FILEPATH), k);
		int iterations = 0;

		do {
			kMeans.kmeansClustering();
			delta = kMeans.getNewCentroids();
			if (log)
				System.out.println("Iteration: " + ++iterations);

		} while (delta > 0.1);

		System.out.println(String.format("\nFinished after %s \t iterations Quality :%s", iterations,
				kMeans.getQualityOfCluster()));
	}

	public double getQualityOfCluster() {
		double result = 0;
		for (Double[] centroid : centroids) {
			List<Double[]> cluster = clusters.get(centroid);
			for (Double[] points : cluster) {
				result += Math.pow(getDistance(points, centroid), 2);
			}
		}
		return result;
	}

	private KMeans(File f, int k) {
		this.points = readPointsFromFile(f);
		this.centroids = getRandomCentroids(k, this.points);
		this.initCluster();
	}

	private void initCluster() {
		this.clusters = new HashMap<Double[], List<Double[]>>();
		for (Double[] centroid : centroids) {
			clusters.put(centroid, new LinkedList<Double[]>());
		}
	}

	private double getNewCentroids() {
		double delta = 0;
		for (Double[] centroid : centroids) {
			System.out.println("Old centroid: " + Arrays.toString(centroid));
			List<Double[]> cluster = clusters.get(centroid);
			Double[] result = { 0.0, 0.0 };
			for (Double[] point : cluster) {
				// aufsummieren
				for (int index = 0; index < point.length; index++) {
					result[index] += point[index];
				}
			}
			// normalisieren
			for (int index = 0; index < result.length; index++) {
				result[index] = (result[index] / cluster.size());
				delta += Math.abs(Math.abs(result[index]) - Math.abs(centroid[index]));
				centroid[index] = result[index];
			}
			System.out.println("New centroid: " + Arrays.toString(centroid));
		}
		return delta;

	}

	private void kmeansClustering() {
		for (Double[] point : points) {
			Double[] shortest = { Double.MAX_VALUE, Double.MAX_VALUE };
			double distance = getDistance(point, shortest);
			for (Double[] centroid : centroids) {
				double distance2 = getDistance(point, centroid);
				// TODO: refactor, distanz zwischenspeichern
				if (distance2 < distance) {
					shortest = centroid;
					distance = distance2;
				}
			}
			clusters.get(shortest).add(point);

		}
	}

	private List<Double[]> readPointsFromFile(File f) {
		List<Double[]> points = new ArrayList<Double[]>();
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line = br.readLine();
			while (line != null) {
				Double[] d = new Double[2];
				String[] s = line.split(" ");
				for (int i = 0; i < d.length; i++) {
					d[i] = Double.parseDouble(s[i]);
				}
				points.add(d);
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return points;
	}

	private List<Double[]> getRandomCentroids(int k, List<Double[]> points) {
		Random random = new Random();
		List<Double[]> result = new LinkedList<Double[]>();
		for (int i = 0; i < k; i++) {
			int index = (int) Math.ceil(random.nextDouble() * points.size());
			result.add(points.get(index));
		}
		return result;
	}

	private double getDistance(Double[] p1, Double[] p2) {
		if (p1.length != p2.length)
			throw new IllegalArgumentException();
		double result = 0;
		for (int index = 0; index < p1.length; index++) {
			result += Math.pow((p1[index] - p2[index]), 2);
		}
		return Math.sqrt(result);
	}

}
