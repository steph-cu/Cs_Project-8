import java.io.File;
import java.util.Scanner;
import java.util.*;

public class FlowGraph {
    int vertexCt;  // Number of vertices in the graph.
    GraphNode[] G;  // Adjacency list for graph.
    String graphName;  //The file from which the graph was created.
    int maxFlowFromSource;
    int maxFlowIntoSink;
	int [][] resGraph; 
	int TotalSpace; 


    public FlowGraph() {
        this.vertexCt = 0;
        this.graphName = "";
        this.maxFlowFromSource = 0;
        this.maxFlowIntoSink = 0;
		//this.resGraph = new int [0][0];
		this.TotalSpace = 0; 
    }

    /**
     * create a graph with vertexCt nodes
     * @param vertexCt
     */
    public FlowGraph(int vertexCt) {
        this.vertexCt = vertexCt;
        G = new GraphNode[vertexCt];
        for (int i = 0; i < vertexCt; i++) {
            G[i] = new GraphNode(i);
        }
        this.maxFlowFromSource = 0;
        this.maxFlowIntoSink = 0;
		//this.resGraph = new int[vertexCt][vertexCt];
		this.TotalSpace = 0; 
    }
/*************'
***************
***************
*************
*********/
    public static void main(String[] args) {
        FlowGraph graph1 = new FlowGraph();
		String [] texts = {"group0.txt", "group1.txt", "group4.txt", "group5.txt", "group6.txt", "group7.txt", "group8.txt", "bellman0.txt"};
		for(int i = 1; i < 2; i++){
			graph1.makeGraph(texts[i]);
			System.out.println(graph1.toString());
			graph1.findFlows(texts[i]);
			// graph1.printEdges();
		}
    }
/***********
************
*************
**************
****************/
/***
goal: to find the path to fill and to do so with lowest cost. 
***/
	public void findFlows(String filename){
		int flow = 0;
		int totalFlow = 0; 
		int cost = 0; 
		while(DijkstraNeg()){
			flow = lowestFlow();
			//System.out.println("flow: " + flow);
			cost += fixCost(flow);
			totalFlow += flow; 
			System.out.println("found flow " + flow + ":" + fixFlow(flow));
		}
		System.out.println(filename + "Max Flow SPACE " + Math.min(maxFlowFromSource,maxFlowIntoSink) + " assigned " + totalFlow);
		printEdges(cost);
	}
	private int lowestFlow(){
		int currNode = vertexCt - 1; 
		int flow = 9999;
		int prev; 
		while (G[currNode].prevNode != -1){
			prev = G[currNode].prevNode;
			if (flow > getCapacity(prev, currNode)){
				flow = getCapacity(prev, currNode);
			}
			currNode = prev; 
		}
		if (currNode != 0){
			flow = 0;
		}
		return flow;
	}
	private int fixCost(int flow){
		int currNode = vertexCt - 1;
		int prev; 
		int pathCost = 0; 
		while (G[currNode].prevNode != -1){
			prev = G[currNode].prevNode;
			pathCost += getCost(prev, currNode);
			currNode = prev; 
		}
		int totalCost = pathCost * flow; 
		return totalCost;
	}
	private String fixFlow(int flow){
		int currNode = vertexCt-1; 
		StringBuilder str = new StringBuilder();
		int prev;
		while (G[currNode].prevNode != -1){
			prev = G[currNode].prevNode;
			resGraph[prev][currNode] -= flow;
			resGraph[currNode][prev] += flow; 
			str.insert(0, " " + currNode);
			currNode = prev;
		}
		return str.toString();
	}
	
	private boolean DijkstraNeg() {
    Queue<Integer> q = new LinkedList<Integer>();// in- out order not priority
    for (int i = 0; i < G.length; i++) {
        G[i].distance = 9999;// for infinity
        G[i].prevNode = -1;  // Each node stored its predecessor in the shortest path
    } // will need because I need to reset prevNode 
    G[0].distance = 0;
    q.add(G[0].nodeID);
    while (!q.isEmpty()) {  // while anything has changed, keep updating
        Integer v = q.remove();
		Iterator<EdgeInfo> itr = G[v].succ.iterator();
        while (itr.hasNext()){
			EdgeInfo next = itr.next();
			if (resGraph[next.from][next.to] != 0){// if the path still has pushing room 
				if (G[v].distance + next.cost < G[next.to].distance){// if distance from source + distance to next node is less than the distance established  at next node 
					G[next.to].distance = G[v].distance + next.cost;
					G[next.to].prevNode = G[v].nodeID;
					q.add(G[next.to].nodeID);  // the distance to w has changed so its successors are updated
					//System.out.println("printed queue" + q);
				}
			}
        }
    }
    return G[vertexCt-1].prevNode >= 0;  // Did you find a path to the sink?
}

	public void printEdges(int total){
		for(int i = 0; i < vertexCt; i++){
			G[i].printEdge(vertexCt, resGraph, i);
		}
		System.out.println("TotalCost = " + total);
	}
	
    public int getVertexCt() {
        return vertexCt;
    }

    public int getMaxFlowFromSource() {
        return maxFlowFromSource;
    }

    public int getMaxFlowIntoSink() {
        return maxFlowIntoSink;
    }

    /**
     * @param source
     * @param destination
     * @param cap         capacity of edge
     * @param cost        cost of edge
     * @return create an edge from source to destination with capacation
     */
    public boolean addEdge(int source, int destination, int cap, int cost) {
        System.out.println("addEdge " + source + "->" + destination + "(" + cap + ", " + cost + ")");
        if (source < 0 || source >= vertexCt) return false;
        if (destination < 0 || destination >= vertexCt) return false;
        //add edge
        G[source].addEdge(source, destination, cap, cost);
        return true;
    }

    /**
     * @param source
     * @param destination
     * @return return the capacity between source and destination
     */
    public int getCapacity(int source, int destination) {
        return G[source].getCapacity(destination);
    }

    /**
     * @param source
     * @param destination
     * @return the cost of the edge from source to destination
     */
    public int getCost(int source, int destination) {
        return G[source].getCost(destination);
    }

    /**
     * @return string representing the graph
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("The Graph " + graphName + " \n");
        sb.append("Total input " + maxFlowIntoSink + " :  Total output " + maxFlowFromSource + "\n");

        for (int i = 0; i < vertexCt; i++) {
            sb.append(G[i].toString());
        }
        return sb.toString();
    }

    /**
     * Builds a graph from filename.  It automatically inserts backward edges needed for mincost max flow.
     * @param filename
     */
    public void makeGraph(String filename) {
        try {
            graphName = filename;
            Scanner reader = new Scanner(new File(filename));
            vertexCt = reader.nextInt();
            G = new GraphNode[vertexCt];
			resGraph = new int[vertexCt][vertexCt];
            for (int i = 0; i < vertexCt; i++) {
                G[i] = new GraphNode(i);
            }
            while (reader.hasNextInt()) {
                int v1 = reader.nextInt();
                int v2 = reader.nextInt();
                int cap = reader.nextInt();
                int cost = reader.nextInt();
                G[v1].addEdge(v1, v2, cap, cost);
                G[v2].addEdge(v2, v1, 0, -cost);
				resGraph[v1][v2] = cap;
				resGraph[v2][v1] = 0; 
				TotalSpace += cap; 
                if (v1 == 0) maxFlowFromSource += cap;
                if (v2 == vertexCt - 1) maxFlowIntoSink += cap;
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}