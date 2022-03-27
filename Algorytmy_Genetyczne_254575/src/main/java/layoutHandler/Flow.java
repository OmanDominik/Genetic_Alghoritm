package layoutHandler;

public class Flow {
    private int source;
    private int dest;
    private int amount;

    public Flow() {
        this.source = 0;
        this.dest = 0;
        this.amount = 0;
    }

    public Flow(int source, int dest, int amount) {
        this.source = source;
        this.dest = dest;
        this.amount = amount;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
