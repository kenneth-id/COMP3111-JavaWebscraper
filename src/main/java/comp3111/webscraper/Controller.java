/**
 * 
 */
package comp3111.webscraper;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Hyperlink;
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

import java.awt.*;
import javafx.application.HostServices;


import javafx.util.Callback;
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
    private TableView<Item> tableControl;

    @FXML
    private TableColumn title;

    @FXML
    private TableColumn price;

    @FXML
    private TableColumn<Item, String> url;

    @FXML
    private TableColumn postedDate;
    
    @FXML
    private Button refineID;

    @FXML
    private Button goID;
    
    @FXML
    private TextField textFieldKeywordRefine;
    
    private String beforeRefine; // to store keyword before refining
    
    private List<Item> result;
    
    private WebScraper scraper;
    
    
    
    /**
     * Default controller
     */
    public Controller() {
    	scraper = new WebScraper();
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
    	System.out.println("actionSearch: " + textFieldKeyword.getText());
    	result = scraper.scrape(textFieldKeyword.getText());
    	String output = "";
    	for (Item item : result) {
    		output += item.getTitle() + "\t" + item.getPrice() + "\t" + item.getUrl() + "\n";
    	}
    	textAreaConsole.setText(output);
    	
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
    	
    	tableControl.setItems(data);
    	tableControl.getColumns().setAll(title,price,url);
    
    }
}

