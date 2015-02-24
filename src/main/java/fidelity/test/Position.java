package fidelity.test;

public class Position {
    private double amount;

    public Position(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override public String toString() {
        return "Position [amount=" + amount + ']';
    }
}
