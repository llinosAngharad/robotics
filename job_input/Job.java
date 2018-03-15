package job_input;


import java.util.HashMap;
import java.util.Map.Entry;

public class Job {
	private final int ID;
	private final HashMap<Item,Integer> ITEMS;
	private State state;
	
	public Job(int id, HashMap<Item,Integer> items) {
		this.ID = id;
		this.ITEMS = items;
		this.state = State.PENDING;
	}

	public int getID() {
		return ID;
	}
	
	public HashMap<Item,Integer> getItems() {
		return ITEMS;
	}
	
	public double getJobScore() {
		double sum = 0;
		for (Entry<Item, Integer> i: ITEMS.entrySet()) {
			sum += i.getKey().getScore() * i.getValue();
		}
		return sum;
	}
	
	public State getState() {
		return this.state;
	}
	
	public void setState(State state) {
		this.state = state;
	}
}
