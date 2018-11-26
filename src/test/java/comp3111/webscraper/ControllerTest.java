package comp3111.webscraper;
	

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
//import org.testfx.framework.junit.ApplicationTest;
//
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextField;
//import javafx.scene.control.TextArea;


public class ControllerTest {

	public Item createDummyItem(String origin, double price, String title, LocalDateTime time) {
		Item item = new Item();
		item.setOrigin(origin);
		item.setPostedDate(time);
		item.setPrice(price);
		item.setTitle(title);
		return item;
	}
	
	@Test
	public void checkHelper_hasNextItem() throws Exception {
		Item item = createDummyItem("Craigslist", 0.0, "Dummy item for test", LocalDateTime.now());
		List<Item> itemTest = new ArrayList<Item>();
		itemTest.add(item);
		Controller c = new Controller();
		
		Iterator<Item> iter = itemTest.listIterator();
				
		assertEquals(iter.hasNext(), true);
	}
	
	@Test
	public void checkHelper_checkCorrectTitle() throws Exception {
		Item item = createDummyItem("Craigslist", 0.0, "Dummy item for test", LocalDateTime.now());
		List<Item> itemTest = new ArrayList<Item>();
		itemTest.add(item);
		Controller c = new Controller();
	
		itemTest = c.findTitleWithRefineKeyword(itemTest, "Dummy");
				
		assertEquals(itemTest.isEmpty(), false);
	}

//	@Test
//	public void checkHelper_checkFalseTitle() {
//		Item item = createDummyItem("Craigslist", 0.0, "Dummy item for test", LocalDateTime.now());
//		List<Item> itemTest = new ArrayList<Item>();
//		itemTest.add(item);
//		Controller c = new Controller();
//	
//		itemTest = c.checkIfTitleIsTheSameAsText(itemTest, "randomText");
//				
//		assertEquals(itemTest.isEmpty(), true);
//	}
	
	@Test
	public void checkRefining() throws Exception {
		Item item = createDummyItem("Craigslist", 0.0, "Dummy item for test", LocalDateTime.now());
		List<Item> itemTest = new ArrayList<Item>();
		itemTest.add(item);
		Controller c = new Controller();
	
		itemTest = c.findTitleWithRefineKeyword(itemTest, "randomText");
		
		//c.refineSearch();
		
		assertEquals(itemTest.isEmpty(), true);
	}
	
	@Test
	public void checkUpdateConsole() throws Exception {
		Item item = createDummyItem("Craigslist", 0.0, "Dummy item for test", LocalDateTime.now());
		List<Item> itemTest = new ArrayList<Item>();
		itemTest.add(item);
		Controller c = new Controller();
		
		String output = "";
		String output2 = "";
		for(Item i : itemTest) {
			output += i.getTitle() + "\t" + i.getPrice() +	 "\t" + i.getOrigin() + "\t" +i.getUrl() + "\n";
			
			output2 = c.printItemAttributes(i);
		}
		
		assertEquals(output,output2);
	}
	
//	
//	@Test
//	public void checkHelper_checkTitle() {
//		Item item = createDummyItem("Craigslist", 0.0, "Dummy item for test", LocalDateTime.now());
//	
//		List<Item> itemTest = null;
//		itemTest.add(item);
//		
//		Controller c = new Controller();
//		c.checkIfTitleIsTheSameAsText(itemTest, "Dummy");
//		
//		assertEquals(itemTest.isEmpty(), false);
//	}
//	

}
