/**
 * 
 */
package comp3111.webscraper;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
//import com.google.common.collect.EvictingQueue;
import java.util.ListIterator;
import java.awt.*;
import javafx.application.HostServices;


import javafx.util.Callback;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;




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
    
    @FXML
    private TableView<Item> tableControl;

    @FXML
    private TableColumn title;

    @FXML
    private TableColumn price;

    @FXML
    private TableColumn<Item, String> url;

    @FXML
    private TableColumn<Item, LocalDateTime> postedDate;
    
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
    	//refineID.setDisable(true); // set refine button to disable on construction
    }
    
    
    /**
     * Called when the search button is pressed.
     */
    @FXML
    private void actionSearch() {
    	String searchKeyWord = textFieldKeyword.getText();
    	System.out.println("actionSearch: " + searchKeyWord);
    	
    	comboBoxTrend.setItems(lastFiveSearches);
    	result = scraper.scrape(searchKeyWord);
    	
    	if(!lastFiveSearches.contains(searchKeyWord)) {
	    	addToLastFiveSearches(searchKeyWord);
	    	addToLastFiveResults(result);
    	}
    	updateTrendChart(result, searchKeyWord);
    	
    	String output = "Items scraped from craiglist and carousell (Currency in USD) \n ";
    	textAreaConsole.setText(output+printConsole(result)); 	
    	
    	beforeRefine = textFieldKeyword.getText();
    	refineID.setDisable(false);
    	tableTab(); // run the table tab
    	
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
    }
    

    @FXML
    private void mouseClicked() {
    	if(textAreaConsole.getText().isEmpty()) {
    		refineID.setDisable(true);
    		return;
    	}
    }
    
    //to print on console
    private String printConsole(List<Item> resultOutput) {
    	String output = "";
    	for (Item item : resultOutput) {
    		output += item.getTitle() + "\t" + item.getPrice() + "\t" + item.getOrigin() + "\t" +item.getUrl() + "\n";
    	}
    	return output;
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
    		result = scraper.scrape(beforeRefine);
    		
//    		for (Iterator<Item> iter = result.listIterator(); iter.hasNext(); ) {
//    		    Item item = iter.next();
//    		    if (item.getTitle().contains(textFieldKeywordRefine.getText())==false) {
//    		        iter.remove();
//    		    }
//    		}

    		Iterator<Item> iter = result.listIterator();
    		while(iter.hasNext()) {
    			Item item = iter.next();
    			if (item.getTitle().toLowerCase().contains(textFieldKeywordRefine.getText().toLowerCase())==false) {    				
    				iter.remove();
    			}
    		}
    		
	    	textAreaConsole.setText(printConsole(result));
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
    

	private static class HyperlinkCell implements  Callback<TableColumn<Item, String>, TableCell<Item, String>> {
	    @Override
	    public TableCell<Item, String> call(TableColumn<Item, String> arg) {
	        TableCell<Item, String> cell = new TableCell<Item, String>() {
	            @Override
	            protected void updateItem(String item, boolean empty) {
	            	Hyperlink item_casted = new Hyperlink(item);
	                setGraphic(item_casted);
	                item_casted.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							try {
								Desktop.getDesktop().browse(new URI(item));
							} catch(IOException e1) {
								e1.printStackTrace();
							} catch(URISyntaxException e1) {
								e1.printStackTrace();
							}
						}
	                	
	                });
	            }
	        };
	        return cell;
	    }
	}
    
    /** Task 4: Fill out table
     * Called when the table tab is clicked
     * **/
    @FXML
    private void tableTab() {
    	tableControl.setEditable(false);
    	
    	ObservableList<Item> data = FXCollections.observableArrayList(result);
    	
    	title.setCellValueFactory(new PropertyValueFactory<Item,String>("title"));
    	price.setCellValueFactory(new PropertyValueFactory<Item,Double>("price"));
    	url.setCellValueFactory(new PropertyValueFactory<Item,String>("url"));
    	url.setCellFactory(new HyperlinkCell());
    	postedDate.setCellValueFactory(new PropertyValueFactory<Item,LocalDateTime>("postedDate"));
    	
    	tableControl.setItems(data);
    	tableControl.getColumns().setAll(title,price,url,postedDate);
    
    }
}

