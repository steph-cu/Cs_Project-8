public class EdgeInfo {

    public EdgeInfo(int from, int to, int capacity,int cost){
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.cost = cost;
		this.residue; = capacity; 

    }
    public String toString(){
        return "Edge " + from + "->" + to + " ("+ capacity + ", " + cost + ") " ;
    }
	public int reduceResidue(int flow){
		if (flow <= residue)residue -= flow; // if it's smaller will reduce residue
		else {// else we get back flow 
			flow = residue - flow ;
			residue = 0; // this will mean that this path is no longer fillable 
		}
		return flow;// if it's negative we get a backflow 
	}

    public int from;        // source of edge
    public int to;          // destination of edge
    public int capacity;    // capacity of edge (U)
    public int cost;        // cost of edge (C)
	public int residue;  	 // the amount that can still flow through


}
