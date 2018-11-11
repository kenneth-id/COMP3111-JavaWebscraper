package comp3111.webscraper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to store everything you need for the chart in Trend Tab
 * @author kenneth-id
 */
public class Trend {
	private ArrayList<ArrayList<Item>> itemLists; //  6 5 4 3 2 1 0 day before
	private ArrayList<Double> averagePricesList; 
	private ArrayList<String> datesString;
	private static final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy ");

	/**
	 * Default constructor 
	 * @author kenneth-id
	 * @return An uninitialized Trend object
	 */
	public Trend() {
	itemLists = new ArrayList<ArrayList<Item>>();
	averagePricesList = new ArrayList<Double>();
	datesString= new ArrayList<String>();
	}
	/**
	 * Parameterized constructor 
	 * @author kenneth-id
	 * @return A Trend object initialized with data from a list of Items
	 * @param result - a list of Items
	 */
	
	public Trend(List<Item> result) {
		itemLists = new ArrayList<ArrayList<Item>>();
		averagePricesList = new ArrayList<Double>();
		datesString= new ArrayList<String>();
		setItemLists(result);
		computeAveragePricesList();
		setDatesString();
	}
	
	/**
	 * Helper method to initialized a Trend object with data from a list of Items
	 * @author kenneth-id
	 */
	public void initializeTrend(List<Item> result) {
		setItemLists(result);
		computeAveragePricesList();
		setDatesString();
	}

	/**
	 * Helper method to get the index of a String in the ArrayList datesString 
	 * @author kenneth-id
	 * @param date - String object that you want to get the index of
	 * @return The index of the String as an integer, returns -1 if String is not found
	 */

	public int getDateIndex(String date) {
		for(int i=0; i<datesString.size() ; i++) {
			if(datesString.get(i).equals(date)) {
			return i;
			}
		}
		return -1;
	}
	/**
	 * Helper method to get an Arraylist of Item objects from the list of lists ItemLists 
	 * @author kenneth-id
	 * @param index - integer that specifies the index you want to get 
	 * @return The ArrayList of Items specified by the index, returns -1 if String is not found
	 * @throws IndexOutOfBoundsException - if the index is out of range (index < 0 || index >= size())
	 */
	public ArrayList<Item> getItemList(int index) {
		return itemLists.get(index);
	}
	
	/**
	 * Helper method to initialize the ArrayList datesString 
	 * @author kenneth-id
	 */
	public void setDatesString() {
		String zeroDaysBeforeDate= LocalDate.now().format(displayFormatter);
		String oneDaysBeforeDate= LocalDate.now().minusDays(1).format(displayFormatter);
		String twoDaysBeforeDate= LocalDate.now().minusDays(2).format(displayFormatter);
		String threeDaysBeforeDate= LocalDate.now().minusDays(3).format(displayFormatter);
		String fourDaysBeforeDate= LocalDate.now().minusDays(4).format(displayFormatter);
		String fiveDaysBeforeDate= LocalDate.now().minusDays(5).format(displayFormatter);
		String sixDaysBeforeDate= LocalDate.now().minusDays(6).format(displayFormatter);
		
		datesString.add(sixDaysBeforeDate);
		datesString.add(fiveDaysBeforeDate);
		datesString.add(fourDaysBeforeDate);
		datesString.add(threeDaysBeforeDate);
		datesString.add(twoDaysBeforeDate);
		datesString.add(oneDaysBeforeDate);
		datesString.add(zeroDaysBeforeDate);
	}
	
	/**
	 * Helper method to get the ArrayList datesString 
	 * @author kenneth-id
	 */
	public ArrayList <String> getDatesString() {
		return datesString;
	}
	
	/**
	 * Helper method to initialize the ArrayList averagePricesList
	 * @author kenneth-id
	 */

	public void computeAveragePricesList() { //6 5 4 3 2 1 0
		Double zeroDaysBeforeAverage = 0.0;
		Double oneDaysBeforeAverage = 0.0;
		Double twoDaysBeforeAverage = 0.0;
		Double threeDaysBeforeAverage = 0.0;
		Double fourDaysBeforeAverage = 0.0;
		Double fiveDaysBeforeAverage = 0.0;
		Double sixDaysBeforeAverage = 0.0;
		
		ArrayList<Item> sixDaysBeforeItems =itemLists.get(0);
		ArrayList<Item> fiveDaysBeforeItems = itemLists.get(1);
		ArrayList<Item> fourDaysBeforeItems = itemLists.get(2);
		ArrayList<Item> threeDaysBeforeItems =itemLists.get(3);
		ArrayList<Item> twoDaysBeforeItems = itemLists.get(4);
		ArrayList<Item> oneDaysBeforeItems = itemLists.get(5);
		ArrayList<Item> zeroDaysBeforeItems =itemLists.get(6);
		
		zeroDaysBeforeAverage= getAveragePriceForList(zeroDaysBeforeItems);
		oneDaysBeforeAverage= getAveragePriceForList(oneDaysBeforeItems);
		twoDaysBeforeAverage= getAveragePriceForList(twoDaysBeforeItems);
		threeDaysBeforeAverage= getAveragePriceForList(threeDaysBeforeItems);
		fourDaysBeforeAverage= getAveragePriceForList(fourDaysBeforeItems);
		fiveDaysBeforeAverage= getAveragePriceForList(fiveDaysBeforeItems);
		sixDaysBeforeAverage= getAveragePriceForList(sixDaysBeforeItems);
		
		averagePricesList.add(sixDaysBeforeAverage);
		averagePricesList.add(fiveDaysBeforeAverage);
		averagePricesList.add(fourDaysBeforeAverage);
		averagePricesList.add(threeDaysBeforeAverage);
		averagePricesList.add(twoDaysBeforeAverage);
		averagePricesList.add(oneDaysBeforeAverage);
		averagePricesList.add(zeroDaysBeforeAverage);
	}
	
	/**
	 * Helper method for the function computeAveragePricesList, it returns the average price of an ArrayList of Items
	 * @author kenneth-id
	 * @param list - an ArrayList of Items 
	 * @return Double object which is the average of item prices in a list
	 */
	public Double getAveragePriceForList(ArrayList<Item> list) {
		Double total =0.0;
		int divisor = 0;
		Double zero = 0.0;
		for(int i=0; i< list.size();i++) {
			Double itemPrice =list.get(i).getPrice();
			if(!(itemPrice.equals(zero))) {
				total += itemPrice;
				divisor++;
			}
		}
		if(divisor!= 0) {
		Double average = (Double) total/divisor;
		return average;
		}
		else {
			return 0.0;
		}
	}
	
	/**
	 * Helper method to get the ArrayList averagePricesList
	 * @author kenneth-id
	 * @return ArrayList of Double objects, averagePricesList
	 */
	
	public ArrayList<Double> getAveragePricesList(){
		return averagePricesList;
	}
	
	/**
	 * Helper method to initialize the ArrayList itemLists
	 * @author kenneth-id
	 * @param result  an ArrayList of Items 
	 */
	public void setItemLists(List<Item> result) { //  6 5 4 3 2 1 0 day before
		//initialize lists
		ArrayList<Item> sixDaysBeforeItems = new ArrayList<Item>();
		ArrayList<Item> fiveDaysBeforeItems = new ArrayList<Item>();
		ArrayList<Item> fourDaysBeforeItems = new ArrayList<Item>();
		ArrayList<Item> threeDaysBeforeItems = new ArrayList<Item>();
		ArrayList<Item> twoDaysBeforeItems = new ArrayList<Item>();
		ArrayList<Item> oneDaysBeforeItems = new ArrayList<Item>();
		ArrayList<Item> zeroDaysBeforeItems = new ArrayList<Item>();
		
		//initialize dates
		LocalDate zeroDaysBeforeDate= LocalDate.now();
		LocalDate oneDaysBeforeDate= LocalDate.now().minusDays(1);
		LocalDate twoDaysBeforeDate= LocalDate.now().minusDays(2);
		LocalDate threeDaysBeforeDate= LocalDate.now().minusDays(3);
		LocalDate fourDaysBeforeDate= LocalDate.now().minusDays(4);
		LocalDate fiveDaysBeforeDate= LocalDate.now().minusDays(5);
		LocalDate sixDaysBeforeDate= LocalDate.now().minusDays(6);
		
		//loop through all items in result
		for(int index =0; index<result.size();index++) {
			LocalDate postedDate=result.get(index).getPostedDate().toLocalDate();
		
			if(postedDate.equals(zeroDaysBeforeDate)) {
				zeroDaysBeforeItems.add(result.get(index));
			}
			else if(postedDate.equals(oneDaysBeforeDate)) {
				oneDaysBeforeItems.add(result.get(index));
			}
			else if(postedDate.equals(twoDaysBeforeDate)) {
				twoDaysBeforeItems.add(result.get(index));
			}
			else if(postedDate.equals(threeDaysBeforeDate)) {
				threeDaysBeforeItems.add(result.get(index));
			}
			else if(postedDate.equals(fourDaysBeforeDate)) {
				fourDaysBeforeItems.add(result.get(index));
			}

			else if(postedDate.equals(fiveDaysBeforeDate)) {
				fiveDaysBeforeItems.add(result.get(index));
			}
			else if(postedDate.equals(sixDaysBeforeDate)) {
				sixDaysBeforeItems.add(result.get(index));
			}
		}
		
		//add lists to array of lists in the order of 6 5 4 3 2 1 0 days before
		itemLists.add(sixDaysBeforeItems);
		itemLists.add(fiveDaysBeforeItems);
		itemLists.add(fourDaysBeforeItems);
		itemLists.add(threeDaysBeforeItems);
		itemLists.add(twoDaysBeforeItems);
		itemLists.add(oneDaysBeforeItems);
		itemLists.add(zeroDaysBeforeItems);
	}
	
	/**
	 * Helper method to get the ArrayList of ArrayList filled with Item objects, itemLists
	 * @author kenneth-id
	 * @return ArrayList of ArrayList filled with Item objects, itemLists
	 */
	public ArrayList<ArrayList<Item>> getItemLists() {
		return itemLists;
	}
}
