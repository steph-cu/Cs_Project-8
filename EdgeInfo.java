public class EdgeInfo {

    public EdgeInfo(int from, int to, int capacity,int cost){
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.cost = cost;
		this.residue = capacity; 

    }
    public String toString(){
        return "Edge " + from + "->" + to + " ("+ capacity + ", " + cost + ") " ;
    }
	

    public int from;        // source of edge
    public int to;          // destination of edge
    public int capacity;    // capacity of edge (U)
    public int cost;        // cost of edge (C)
	public int residue;  	 // the amount that can still flow through


}
