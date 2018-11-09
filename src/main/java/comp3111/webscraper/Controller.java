/**
 * 
 */
package comp3111.webscraper;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
import javafx.event.EventHandler;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;

import java.util.Iterator;
import java.util.List;
//import com.google.common.collect.EvictingQueue;

//import java.awt.Button;
import javafx.application.HostServices;


import javax.security.auth.callback.Callback;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;





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
    
    private TableView<Item> tableControl;

    @FXML
    private TableColumn title;

    @FXML
    private TableColumn price;

    @FXML
    private TableColumn url;

    @FXML
    private TableColumn postedDate;
    
    @FXML
    private Button refineID;

    @FXML
    private Button goID;
    
    @FXML
    private TextField textFieldKeywordRefine;
    
    private String beforeRefine; // to store keyword before refining
    
    
    
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
//    	System.out.println(comboString);
    	int index = lastFiveSearches.indexOf(comboString);
//    	System.out.println(index);
    	List<Item> comboResult = lastFiveResults.get(index);
    	updateTrendChart(comboResult,comboString);
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
//    		System.out.println("Index in adding points"+i);
    		Data<String,Number> point =new Data<String, Number>(searchTrend.getDatesString().get(i), 
    				searchTrend.getAveragePricesList().get(i));
    		averagePricesSeries.getData().add(point); 		
//    		}
//    		else {
//    		averagePricesSeries.getData().add(ne	w Data<String, Number>(searchTrend.getDatesString().get(i), null));  		
//    		}
    	}
    	areaChartTrend.getData().addAll(averagePricesSeries);
    	//adding double click event handler to each point
    	//TODO: fix bug with combobox
    	for(int i=0; i<7;i++) {
//    		System.out.println("Index in adding listeners"+i);
    		areaChartTrend.getData().get(0).getData().get(i).getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
        		    new EventHandler<MouseEvent>() {
    		        @Override 
    		        public void handle(MouseEvent mouseEvent) {
    		                 if(mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2){
    		                     System.out.println("Double clicked");
    		                 }
    		         }
    		    });
    	}
    	beforeRefine = textFieldKeyword.getText();
    	
    	tableTab();
    	refineID.setDisable(false);
    }
    

    @FXML
    private void mouseClicked() {
    	if(textAreaConsole.getText().isEmpty()) {
    		refineID.setDisable(true);
    		return;
    	}
    }
    @FXML
    private void refineSearch() {
    	//System.out.println("actionSearch: " + textFieldKeyword.getText());
    	
    	//if(textFieldKeyword.getText().isEmpty() && textFieldKeywordRefine.getText().isEmpty()) 
    	if(textAreaConsole.getText().isEmpty()) {
    		refineID.setDisable(true);
    		return;
    	}

    	if(refineID.isDisabled()==false) {
//	    	List<Item> result = scraper.scrape(beforeRefine + " " + textFieldKeywordRefine.getText());
    		result = scraper.scrape(beforeRefine);
    		
    		for (Iterator<Item> iter = result.listIterator(); iter.hasNext(); ) {
    		    Item item = iter.next();
    		    if (!item.getTitle().contains(textFieldKeywordRefine.getText())) {
    		        iter.remove();
    		    }
    		}
    		String output = "";
	    	for (Item item : result) {
    		output += item.getTitle() + "\t" + item.getPrice() + "\t" + item.getUrl() + "\n";
	    	}	
	    	textAreaConsole.setText(output);
	    	tableTab();
    	}
    	
    	refineID.setDisable(true);
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
    
    /** Task 4: Fill out table
     * Called when the table tab is clicked
     * **/
    @FXML
    private void tableTab() {
    	tableControl.setEditable(false);
    	//List<Item> result = scraper.scrape(textFieldKeyword.getText());
    	
    	ObservableList<Item> data = FXCollections.observableArrayList(result);
    	
    	title.setCellValueFactory(new PropertyValueFactory<Item,String>("title"));
    	price.setCellValueFactory(new PropertyValueFactory<Item,Double>("price"));
    	url.setCellValueFactory(new PropertyValueFactory<Item,String>("url"));
    	
    	tableControl.setItems(data);
    	tableControl.getColumns().setAll(title,price,url);
    
    }
}

