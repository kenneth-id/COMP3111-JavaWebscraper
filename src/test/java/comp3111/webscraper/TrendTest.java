package comp3111.webscraper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.List;

public class TrendTest {
	private static WebScraper scraper;
	private static Trend trend;
	static List<Item> result;
	
	
	@BeforeClass
	public static void setUp() throws Exception{
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
	public void testGetDateIndexValid() throws Exception {
		assertEquals(3,trend.getDateIndex(trend.getDatesString().get(3)));
	}
	// Im not sure if this one is stable yet.
	@Test
	public void testGetDateIndexNotValid() throws Exception {
		assertEquals(-1,trend.getDateIndex("random"));
	}
	
	@Test
	public void testGetItemList() throws Exception{
		assertNotNull(trend.getItemList(0));
	}
	
	@Test
	public void testGetDatesString() throws Exception {
		assertNotNull(trend.getDatesString());
	}
	
	@AfterClass
	public static void teardown() throws Exception {
		scraper=null;
		trend=null;
		result=null;
	}
}
