package comp3111.webscraper;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ComboBox;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;


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
    
    private ArrayList<Trend> lastFiveTrends;
    
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
    	lastFiveTrends = new ArrayList<Trend>();
    }

    /**
     * Default initializer. It is empty.
     */
    @FXML
    private void initialize() {
    	//refineID.setEnabled(false)(true); // set refine button to disable on construction
    }

    private void updateAllTabs() {
    	updateTableTab();
    }
    
    
    /**
     * Called when the search button is pressed.
     */
    @FXML
    private void actionSearch() {
    	String searchKeyWord =textFieldKeyword.getText();
    	System.out.println("actionSearch: " + searchKeyWord);
    	
    	
    	comboBoxTrend.setItems(lastFiveSearches);
    	System.out.println("Begin scraping");
    	result = scraper.scrape(searchKeyWord);
    	System.out.println("Finished scraping");
    	Trend searchTrend = new Trend (result);
    	if(!lastFiveSearches.contains(searchKeyWord)) {
	    	addToLastFiveResults(result);
	    	addToLastFiveTrends(searchTrend);
	    	addToLastFiveSearches(searchKeyWord);
    	}
    	
    	updateTrendChart(searchTrend,searchKeyWord);
//    	
//    	String output = "Items scraped from craiglist and carousell (Currency in USD) \n ";
//    	for (Item item : result) {
//    		output += item.getTitle() + "\t" + item.getPrice() +	 "\t" 
//    	+ item.getOrigin() +	 "\t" +item.getUrl() + "\n";
//    	}
//    	textAreaConsole.setText(output);
    	
    	updateConsole(result);

    	beforeRefine = textFieldKeyword.getText();
    	refineID.setEnabled(true);
    	updateAllTabs();
    }
    
    @FXML
    /**
	 * Called when the Value property of the combobox in the Trend tab is changed.
	 * @author kenneth-id
	 */
    void trendComboBoxAction(ActionEvent event) {
    	String comboString = comboBoxTrend.getValue();
    	System.out.println(comboString);
    	int index = lastFiveSearches.indexOf(comboString);
    	Trend comboTrend = lastFiveTrends.get(index);
    	updateTrendChart(comboTrend,comboString);
    	updateConsole(lastFiveResults.get(index));
    }
    
    /**
	 * Helper method to update the chart in the Trend tab 
	 * @author kenneth-id
	 * @param searchTrend - Trend object 
	 * @param searchKeyWord - String of the searched keyword 
	 */
    private void updateTrendChart(Trend searchTrend, String searchKeyWord) {
    	//remove previous linechart
    	areaChartTrend.getData().clear();
    	XYChart.Series<String, Number> averagePricesSeries = new XYChart.Series<String, Number>();
    	averagePricesSeries.setName("The average selling price of the " + searchKeyWord);
    	int numberOfPoints=0;
    	for(int i=0; i<7;i++) {
    		if(!(searchTrend.getAveragePricesList().get(i).equals(0.0))) {
    		Data<String,Number> point =new Data<String, Number>(searchTrend.getDatesString().get(i), 
    				searchTrend.getAveragePricesList().get(i));
    		averagePricesSeries.getData().add(point);
    		numberOfPoints++;
    		}
    	}
    	areaChartTrend.getData().addAll(averagePricesSeries);
    	final int numberOfPointsFinal = numberOfPoints;
    	//adding double click event handler to each point
    	for(int i=0; i<numberOfPointsFinal;i++) {
    		Data<String, Number> currentDataPoint =areaChartTrend.getData().get(0).getData().get(i);
    		Node currentNode =  currentDataPoint.getNode();
    		currentNode.addEventHandler(MouseEvent.MOUSE_PRESSED,
        		    new EventHandler<MouseEvent>() {
    		        @Override 
    		        public void handle(MouseEvent mouseEvent) {
    		                 if(mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2){
    		                	 
    		                	 ArrayList<Node> bluePoints = new ArrayList<Node>();
    		                	 for(int i=0 ; i<numberOfPointsFinal ;i++) {
    		                		 
    		                		 Node currentNode =  areaChartTrend.getData().get(0).getData().get(i).getNode();
    		                		 if((currentNode.getStyle() == "-fx-background-color: blue;")){
    		                			 bluePoints.add(currentNode);
        		                     }
    		                	 }
    		                	 for(Node bluePoint : bluePoints) {
    		                		 bluePoint.setStyle("");
    		                	 }
    		                	 if(!currentNode.getStyle().equals("-fx-background-color: blue;") ) {
    		                     currentNode.setStyle("-fx-background-color: blue;");
    		                     int dateIndex = searchTrend.getDateIndex(currentDataPoint.getXValue());
    		                     updateConsole(searchTrend.getItemList(dateIndex));
    		                	 }
    		                 }
    		         }
    		    });
    	}
    }
    
    /**
     * Called when the new button is pressed. Very dummy action - print something in the command prompt.
     */
    @FXML
    private void actionNew() {
    	System.out.println("actionNew");
    }    
    
    private void updateConsole(List<Item> result) {
    	System.out.println("Items: \n");
    	String output = "";
    	for (Item item : result) {
    		output += item.getTitle() + "\t" + item.getPrice() +	 "\t" 
    				+ item.getOrigin() +	 "\t" +item.getUrl() + "\n";
    	}
    	
    	textAreaConsole.setText(output);
    }
    
    /**
	 * Helper method to add a String object to the ArrayList lastFiveSearches 
	 * @author kenneth-id
	 * @param toAdd  String object to be added into the ArrayList 
	 */
    private void addToLastFiveSearches (String toAdd) {
    	if(lastFiveSearches.size()<5 ) {
    		lastFiveSearches.add(toAdd);
    	}
    	else {
    		lastFiveSearches.remove(0);
    		lastFiveSearches.add(toAdd);
    	}
    }
    
    /**
	 * Helper method to add a List of Items to the ArrayList lastFiveResults 
	 * @author kenneth-id
	 * @param toAdd  List of Item objects to be added into the ArrayList 
	 */
    private void addToLastFiveResults (List<Item> toAdd ) {
    	if(lastFiveSearches.size()<5) {
    		lastFiveResults.add(toAdd);
    	}
    	else {
    		lastFiveResults.remove(0);
    		lastFiveResults.add(toAdd);
    	}
    }
    
    /**
	 * Helper method to add a Trend object to the ArrayList lastFiveTrends 
	 * @author kenneth-id
	 * @param toAdd  Trend object to be added into the ArrayList 
	 */
    private void addToLastFiveTrends (Trend toAdd ) {
    	if(lastFiveSearches.size()<5) {
    		lastFiveTrends.add(toAdd);
    	}
    	else {
    		lastFiveTrends.remove(0);
    		lastFiveTrends.add(toAdd);
    	}
    }
    

    @FXML
    private void mouseClicked() {
    	if(textAreaConsole.getText().isEmpty()) {
    		refineID.setEnabled(false);
    		return;
    	}
    }
    
    private List<Item> findTitleWithRefineKeyword(List<Item> result, String text) {

		Iterator<Item> iter = result.listIterator();
		while(iter.hasNext()) {
			Item item = iter.next();
			if (item.getTitle().toLowerCase().contains(text.toLowerCase())==false) {    				
				iter.remove();
			}
		}
    	
    	return result;
    }
    
    @FXML
    private void refineSearch() {
    	//System.out.println("actionSearch: " + textFieldKeyword.getText());
    	
    	//if(textFieldKeyword.getText().isEmpty() && textFieldKeywordRefine.getText().isEmpty()) 
    	if(textAreaConsole.getText().isEmpty()) {
    		refineID.setEnabled(false);
    		return;
    	}

    	if(refineID.isEnabled()==true) {
    		result = scraper.scrape(beforeRefine);
    		// update result items
    		result = findTitleWithRefineKeyword(result, textFieldKeywordRefine.getText());
    		
	    	//textAreaConsole.setText(printConsole(result));
	    	
    		updateConsole(result);
    		updateAllTabs();
    	}
    	
    	refineID.setEnabled(false);
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
    private void updateTableTab() {
    	tableControl.setEditable(false);
    	
    	ObservableList<Item> data = FXCollections.observableArrayList(result);
    	
    	title.setCellValueFactory(new PropertyValueFactory<Item,String>("title"));
    	price.setCellValueFactory(new PropertyValueFactory<Item,Double>("price"));
    	url.setCellValueFactory(new PropertyValueFactory<Item,String>("url"));
    	url.setCellFactory(new HyperlinkCell());
    	postedDate.setCellValueFactory(new PropertyValueFactory<Item,LocalDateTime>("postedDate"));
    	
    	// Set Data to Table Control
    	tableControl.setItems(data);
    	
    	// Set all columns with data
    	tableControl.getColumns().setAll(title,price,url,postedDate);
    
    }
}

