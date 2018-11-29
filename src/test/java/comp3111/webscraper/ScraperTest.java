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
import com.gargoylesoftware.htmlunit.javascript.host.Console;

public class ScraperTest {

	private WebScraper scraper;
	
	@Before
	public void setUp() {
		scraper = new WebScraper ();
	}
	
	@Test
	public void notFound() {
		List<Item> result = scraper.scrape("alsdfjklasfjdsjaflsafjljfdsa");
		
		assertEquals(0, result.size());
	}
	
	@Test
	public void foundMoreThan5() {
		List<Item> result = scraper.scrape("watch");
		
		// 600 from Craiglist (120 per page, 5 pages total), 40 from carousell
		assertEquals(result.size(), 640);
	}

	@Test
	public void foundLessThan5() {
		List<Item> result = scraper.scrape("scrape");
		
		assertTrue(result.size() < 640);
	}
	
	
}
