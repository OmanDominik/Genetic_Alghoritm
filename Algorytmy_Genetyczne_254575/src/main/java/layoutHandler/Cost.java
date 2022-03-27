package layoutHandler;

public class Cost {
    private int source;
    private int dest;
    private int cost;

    public Cost() {
        this.source = 0;
        this.dest = 0;
        this.cost = 0;
    }

    public Cost(int source, int dest, int cost) {
        this.source = source;
        this.dest = dest;
        this.cost = cost;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getDest() {
        return dest;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
