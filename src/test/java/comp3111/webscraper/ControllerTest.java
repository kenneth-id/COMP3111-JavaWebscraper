package comp3111.webscraper;


import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ControllerTest {
	
	private Controller c;
	
	@Before
	public void setUp() {
		c = new Controller ();
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
		WebScraper scraper = new WebScraper();
		List<Item> result = scraper.scrape("shoe");
		for(int i=0;i<6;i++) {
			c.addToLastFiveResults(result);
		}
		assertEquals(5,c.getLastFiveResults().size());	
	}
	
	@Test
	public void testAddToLastFiveTrends() throws Exception{
		WebScraper scraper = new WebScraper();
		List<Item> result = scraper.scrape("shoe");
		Trend trend = new Trend(result);
		for(int i=0;i<6;i++) {
			c.addToLastFiveTrends(trend);
		}
		assertEquals(5,c.getLastFiveTrends().size());
	}

	public Item createDummyItem(String origin, double price, String title, LocalDateTime time) {
		Item item = new Item();
		item.setOrigin(origin);
		item.setPostedDate(time);
		item.setPrice(price);
		item.setTitle(title);
		return item;
	}
	
	@Test
	public void checkHelper_hasNextItem() {
		Item item = createDummyItem("Craigslist", 0.0, "Dummy item for test", LocalDateTime.now());
		List<Item> itemTest = new ArrayList<Item>();
		itemTest.add(item);
		Controller c = new Controller();
		
		Iterator<Item> iter = itemTest.listIterator();
				
		assertEquals(iter.hasNext(), true);
	}
	
	@Test
	public void checkHelper_checkCorrectTitle() {
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
	public void checkRefining() {
		Item item = createDummyItem("Craigslist", 0.0, "Dummy item for test", LocalDateTime.now());
		List<Item> itemTest = new ArrayList<Item>();
		itemTest.add(item);
		Controller c = new Controller();
	
		itemTest = c.findTitleWithRefineKeyword(itemTest, "randomText");
				
		assertEquals(itemTest.isEmpty(), true);
	}
	
	@After
	public void tearDown() throws Exception{
		c =null;
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
