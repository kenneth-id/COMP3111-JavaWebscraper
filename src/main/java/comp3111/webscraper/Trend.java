package comp3111.webscraper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Trend {
	private ArrayList<ArrayList<Item>> itemLists; //  6 5 4 3 2 1 0 day before
	private ArrayList<Double> averagePricesList; 

	public Trend() {
	itemLists = new ArrayList<ArrayList<Item>>();
	averagePricesList = new ArrayList<Double>();
	}
	
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
		Double average = (Double) total/divisor;
		return average;
	}
	
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
	
	public ArrayList<Double> getAveragePricesList(){
		return averagePricesList;
	}
	
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
	
	public ArrayList<ArrayList<Item>> getItemLists() {
		return itemLists;
	}
}
