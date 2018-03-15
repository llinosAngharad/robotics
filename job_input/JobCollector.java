package job_input;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/** 
 Job Collector to obtain jobs, cancellations and drop off points.
 @author Richard
 **/
public class JobCollector {
	private final String fileLocation;
	private HashMap<String,Item> items;
	
	/**
	 Class constructor which sets file location and gets items.
	 @param fileLocation location of the files.
	 **/
	public JobCollector(String fileLocation) {
		if (fileLocation.endsWith("/")) fileLocation = fileLocation.substring(0, fileLocation.length()-1);
		this.fileLocation = fileLocation;
		items = getItems();
	}
	

	/**
	 Gets all the jobs for the robots.
	 @return List of all the jobs.
	 @see Job
	 **/
	public List<Job> getJobs() {
		LinkedList<Job> jobs = new LinkedList<>();
		LinkedList<String> file = readFile(FileNames.JOBS);
		for (String s: file) {
			jobs.add(getJob(s));
		}
		return jobs;
	}
	
	/**
	 Gets the drop off locations.
	 @return List of all the provided drop off locations.
	 @see Location
	 **/
	public List<Location> getDropOffs() {
		LinkedList<Location> dropOffs = new LinkedList<>();
		LinkedList<String> file = readFile(FileNames.DROPOFF_LOCATIONS);
		for (String s: file) {
			LinkedList<String> components = getComponents(s);
			int x = Integer.valueOf(components.removeFirst());
			int y = Integer.valueOf(components.removeFirst());
			dropOffs.add(new Location(x,y));
		}
		return dropOffs;
	}
	
	/**
	 Gets all the cancellations, used only for machine learning.
	 @return HashMap of int job ID being cancelled and boolean if the job has been cancelled.
	 **/
	public HashMap<Integer,Boolean> getCancellations() {
		HashMap<Integer,Boolean> cancellations = new HashMap<>();
		LinkedList<String> file = readFile(FileNames.CANCELLATIONS);
		for (String s: file) {
			LinkedList<String> components = getComponents(s);
			int id = Integer.valueOf(components.removeFirst());
			boolean isCancelled;
			if (components.removeFirst().equals("1")) isCancelled = true;
			else isCancelled = false;
			cancellations.put(id,isCancelled);
		}
		return cancellations;
	}
	
	private Job getJob(String jobText) {
		LinkedList<String> components = getComponents(jobText);
		int id = Integer.valueOf(components.removeFirst());
		HashMap<Item,Integer> itemList = new HashMap<>();
		while(!components.isEmpty()) {
			String itemName = components.removeFirst();
			Integer quantity = Integer.valueOf(components.removeFirst());
			itemList.put(items.get(itemName),quantity);
		}
		return new Job(id,itemList);
	}
	
	private HashMap<String,Item> getItems() {
		HashMap<String,Location> locations = getLocations();
		HashMap<String,Item> currentItems = new HashMap<>();
		LinkedList<String> file = readFile(FileNames.ITEMS);
		for (String s: file) {
			LinkedList<String> components = getComponents(s);
			String name = components.removeFirst();
			double reward = Double.valueOf(components.removeFirst());
			double weight = Double.valueOf(components.removeFirst());
			currentItems.put(name, new Item(name,locations.get(name),reward,weight));
		}
		return currentItems;
	}
	
	private HashMap<String,Location> getLocations() {
		HashMap<String,Location> locations = new HashMap<>();
		LinkedList<String> file = readFile(FileNames.ITEM_LOCATIONS);
		for (String s: file) {
			LinkedList<String> components = getComponents(s);
			int x = Integer.valueOf(components.removeFirst());
			int y = Integer.valueOf(components.removeFirst());
			String itemName = components.removeFirst();
			locations.put(itemName, new Location(x,y));
		}
		return locations;
	}
	
	private LinkedList<String> readFile(String fileName) {
		String path = fileLocation+ "/" + fileName + ".csv";
		LinkedList<String> file = new LinkedList<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
			String line = "";
            while ((line = bufferedReader.readLine()) != null) {
            	file.add(line);
            }
        } catch (FileNotFoundException e) {
        		System.out.println("File not found");
        } catch (IOException e) {
        		System.out.println("IO Exception");
        }
		return file;
	}
	
	private static LinkedList<String> getComponents(String line) {
		return new LinkedList<String>(Arrays.asList(line.split(","))); //Files are csv
	}
	
}
