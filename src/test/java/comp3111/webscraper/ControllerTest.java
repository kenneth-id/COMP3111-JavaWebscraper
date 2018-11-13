package comp3111.webscraper;


import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;

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
