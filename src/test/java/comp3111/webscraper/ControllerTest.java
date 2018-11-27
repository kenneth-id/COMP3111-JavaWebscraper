package comp3111.webscraper;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import org.junit.BeforeClass;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//import org.testfx.framework.junit.ApplicationTest;
//
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextField;
//import javafx.scene.control.TextArea;

import com.gargoylesoftware.htmlunit.javascript.host.Console;

public class ControllerTest {
	private Controller c;
	static List<Item> result;
	private static WebScraper scraper;

	public Item createDummyItem(String origin, double price, String title, LocalDateTime time) {
		Item item = new Item();
		item.setOrigin(origin);
		item.setPostedDate(time);
		item.setPrice(price);
		item.setTitle(title);
		return item;
	}
	
	@Before
	public void setUp() {
		c = new Controller ();
	}
	
	@BeforeClass
	public static void setUpSearchResult() {
		scraper = new WebScraper();
		result = scraper.scrape("watch");
	}
	
	@Test
	public void testDefaultConstructor()throws Exception {
		assertNotNull(c);
	}
	
	@Test
	public void testAddToLastFiveSearches() throws Exception{
		c.addToLastFiveSearches("1");
		c.addToLastFiveSearches("2");
		c.addToLastFiveSearches("3");
		c.addToLastFiveSearches("4");
		c.addToLastFiveSearches("5");
		c.addToLastFiveSearches("6");
		assertEquals(5,c.getLastFiveSearches().size());		
	}
	
	@Test
	public void testAddToLastFiveResults() throws Exception{
		for(int i=0;i<6;i++) {
			c.addToLastFiveResults(result);
		}
		assertEquals(5,c.getLastFiveResults().size());	
	}
	
	@Test
	public void testAddToLastFiveTrends() throws Exception{
		Trend trend = new Trend(result);
		for(int i=0;i<6;i++) {
			c.addToLastFiveTrends(trend);
		}
		assertEquals(5,c.getLastFiveTrends().size());
	}

	
	@Test
	public void checkHelper_hasNextItem() throws Exception {
		Item item = createDummyItem("Craigslist", 0.0, "Dummy item for test", LocalDateTime.now());
		List<Item> itemTest = new ArrayList<Item>();
		itemTest.add(item);
		//Controller c = new Controller();
		
		Iterator<Item> iter = itemTest.listIterator();
				
		assertEquals(iter.hasNext(), true);
	}
	
	@Test
	public void checkHelper_checkCorrectTitle() throws Exception {
		Item item = createDummyItem("Craigslist", 0.0, "Dummy item for test", LocalDateTime.now());
		List<Item> itemTest = new ArrayList<Item>();
		itemTest.add(item);
		//Controller c = new Controller();
	
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
		//Controller c = new Controller();
	
		itemTest = c.findTitleWithRefineKeyword(itemTest, "randomText");
		
		//c.refineSearch();
		
		assertEquals(itemTest.isEmpty(), true);
	}
	
	@Test
	public void testSummary() {
		
		//cheapest item
		Item item1 = createDummyItem("Craigslist", 10.0, "Dummy item for test 1", LocalDateTime.MIN);
		item1.setUrl("http://www.google.com");
		
		Item item2 = createDummyItem("Craigslist", 12.0, "Dummy item for test 2", LocalDateTime.MIN);
		
		//latest item
		Item item3 = createDummyItem("Craigslist", 14.0, "Dummy item for test 3", LocalDateTime.now());
		item3.setUrl("http://www.yahoo.com");
		
		Item item4 = createDummyItem("Craigslist", 16.0, "Dummy item for test 4", LocalDateTime.MIN);
		
		List<Item> itemsTest = new ArrayList<Item>();
		
		itemsTest.add(item1);
		itemsTest.add(item2);
		itemsTest.add(item3);
		itemsTest.add(item4);
		
		c.getSummaryData(itemsTest);
		
		assertEquals(c.average, "13.0");
		assertEquals(c.latestUrl, "http://www.yahoo.com");
		assertEquals(c.lowUrl, "http://www.google.com");
		assertEquals(c.min, "10.0");
		assertEquals(c.totalcount, "4");
	}

	@Test
	public void checkUpdateConsole() throws Exception {
		Item item = createDummyItem("Craigslist", 0.0, "Dummy item for test", LocalDateTime.now());
		List<Item> itemTest = new ArrayList<Item>();
		itemTest.add(item);
		
		String output = "";
		String output2 = "";
		for(Item i : itemTest) {
			output += i.getTitle() + "\t" + i.getPrice() +	 "\t" + i.getOrigin() + "\t" +i.getUrl() + "\n";
			
			output2 = c.printItemAttributes(i);
		}
		
		assertEquals(output,output2);
	}
	
//	@Test
//	public void checkUpdateConsoleMainFunction() throws Exception {
//		Item item1 = createDummyItem("Craigslist", 0.0, "Dummy item 1 for test", LocalDateTime.now());
//		Item item2 = createDummyItem("Craigslist", 0.0, "Dummy item 2 for test", LocalDateTime.now());
//
//		List<Item> itemTest = new ArrayList<Item>();
//		itemTest.add(item1);
//		itemTest.add(item2);
//		
//		c.updateConsole(itemTest);
//		
//		assertNotNull(itemTest);
//	}

	@After
	public void tearDown() throws Exception{
		c =null;
	}

	
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
