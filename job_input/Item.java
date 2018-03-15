package job_input;
public class Item {
	private final Location location;
	private final double reward;
	private final double weight;
	private final String name;
	private double score;

	public Item(String name, Location location, double reward, double weight) {
		this.name = name;
		this.location = location;
		this.reward = reward;
		this.weight = weight;
		this.score = reward / weight;
	}
	
	public String getName() {
		return name;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public double getReward() {
		return reward;
	}
	
	public double getWeight() {
		return weight;
	}

	public double getScore() {
		return score;
	}
}
