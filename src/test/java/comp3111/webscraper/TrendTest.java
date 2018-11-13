package comp3111.webscraper;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.List;

public class TrendTest {
	private WebScraper scraper;
	private Trend trend;
	List<Item> result;
	
	
	@Before
	public void setUp() throws Exception{
		scraper = new WebScraper();
		result= scraper.scrape("watch");
		trend = new Trend(result);
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
	public void testDefaultConstructor() throws Exception{
		Trend test = new Trend();
		assertNotNull(test);
	}
	@Test
	public void parameterizedConstructor() throws Exception {
		assertNotNull(trend);
	}
	
	@Test
	public void testInitializeTrend() throws Exception {
		Trend test = new Trend();
		test.initializeTrend(result);
		assertNotNull(test);
	}
	
	@Test
	public void testGetItemList() throws Exception{
		assertNotNull(trend.getItemList(0));
	}
	
	@Test
	public void testGetDatesString() throws Exception {
		assertNotNull(trend.getDatesString());
	}
	
	@After
	public void teardown() throws Exception {
		scraper=null;
		trend=null;
		result=null;
	}
	
}
