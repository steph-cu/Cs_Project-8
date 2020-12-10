import java.io.File;
import java.util.Scanner;

public class FlowGraph {
    int vertexCt;  // Number of vertices in the graph.
    GraphNode[] G;  // Adjacency list for graph.
    String graphName;  //The file from which the graph was created.
    int maxFlowFromSource;
    int maxFlowIntoSink;


    public FlowGraph() {
        this.vertexCt = 0;
        this.graphName = "";
        this.maxFlowFromSource = 0;
        this.maxFlowIntoSink = 0;
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
    }
/*************'
***************
***************
*************
*********/
    public static void main(String[] args) {
        FlowGraph graph1 = new FlowGraph();
        graph1.makeGraph("group0.txt");
        System.out.println(graph1.toString());
		graph1.findFlows();
    }
/***********
************
*************
**************
****************/
/***
goal: to find the path to fill and to do so with lowest cost. 
***/
	public void findFlows(){
		// need to find shortest path with cost
		StringBuilder Pathes = new StringBuilder();
		while(DijkstraNeg()){
			// start at beginning and mark nodes as visited
			LinkedList<Integer> forFlow = new LinkedList();
			GraphNode path = G[vertexCt-1]; // the end node
			while(path.nodeID != 0){// makes the path
				forFlow.addFirst(path.nodeID);
				path = G[path.prevNode];
			}
			// need to find the max flow that can go on this path 
			LinkedList<Integer> copy = new LinkedList(forFlow);
			Iterator<EdgeInfo> itr = G[forFlow.removeFirst()].succ.iterator();// gets to source
			Integer removed = forFlow.removeFirst();// to check for next place for source
			Integer maxFlow = removed.distance;// to set for the first path 
			EdgeInfo previousOnPath = null; 
			while(itr.hasNext()){
				EdgeInfo next = itr.next();
				if (next.to == removed){
					if (maxFlow > next.residue){// if there's more flow than what can go forward
						Integer backflow = next.reduceResidue(maxFlow);// how much we got?
						//reverseMaxFlow(forFlow, removed, maxFlow - backFlow);// to make flow in past passes, smaller (for the path, where to stop, size) [maybe later]]
						while(itr.hasNext()){		// search for nodes that have: 
							EdgeInfo prev = itr.next();
							if (prev.capacity == 0 && (-prev.cost) > previousOnPath.cost){// another path that goes back and it's cost is greater
								Iterator<EdgeInfo> itr2 = G[prev.to].succ.iterator();
								while(itr2.hasNext()){							// find path that leads to current node. 
									EdgeInfo moreExpensive = itr2.next();
									if(moreExpensive.to == removed){
										backFlow = moreExpensive.reduceResidue(backflow);
										break; 
									}
								}
							}
						}
						if (backFlow != 0) reverseMaxFlow(copy, removed, maxFlow + backFlow);//needs to decrease the flow through the path (if leftover backFlow) 
					}
					else next.reduceResidue(maxFlow);// else all you have to do is reduce the residue and then move to next one 
					if (forFlow.isEmpty()) break; //you've reached the end no more needed 
					itr = G[next.to].succ.iterator();
					removed = forFlow.removeFirst();
					previousOnPath = next; 
				}
			}
		}
	}
	
	private void reverseMaxFlow(LinkedList<Integer> forFlow, Integer removed, Integer RmaxFlow){
		LinkedList<Integer> copy = new LinkedList(forFlow);
		Iterator<EdgeInfo> itr = G[copy.removeFirst()].succ.iterator();
		Integer next = copy.removeFirst();
		while (itr.hasNext()){
			EdgedInfo nextEdge = itr.next();
			if(nextEdge.to == next){
				nextEdge.residue = capacity - RmaxFlow; // not sure this is right 
				if (nextEdge.to == removed) break; // if you've gotten back to your current node 
				itr = G[nextEdge.to].succ.iterator();
				next = copy.removeFirst();
			}
		}
	}
	
	private boolean DijkstraNeg() {
    Queue<Integer> q = new Queue;
    for (int i = 0; i < G.size(); i++) {
        G[i].distance = INFINITY;
        G[i].prevNode = -1;  // Each node stored its predecessor in the shortest path
    } // will need because I need to reset prevNode 
    G[0].distance = 0;
    q.enqueue(G[0]);
    while (!q.isEmpty()) {  // while anything has changed, keep updating
        Integer v = q.dequeue();
		Iterator<EdgeInfo> itr = G[v].succ.iterator();
        while (itr.hasNext()){
			EdgeInfo next = itr.next();
			if (next.residue != 0){// if the path still has pushing room 
				if (G[v].distance + next.cost < G[next.to].distance){// if distance from source + distance to next node is less than the distance established  at next node 
					G[next.to].distance = G[v].distance + next.cost;
					G[next.to].prevNode = G[v].nodeID;
					q.enqueue(G[next.to]);  // the distance to w has changed so its successors are updated
				}
			}
        }
    }
    return G[vertexCt-1].pred >= 0;  // Did you find a path to the sink?
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
                if (v1 == 0) maxFlowFromSource += cap;
                if (v2 == vertexCt - 1) maxFlowIntoSink += cap;
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}