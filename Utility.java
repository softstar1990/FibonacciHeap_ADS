import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class Utility {

	//read input from file, please make sure that the input is formatted well as the project instruction
	public static ArrayList<Integer> readInput(String string) {
		File file = new File(string);
		Scanner scanner;
		ArrayList<Integer> inputList = new ArrayList<Integer>();
		try {
			scanner = new Scanner(file);
			while(scanner.hasNextInt())
			{
				inputList.add(Integer.parseInt(scanner.next()));
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error: " + string + " not found");
		}	
		return inputList;	
	}

	//initialize Adjacent List after reading data from a file
	public static ArrayList<Integer>[] initAdjList(ArrayList<Integer> input,
			int number) {
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] AdjList = new ArrayList[number];
		for (int i = 0; i < number; i++) {
			AdjList[i] = new ArrayList<Integer>();
		}
		for (Iterator<Integer> iter = input.iterator(); iter.hasNext();) {
			Integer s = (Integer) iter.next();
			Integer d = (Integer) iter.next();
			Integer c = (Integer) iter.next();
			AdjList[s].add(d);
			AdjList[s].add(c);
			AdjList[d].add(s);
			AdjList[d].add(c);
		}
		return AdjList;
	}
	
	public static boolean generateAdjList(ArrayList<Integer>[] adjList, int n, int edges) {
		
		Random ra = new Random();		
		boolean[][] adjMatrix = new boolean[n][n];
		
		//when density if very large, it will be very easy to generate an edge that we already
		// generated before, for example, after we generate 90% of edges, each time we generate
		// a new edge, only 10% chance we will success, expected need 10 times to generate new one
		//when d is 100, at the last time, only one edge need to generated, but we need expected
		//O(edges) time to randomly get it.
		if (edges == (n*(n-1)/2)) {
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					int c = ra.nextInt(1000);
					adjList[i].add(j);
					adjList[i].add(c);
					adjList[j].add(i);
					adjList[j].add(c);
				}
			}
			return true;
		} 
		
		for (int i = 0; i < edges; i++) {
			int from = 0;
			int to = 0;
			int cost = ra.nextInt(1000);

			do {
				from = ra.nextInt(n);
				to = ra.nextInt(n);				
			} while (from == to);	//make sure the edge not end at the node itself
			
			while(adjMatrix[from][to]){
				do {
					from = ra.nextInt(n);
					to = ra.nextInt(n);				
				} while (from == to);
			}  //make sure the edge was not generated before

			adjMatrix[from][to] = adjMatrix[to][from] = true;
			adjList[from].add(to);
			adjList[from].add(cost);
			adjList[to].add(from);
			adjList[to].add(cost);
		}
		
		//check connect
		boolean[] connect = new boolean[n];
		check(adjMatrix,connect,0);
		
		//when density is too low, it is hard to generate a connected graph
		//if we find the graph in not connected and then generate a new graph, the new graph usually still unconnected
		//so we add edges to make it connected
		if (edges < 3*n) {
			int count = 0;
			for (int i = 0; i < connect.length; i++){
				if(!connect[i]) count++;
			}
			//System.out.println(count);
			
			while(count>0){
				for (int i = 0; i < connect.length; i++) {
					if(!connect[i]){
						int to = ra.nextInt(i);
						int cost =ra.nextInt(1000);
						if (i==0) {
							to=ra.nextInt(n);
						}
						adjMatrix[i][to] = adjMatrix[to][i] = true;
						adjList[i].add(to);
						adjList[i].add(cost);
						adjList[to].add(i);
						adjList[to].add(cost);
					}
				}
				
				connect = new boolean[n];
				check(adjMatrix,connect,0);		
				count = 0;
				for (int i = 0; i < connect.length; i++){
					if(!connect[i]) count++;
				}
				//System.out.println(count);
			}
			return true;
		}
		
		//if not connect return false
		for (int i = 0; i < connect.length; i++) {
			if(!connect[i]){
				return false;
			}
		}
		//if connect return true
		return true;	
	}
	
	//given the adjMatrix and a source i, return an array indicate that whether the node is 
	//connected to the source
	private static void check(boolean[][] adjMatrix, boolean[] connect, int i) {
		connect[i] = true;
		for (int j = 0; j < connect.length; j++) {
			if (adjMatrix[i][j] && !connect[j]) {
				check(adjMatrix, connect, j);
			}
		}	
	}

}