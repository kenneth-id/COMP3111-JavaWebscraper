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
import javafx.scene.control.Button;

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
//import java.awt.Button;

import javafx.application.HostServices;

import javafx.util.Callback;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public Button refineID;

    @FXML
    private Button goID;
    
    @FXML
    private TextField textFieldKeywordRefine;
    
    private String beforeRefine; // to store keyword before refining
    
    public String totalcount, min, average, lowUrl,latestUrl, latest;
    

    /**
     * Default controller
     */
    public Controller() {
    	scraper = new WebScraper();
    	lastFiveSearches = FXCollections.observableArrayList();
    	lastFiveResults= new ArrayList<List<Item>>();
    	lastFiveTrends = new ArrayList<Trend>();
    	totalcount = "";
    	min = "";
    	average = "";
    	lowUrl = "";
    	latestUrl = "";
    }

    /**
     * Default initializer. It is empty.
     */
    @FXML
    private void initialize() {
    	refineID.setDisable(true); // set refine button to disable on construction
    }

    /**
     * Updates all tabs whenever a search or refine search is called.
     * @author vajunaedi
     * @param searchKeyWord - String needed to update the updateTrendTab
     * @param result - List of items from scraper
     */
    public void updateAllTabs(String searchKeyWord, List<Item> result) {
    	updateTableTab();
    	getSummaryData(result);
    	setSummaryTab();
    	updateTrendTab(searchKeyWord, result);
    }
    
    /**
     * Called to update the trend tab
     * @author kenneth-id
     * @param searchKeyWord - String to store the previous searches
     * @param result - a list of items to store the scraped results
     */
    public void updateTrendTab(String searchKeyWord, List<Item> result) {
    	Trend searchTrend = new Trend (result);
    	if(!lastFiveSearches.contains(searchKeyWord)) {
	    	addToLastFiveResults(result);
	    	addToLastFiveTrends(searchTrend);
	    	addToLastFiveSearches(searchKeyWord);
    	}
    	updateTrendChart(searchTrend,searchKeyWord);
    }
    	
    /**
     * Called when the search button is pressed.
     * @author - vajunaedi, kennethlee-id, hskrishandi
     */
    @FXML
    private void actionSearch() {
    	String searchKeyWord = textFieldKeyword.getText();
    	System.out.println("actionSearch: " + searchKeyWord);
    	
    	comboBoxTrend.setItems(lastFiveSearches);
    	//System.out.println("Begin scraping");
    	result = scraper.scrape(searchKeyWord);

    	//System.out.println("Finished scraping");
 //   	Trend searchTrend = new Trend (result);
//    	if(!lastFiveSearches.contains(searchKeyWord)) {
 //   	if(!lastFiveSearches.contains(searchKeyWord)) {
//	    	addToLastFiveResults(result);
//	    	addToLastFiveTrends(searchTrend);
//	    	addToLastFiveSearches(searchKeyWord);
//    	}
//   	updateTrendChart(searchTrend,searchKeyWord);
    	
    	updateTrendTab(searchKeyWord, result);
    	updateConsole(result);
    	updateAllTabs(searchKeyWord, result);

    	beforeRefine = searchKeyWord;
    	refineID.setDisable(false);
    	//getSummaryData(result);
    }
    
    /**
	 * Called when the Value property of the combobox in the Trend tab is changed.
	 * @author kennethid
	 */
    @FXML
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
	 * @author kennethid
	 * @param searchTrend Trend object 
	 * @param searchKeyWord String of the searched keyword 
	 */
    public void updateTrendChart(Trend searchTrend, String searchKeyWord) {
    	//remove previous linechart
    	areaChartTrend.getData().clear();
    	XYChart.Series<String, Number> averagePricesSeries = addDataPoints(searchTrend,searchKeyWord);
    	areaChartTrend.getData().addAll(averagePricesSeries);
    	final int numberOfPointsFinal = averagePricesSeries.getData().size();
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
    		                     result = searchTrend.getItemList(dateIndex); // Vivi add - to store the current search 
    		                	 }
    		                 }
    		         }
    		    });
    	}
    }
    
    /**
	 * Helper method to add data points the chart in the Trend tab 
	 * @author kenneth-id
	 * @param searchTrend - Trend object 
	 * @param searchKeyWord - String of the searched keyword
	 * @return the Series of data points we will add to the chart 
	 */    
    public XYChart.Series<String, Number> addDataPoints(Trend searchTrend, String searchKeyWord) {
    	XYChart.Series<String, Number> averagePricesSeries = new XYChart.Series<String, Number>();
    	averagePricesSeries.setName("The average selling price of the " + searchKeyWord);
    	for(int i=0; i<7;i++) {
    		if(!(searchTrend.getAveragePricesList().get(i).equals(0.0))) {
    		Data<String,Number> point =new Data<String, Number>(searchTrend.getDatesString().get(i), 
    				searchTrend.getAveragePricesList().get(i));
    		averagePricesSeries.getData().add(point);
    		}
    	}
    	return averagePricesSeries;
    }
    
    /**
     * Called when the new button is pressed. Very dummy action - print something in the command prompt.
     */
    @FXML
    private void actionNew() {
    	System.out.println("actionNew");
    }    
    
    /**
	 * Helper method to add a String object to the ArrayList lastFiveSearches 
	 * @author kennethid
	 * @param toAdd  String object to be added into the ArrayList 
	 */
    public void addToLastFiveSearches (String toAdd) {
    	if(lastFiveSearches.size()<5) {
    		lastFiveSearches.add(toAdd);
    	}
    	else {
    		lastFiveSearches.remove(0);
    		lastFiveSearches.add(toAdd);
    	}
    }
    
    /**
	 * Helper method to add a List of Items to the ArrayList lastFiveResults 
	 * @author kennethid
	 * @param toAdd  List of Item objects to be added into the ArrayList 
	 */
    public void addToLastFiveResults (List<Item> toAdd) {
    	if(lastFiveResults.size()<5) {
    		lastFiveResults.add(toAdd);
    	}
    	else {
    		lastFiveResults.remove(0);
    		lastFiveResults.add(toAdd);
    	}
    }
    
    /**
	 * Helper method to add a Trend object to the ArrayList lastFiveTrends 
	 * @author kennethid
	 * @param toAdd  Trend object to be added into the ArrayList 
	 */
    public void addToLastFiveTrends (Trend toAdd) {
    	if(lastFiveTrends.size()<5) {
    		lastFiveTrends.add(toAdd);
    	}
    	else {
    		lastFiveTrends.remove(0);
    		lastFiveTrends.add(toAdd);
    	}
    }
    
    /**
	 * Helper method to get the ObservableList lastFiveSearches from the controller class 
	 * @author kenneth-id
	 * @return The ObservableList lastFiveSearches from the controller class 
	 */
    public ObservableList<String> getLastFiveSearches(){
    	return lastFiveSearches;
    }
    
    /**
	 * Helper method to get the ArrayList lastFiveTrends from the controller class 
	 * @author kennethid
	 * @return The ArrayList of Trend object, lastFiveTrends from the controller class 
	 */
    public ArrayList<Trend> getLastFiveTrends(){
    	return lastFiveTrends;
    }
    
    /**
	 * Helper method to get the ArrayList of List lastFiveResults from the controller class 
	 * @author kennethid
	 * @return The ArrayList of List filled with Item objects, lastFiveResults from the controller class 
	 */
    public ArrayList<List<Item>> getLastFiveResults(){
    	return lastFiveResults;
    }
   
    /**
	 * Helper method to print on console 
	 * @author vajunaedi
	 * @return item's string
	 * @param item - the Item to be converted into String
	 */ 
    public String printItemAttributes(Item item) {
    	String output = "";
    	output = item.getTitle() + "\t" + item.getPrice() +	 "\t" + item.getOrigin() + "\t" +item.getUrl() + "\n";
    	return output;
    }
    
    /**
	 * Helper method to print on console 
	 * @author kenneth-id, vajunaedi
	 * @param result - the list of items to be printed
	 */
    public void updateConsole(List<Item> result) {
    	System.out.println("Items from Craiglist and Carousell (Price in USD)");
    	String output = "";
    	for (Item item : result) {
    		output += printItemAttributes(item);
    	}
    	textAreaConsole.setText(output);
    }
    

//    /**
//	 * Additional method to ensure that on first click, the refine button is disabled
//	 * @author vajunaedi
//	 */
//    @FXML
//    private void mouseClicked() {
//    	disableRefineIDonEmptyConsole();
//    }
//    
    /**
	 * Additional method to ensure that on first click, the refine button is disabled
	 * @author vajunaedi
	 */
    private void disableRefineIDonEmptyConsole() {
    	if(result == null) {
    		refineID.setDisable(true);
    		return;
    	}
    }
    
    /**
	 * Helper function for findTitleWithRefineKeyword; as an iterator iterates through the list,
	 * it checks if a string (i.e. the item's title) contains a substring (the refine search keyword),
	 * and it removes the item which does not contain said keyword/substring
	 * @author vajunaedi
	 * @param result - the list of items to check or iterate through
	 * @param text - the keyword/substring checked 
	 * @return A refined list of item, representing those whose title contains the specified keyword/substring
	 */
    public List<Item> findTitleWithRefineKeyword(List<Item> result, String text) {
		Iterator<Item> iter = result.listIterator();
		while(iter.hasNext()) {
	    	Item item = iter.next();
			if (item.getTitle().toLowerCase().contains(text.toLowerCase())==false) {    				
				iter.remove();
			}
		}
    	return result;
    }
    

    /**
	 * Basic Task 5: Refine Search. 
	 * This task can only be done if the refine search button is enabled, and when the previous search produced some results
	 * Refine Search essentially refines a scraped list, filtering those whose titles are specified in the refine search text area
	 * After the refine search is done, all other tabs are refreshed and updated, and the refine button is disabled.
	 * @author vajunaedi
	 */
    @FXML
    public void refineSearch() {
//    	if(textAreaConsole.getText().isEmpty()) {
//    		refineID.setDisable(true);
//    		return;
//    	}
    	disableRefineIDonEmptyConsole();
    	//result = scraper.scrape(beforeRefine);
    	// Update the result lists 
    	result = findTitleWithRefineKeyword(result, textFieldKeywordRefine.getText());
    	// Console is updated with the refined results
    	updateConsole(result);
    	// Update all other tabs to the right of console
    	updateAllTabs(beforeRefine + " " + textFieldKeywordRefine.getText(), result);
    	//updateTrendTab(beforeRefine, result);
    	refineID.setDisable(true);
    }

    /**
	 * Helper class to define the URL TableColumn for Basic Task 4 - Table updates. 
	 * @author vajunaedi
	 */
	private static class HyperlinkCell implements  Callback<TableColumn<Item, String>, TableCell<Item, String>> {
		
	    /**
		 * This function help define the URL's table cell to be defined as Hyperlink, although it is initially stored as String.
		 * In addition, it is handling each cell to be opened in a new browser.
		 * @author vajunaedi
		 * @param arg - calling the TableColumn URL
		 * @return represents the TableColumn that has been set into Hyperlink 
		 */
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

    /**
	 * Basic Task 4: Table. 
	 * This function stored the scraped items content into a table. 
	 * @author vajunaedi
	 */
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
    
    /**
	 * Basic Task 1: Summary. 
	 * This function gets the total count, lowest selling price, average, and latest item from
	 * the scraped items, and put into the data members of controller
	 * @author hskrishandi
	 * @param result - the list of items to check or iterate through
	 */
    
    public void getSummaryData(List<Item> result) {
    	
    	System.out.println(result.size());
    	
    	if(result.size() == 0) {
    		min = "-";
        	average = "-";
        	totalcount = String.valueOf(result.size());
        	latestUrl = "";
        	lowUrl = "";
        	latest = "-";
        	return;
    	}
    	
    	int count = 0;
    	int priceSum = 0;
    	double avg;
    	
    	double lowest = Double.POSITIVE_INFINITY;
    	
    	LocalDateTime datelatest = LocalDateTime.MIN;
    	
    	for (Item item : result) {
    		
    		double price = item.getPrice();
    		LocalDateTime  date = item.getPostedDate();
    		String url = item.getUrl();
    		
    		if(datelatest.isBefore(date)) {
    			datelatest = date;
    			latestUrl = url;	
    		}
    		
    		if(price == 0) {
    			continue;
    		}
    			
    		if(lowest > price) {
    			lowest = price;
    			lowUrl = url;
    		}
    		
    		priceSum += price;
    		count++;
    	}
    	
    	if(count == 0) {
    		avg = 0;
    		lowest = 0;
    	} else {
    		avg = priceSum / count;
    	}
    	
    	min = String.valueOf(lowest);
    	average = String.valueOf(avg);
    	totalcount = String.valueOf(result.size());
    	latest = String.valueOf(datelatest.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
    	
    }
    
    /** 
	 * Update the summary tab in the UI using the data members, and set the hyperlinks
	 * for lowest selling price and latest item
	 * @author hskrishandi
	 */
    
    private void setSummaryTab() {
    	
    	labelCount.setText(totalcount);
    	labelPrice.setText(average);
    	labelMin.setText(min);
    	labelLatest.setText(latest);
    	
    	final String url1 = lowUrl;
    	
    	labelMin.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent e) {
    	    	
    	    	popUpLink(url1);
    	    }
    	});	
    	
    	final String url2 = latestUrl;
    	
    	labelLatest.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent e) {
    	    	
    	    	popUpLink(url2);
    	    	
    	    }
    	});
    	
    	
    }
    
    /** 
	 * Helper function to open browser when clicking hyperlink
	 * @author hskrishandi
	 * @param link - the destination link
	 */
    
    private void popUpLink(String link) {
    	
    	if(link == "")
    		return;
    	
    	Desktop d = Desktop.getDesktop();
    	URI u = URI.create(link);
    	
    	try {
			d.browse(u);
		} catch (IOException e) {
			e.printStackTrace();
		}
    
    }
}
