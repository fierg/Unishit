package u2;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author Sven Fiergolla
 * 1252732
 *
 */
public class GraphPath {

	public static void main(String[] args) {

		int[][] matrix = null;

		matrix = fillRandom(10, 20, matrix);
		System.out.println("Adjazenzmatrix:\n");
		printMatrix(matrix);
		matrix = fillPathMatrixFloydWarshall(matrix);
		System.out.println("\nWegmatrix:\n");
		printMatrix(matrix);
		
		System.out.println("\nZeitmessung für  50 x  50 in milli\t"  + iterateMeasure(50, 50,100));
		System.out.println("Zeitmessung für 100 x 100 in milli\t" + iterateMeasure(100, 100, 100));
		System.out.println("Zeitmessung für 200 x 200 in milli\t" + iterateMeasure(200, 200, 50));
		System.out.println("Zeitmessung für 400 x 400 in milli\t"  + iterateMeasure(400, 400,10));
		System.out.println("Zeitmessung für 800 x 1000 in milli\t" +  iterateMeasure(800, 1000, 5));
		System.out.println("Zeitmessung für 1600 x 2000 in milli\t" +  iterateMeasure(1600, 1000, 5));
		System.exit(0);
	}
	
	public static int[][] fillRandom(int nodes, int edges, int[][] matrix) {
		matrix = new int[nodes][nodes];

		for (int indexEdges = edges; indexEdges > 0;) {
			int edgePosition = (int) Math.floor(Math.random() * Math.pow(nodes, 2));
			int edgeColumn = (int) Math.floor(edgePosition / nodes);
			int edgeRow = edgePosition % nodes;

			if (matrix[edgeColumn][edgeRow] == 0 && matrix[edgeRow][edgeColumn] == 0 && edgeColumn != edgeRow) {
				matrix[edgeColumn][edgeRow] = 1;
				indexEdges--;
			}

		}
		return matrix;
	}

	public static void printMatrix(int[][] matrix) {
		for (int index = 0; index < matrix.length; index++) {
			System.out.println(Arrays.toString(matrix[index]));

		}
	}
	

	//Algorithmus von Floyd und Warshall
	public static int[][] fillPathMatrixFloydWarshall(int[][] matrix) {
		int[][] dist = initDistMatrix(matrix);

		//Pfade berechnen nach Floyd Warshall
		for (int index1 = 0; index1 < dist.length; index1++) {
			for (int index2 = 0; index2 < dist.length; index2++) {
				for (int index3 = 0; index3 < dist.length; index3++) {
					dist[index3][index2] = Math.min(dist[index3][index2], dist[index3][index1] + dist[index1][index2]);
				}
			}
		}
		//paare (i,i) auf 0 setzten und 'unendlich' auf Integer.MAX_VALUE setzten 
		for (int index = 0; index < dist.length; index++) {
			dist[index][index] = 0;
			if(dist[index][0] == Integer.MAX_VALUE / 2)
			for (int index2 = 0; index2 < dist.length; index2++) {
				dist[index][index2] = Integer.MAX_VALUE;
				dist[index2][index] = Integer.MAX_VALUE;
			}
		}
		return dist;
	}

	private static int[][] initDistMatrix(int[][] matrix) {
		int[][] dist = new int[matrix.length][matrix.length];

		for (int col = 0; col < dist.length; col++) {
			for (int row = 0; row < dist.length; row++) {
				if (matrix[col][row] == 1 || matrix[row][col] == 1) {
					dist[col][row] = 1;
				} else {
					dist[col][row] = Integer.MAX_VALUE / 2;
				}
			}
		}
		return dist;
	}
	
	public static long measureTimeMilli(int nodes, int edges){
		int[][] matrix = null;
	
		matrix = fillRandom(nodes, edges, matrix);
		
		long start = System.currentTimeMillis();
		matrix = fillPathMatrixFloydWarshall(matrix);
		return System.currentTimeMillis() - start;
	}

	public static double iterateMeasure(int nodes, int edges, int iterations){
		double sum=0;
		for (int i = 0; i < iterations; i++) {
			sum += measureTimeMilli(nodes, edges);
		}
		return sum / iterations;
	}

	// A*, funktioniert leider nicht :/
	public static int calculateShortestWayASTar(int[][] matrix, int startNode, int endNode) {
		if (matrix == null)
			return -1;
		if (startNode == endNode)
			return 0;
		if (matrix[startNode][endNode] == 1 || matrix[endNode][startNode] == 1)
			return 1;

		Queue<LinkedList<Integer>> reachable = new PriorityQueue<LinkedList<Integer>>();
		List<Integer> visited = new LinkedList<Integer>();
		int steps = 0;

		LinkedList<Integer> reachableFromStartNode = new LinkedList<Integer>();
		reachableFromStartNode.add(startNode);

		reachable.add(reachableFromStartNode);
		// visited.add(startNode);

		while (!reachable.isEmpty()) {
			LinkedList<Integer> iteration = new LinkedList<Integer>();
			LinkedList<Integer> nextIteration = new LinkedList<Integer>();

			iteration = reachable.poll();

			while (!iteration.isEmpty()) {
				int node = iteration.poll();

				for (int index = 0; index < matrix.length; index++) {
					if (matrix[node][index] == 1 && !visited.contains(index)) {
						nextIteration.add(index);
					}
				}
				reachable.add(nextIteration);

				visited.add(node);

				if (reachable.contains(endNode)) {
					return steps;
				}

			}
			steps++;
		}

		return -1;
	}

	public static int[][] fillPathMatrixAStar(int[][] matrix) {
		for (int col = 0; col < matrix.length; col++) {
			for (int row = 0; row < matrix.length; row++) {
				matrix[col][row] = calculateShortestWayASTar(matrix, col, row);
			}
		}
		return matrix;
	}

	public static long measureTimeNano(int nodes, int edges){
		int[][] matrix = null;
	
		matrix = fillRandom(nodes, edges, matrix);
		
		long start = System.nanoTime();
		matrix = fillPathMatrixFloydWarshall(matrix);
		return System.nanoTime() - start;
		
	}

}
