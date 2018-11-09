/**
 * 
 */
package comp3111.webscraper;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ComboBox;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;
//import com.google.common.collect.EvictingQueue;


/**
 * 
 * @author kevinw
 *
 *
 * Controller class that manage GUI interaction. Please see document about JavaFX for details.
 * 
 */
public class Controller {

    @FXML 
    private Label labelCount; 

    @FXML 
    private Label labelPrice; 

    @FXML 
    private Hyperlink labelMin; 

    @FXML 
    private Hyperlink labelLatest; 

    @FXML
    private TextField textFieldKeyword;
    
    @FXML
    private TextArea textAreaConsole;
    
    @FXML
    private AreaChart<String, Number> areaChartTrend;
    
    @FXML
    private ComboBox<String> comboBoxTrend;
    
    private WebScraper scraper;
    
    private List<Item> result;
    
    private ObservableList<String> lastFiveSearches;
    
    private ArrayList<List<Item>> lastFiveResults;
    
    /**
     * Default controller
     */
    public Controller() {
    	scraper = new WebScraper();
    	lastFiveSearches = FXCollections.observableArrayList();
    	lastFiveResults= new ArrayList<List<Item>>();
    }

    /**
     * Default initializer. It is empty.
     */
    @FXML
    private void initialize() {
    	
    }
    
    /**
     * Called when the search button is pressed.
     */
    @FXML
    private void actionSearch() {
    	String searchKeyWord =textFieldKeyword.getText();
    	System.out.println("actionSearch: " + searchKeyWord);
    	
    	
    	comboBoxTrend.setItems(lastFiveSearches);
    	result = scraper.scrape(searchKeyWord);
    	
    	if(!lastFiveSearches.contains(searchKeyWord)) {
	    	addToLastFiveSearches(searchKeyWord);
	    	addToLastFiveResults(result);
    	}
    	
    	updateTrendChart(result,searchKeyWord);
    	
    	String output = "Items scraped from craiglist and carousell (Currency in USD) \n ";
    	for (Item item : result) {
    		output += item.getTitle() + "\t" + item.getPrice() +	 "\t" + item.getOrigin() +	 "\t" +item.getUrl() + "\n";
//    		System.out.println(item.getPostedDate().toString());
    	}
    	textAreaConsole.setText(output); 	
    }
    
    @FXML
    void trendComboBoxAction(ActionEvent event) {
    	String comboString = comboBoxTrend.getValue();
    	int index = lastFiveSearches.indexOf(comboString);
    	List<Item> comboResult = lastFiveResults.get(index);
    	updateTrendChart(comboResult,comboString);
    	comboBoxTrend.setValue("Search Record");
    }
    
    private void updateTrendChart(List<Item> result, String searchKeyWord) {
    	//remove previous linechart
    	areaChartTrend.getData().clear();
    	Trend searchTrend = new Trend();
    	searchTrend.initializeTrend(result);
    	XYChart.Series<String, Number> averagePricesSeries = new XYChart.Series<String, Number>();
    	averagePricesSeries.setName("The average selling price of the " + searchKeyWord);
    	
    	for(int i=0; i<7;i++) {
//    		if(!(searchTrend.getAveragePricesList().get(i).equals(0.0))) {
    			averagePricesSeries.getData().add(new Data<String, Number>(searchTrend.getDatesString().get(i), searchTrend.getAveragePricesList().get(i)));
//    		}
//    		else {
//    		averagePricesSeries.getData().add(new Data<String, Number>(searchTrend.getDatesString().get(i), null));
//        		
//    		}
    	}
    	areaChartTrend.getData().addAll(averagePricesSeries);
    }
    
    /**
     * Called when the new button is pressed. Very dummy action - print something in the command prompt.
     */
    @FXML
    private void actionNew() {
    	System.out.println("actionNew");
    }
    
    private void addToLastFiveSearches (String toAdd) {
    	if(lastFiveSearches.size()<5 ) {
    		lastFiveSearches.add(toAdd);
    	}
    	else {
    		lastFiveSearches.remove(0);
    		lastFiveSearches.add(toAdd);
    	}
    }
    
    private void addToLastFiveResults (List<Item> toAdd ) {
    	if(lastFiveSearches.size()<5) {
    		lastFiveResults.add(toAdd);
    	}
    	else {
    		lastFiveResults.remove(0);
    		lastFiveResults.add(toAdd);
    	}
    }
    
}

