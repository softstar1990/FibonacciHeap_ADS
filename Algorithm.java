import java.util.ArrayList;

public class Algorithm {
	
	//simple dijkstra algorithm, for adjacent list representation of graph, use find minimal edges in array
	public static void userSimple(String fileName) {
		
		//get all the inputs
		ArrayList<Integer> input = Utility.readInput(fileName);

		//number of nodes, number of edges and source node, read from user input
		int source = input.get(0);
		input.remove(0);
		int number = input.get(0);
		input.remove(0);
		@SuppressWarnings("unused")
		int edges = input.get(0);
		input.remove(0);

		//initialized adjacent list from input, and we need the number of the node
		ArrayList<Integer>[] AdjList = Utility.initAdjList(input, number);
		
		//call dijkstra, input is source node, number of nodes and adjacent list
		int[] distance = dijkstraSimple(source, number, AdjList);
		
		//output the result
		for (int j = 0; j < number; j++) {
			//System.out.println("Node " + j + " : " + distance[j]);
			System.out.println(distance[j]);
		}
	}

	
	//dijkstra using fibonacci heap
	public static void userFheap(String fileName) {

		//get all the inputs
		ArrayList<Integer> input = Utility.readInput(fileName);

		//number of nodes, number of edges and source node, read from user input
		int source = input.get(0);
		input.remove(0);
		int number = input.get(0);
		input.remove(0);
		@SuppressWarnings("unused")
		int edges = input.get(0);
		input.remove(0);

		//initialized adjacent list from input, and we need the number of the node
		ArrayList<Integer>[] AdjList = Utility.initAdjList(input, number);
		
		//call dijkstra, input is source node, number of nodes and adjacent list
		int[] distance = dijkstraFHeap(source, number, AdjList);
		
		//output the result
		for (int j = 0; j < number; j++) {
			//System.out.println("Node " + j + " : " + distance[j]);
			System.out.println(distance[j]);
		}
	}
	
	
	//method for random generated graph
	public static void randomInput(int n, double d, int x) {
		
		int edges = (int) (n*(n-1)*d/200);
		System.out.println("Number of nodes: "+n + ";\tDensity: "+d +"%;\tSource Node: "+ x +";\tNumber of edges: "+ edges);
		//if edges is less than n-1, we cannot generate a connected graph, so we exit the program
		if (edges < n-1) {
			System.out.println("Cannot generate connected graph with such low density");
			System.exit(0);
		}	
		
		//generate adjacent list by a random graph. consider some situation
		//measure the generation time, seems it will be O(edges)
		//System.out.println("--------------running---------------");
		long gen1 = System.currentTimeMillis();
		
		//we use an array of ArrayList to represent the adjacent list
		//for example adjList[0] contains nodes adjacent to node 0, we insert destination and cost in the ArrayList
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] adjList = new ArrayList[n];
		for (int i = 0; i < n; i++) {
			adjList[i] = new ArrayList<Integer>();
		}	
				
		//generate the adjacent list randomly, if we get an unconnect graph, repeat the generation
		boolean generated = Utility.generateAdjList(adjList, n, edges);
		while(!generated){
			for (int i = 0; i < n; i++) {
				adjList[i] = new ArrayList<Integer>();
			}	
			System.out.println("Fail to generate a connect graph, try again");
			generated = Utility.generateAdjList(adjList, n, edges);
		}
		
		long gen2 = System.currentTimeMillis();
		System.out.println("Generation Time: "+(gen2-gen1));
		
		long start1 = System.currentTimeMillis();
		dijkstraSimple(x, n, adjList);
		long stop1 = System.currentTimeMillis();
		
		long start2 = System.currentTimeMillis();
		dijkstraFHeap(x, n, adjList);
		long stop2 = System.currentTimeMillis();
		
		
		System.out.println("Simple Dijkstra: " + (stop1-start1));
		System.out.println("Fibonacci Heaps: " + (stop2-start2));
}
	
	public static int[] dijkstraFHeap(int x, int n, ArrayList<Integer>[] adjList) {
		
		FHeap pq = new FHeap(); //Initialize a new Fibonacci Heap
		FHeap.Entry[] distance = new FHeap.Entry[n]; //distance is the array of entry that in the FHeap
		int[] result = new int[n];	
		boolean[] determined = new boolean[n];
		
		//we first insert all node into FHeap, at beginning, distance to each node is assume to max_value
		for(int i = 0; i < n; i++){
			distance[i] = pq.insert(i, Integer.MAX_VALUE); 
			//notice that enqueue will return a reference to the entry we inserted
		}
		pq.decreaseKey(distance[x], 0); //decrease the distance to source node to 0
		determined[x] = true; 
		
		while (!pq.isEmpty()) {
			
			FHeap.Entry curr = pq.deleteMin();
			int min = curr.getValue();	//get value return the number of the node
			int cost = curr.getPriority();	//get priority return the distance to that node
			determined[min] = true;	
			result[min] = cost;
			
			//for the node that adjacent to the [min] node, if it shortest path was not determined
			//try to update it distance if need
			for (int i = 0; i < adjList[min].size()/2; i++) {
				int adj = adjList[min].get(2*i);
				int arc = adjList[min].get(2*i+1);
				
				int dist = distance[adj].getPriority(); //dist is the current shortest path to the node adjacent to [min]
				if (!determined[adj] && dist > cost + arc) {
					pq.decreaseKey(distance[adj], cost + arc);
				}
			}		
		}
	
		return result;
		
	}

	public static int[] dijkstraSimple(int source, int number,
			ArrayList<Integer>[] adjList) {
		//initialize for dijkstra, using arrays determined[] and distance[]
		//determined[i] is true means that already find shortest path from source to node i
		//distance[i] is the distance from source to node i we found at this time
		boolean[] determined = new boolean[number];
		int[] distance = new int[number];
		for (int i = 0; i < number; i++) {
			determined[i] = false;
			distance[i] = Integer.MAX_VALUE;
		}
		distance[source] = 0;	
		
		//main part of dijkstra algorithm
		for (int i = 0; i < number; i++) {
			//find the node with minimal distance but not determined so far
			//each time to find min from distance[], we need distance.length=number of nodes operations
			//we need find min #nodes times for the outer loop
			int min = 0;
			while(determined[min]){
				min++;
			}
			for (int j = min; j < number; j++) {
				if (determined[j]==false && distance[j]<distance[min]) {
					min = j;
				}
			}
			determined[min] = true; //determined the min as labeled
			
			//in each iteration in dijkstra, we find a node and update some distance[]
			//each time the number of update is equal to the degree of the node
			//then for the outer loop, totally we have #edges updates, and this will not exceed n^2 
			for (int j = 0; j < adjList[min].size()/2; j++) {
				int dest = adjList[min].get(2*j);
				int cost = adjList[min].get(2*j+1);
				if(determined[dest]==false && distance[dest] > distance[min] + cost){
					distance[dest] = distance[min] + cost;
				}
			}
			
			//print current situation after each iteration
//			System.out.print("After "+i+" iteration: ");
//			for (int j = 0; j < number; j++) {
//				System.out.print(determined[j] + "," + distance[j] + "; ");
//			}
//			System.out.print("\n");
			
		}
		
//		File file = new File("output.txt");
//		try {
//			PrintStream p = new PrintStream(file);
//			for (int i = 0; i < distance.length; i++) {
//				p.println("Node "+i+" : "+distance[i]);
//			}
//			p.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		return distance;
	}
}

