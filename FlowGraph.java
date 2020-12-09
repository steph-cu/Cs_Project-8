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
			Integer maxFlow = findMaxFlow(forFlow); 
			Iterator<EdgeInfo> itr = G[forFlow.removeFirst()].succ.iterator();// gets to source
			Integer removed = forFlow.removeFirst();// to check for next place for source
			while(itr.hasNext()){
				EdgeInfo next = itr.next();
				if (next.to == removed){
					next.reduceResidue(maxFlow);
					if (forFlow.isEmpty()) break;
					else {
						itr = G[removed].succ.iterator();
						removed = forFlow.removeFirst();
					}
				}
			}
			/***
			will need:
			- check for residue
			- alter paths if needed 
				- might need to alter the DijkstraNeg() as well. to check for residue (cause we can't go forward if no residue 
			***/
		}
	}
	
	private Integer findMaxFlow(LinkedList<Integer> forFlow){
		Integer max = INFINITY;
		LinkedList<Integer> copy = new LinkedList(forFlow);// don't alter the previous one 
		Iterator<EdgeInfo> itr = G[copy.removeFirst()].succ.iterator();// we will look at the beginning of the path's successors
		int removed = copy.removeFirst();// this is the successor we are looking for 
		while(itr.hasNext()){
			EdgeInfo next = itr.next();
			if (next.to == removed){// if the edge goes to the next one in the path
				if (max > next.residue) max = next.residue;// if max can only smaller, we make it so 
				if (copy.isEmpty()) break;// if the linkelist is empty we don't need to keep going we're done
				else {
					itr = G[removed].succ.iterator();// the iterator moves to what next part of the linked list 
					removed = copy.removeFirst();// removed will be updated
				}
			}
		}
		return max; 
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