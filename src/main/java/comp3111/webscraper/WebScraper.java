package comp3111.webscraper;

import java.io.File;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Collections;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import java.util.Vector;
//TODO: sorting
//TODO: get PostedDate

public class WebScraper {

	private static final String CRAIGLIST_DEFAULT_URL = "https://newyork.craigslist.org/";
	private static final String CAROUSELL_DEFAULT_URL = "https://hk.carousell.com/";
	private static final DateTimeFormatter craiglistFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//	private static final DateTimeFormatter finalFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	
	private WebClient client;

	/**
	 * Default Constructor 
	 */
	public WebScraper() {
		client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
//		System.out.println(client.getBrowserVersion().getApplicationName()); print to get browser version		
	}

	/**
	 * The only method implemented in this class, to scrape web content from the craigslist
	 * 
	 * @param keyword - the keyword you want to search
	 * @return A list of Item that has found. A zero size list is return if nothing is found. Null if any exception (e.g. no connectivity)
	 */
	public List<Item> scrape(String keyword) {

		try {
			
			String searchCraiglistUrl = CRAIGLIST_DEFAULT_URL + "search/sss?sort=rel&query=" + URLEncoder.encode(keyword, "UTF-8");
			HtmlPage craiglistPage = client.getPage(searchCraiglistUrl);
			client.waitForBackgroundJavaScriptStartingBefore(50000);


			
			List<?> craiglistItems = (List<?>) craiglistPage.getByXPath("//li[@class='result-row']");
			System.out.println("size of craiglistItems list= " + craiglistItems.size());		
			
			Vector<Item> result = new Vector<Item>();
			
			//this loop is for craiglist items
			for (int i = 0; i < craiglistItems.size(); i++) {
				HtmlElement htmlItem = (HtmlElement) craiglistItems.get(i);
				HtmlAnchor itemAnchor = ((HtmlAnchor) htmlItem.getFirstByXPath(".//p[@class='result-info']/a"));
				HtmlElement spanPrice = ((HtmlElement) htmlItem.getFirstByXPath(".//a/span[@class='result-price']"));
				HtmlElement itemPostedDate = ((HtmlElement) htmlItem.getFirstByXPath("./p/time"));
				// It is possible that an item doesn't have any price, we set the price to 0.0
				// in this case
				String itemPrice = spanPrice == null ? "0.0" : spanPrice.asText();
				
				String postedDateString=itemPostedDate.getAttribute("datetime");
				LocalDateTime finalPostedDate=LocalDateTime.parse(postedDateString,craiglistFormatter);
				Item item = new Item();
				item.setTitle(itemAnchor.asText());
				item.setUrl(CRAIGLIST_DEFAULT_URL + itemAnchor.getHrefAttribute());
				item.setPostedDate(finalPostedDate);
				item.setPrice(new Double(itemPrice.replace("$", "")));
				item.setOrigin("Craiglist");

				result.add(item);
			}
			
			
			String searchCarousellUrl = CAROUSELL_DEFAULT_URL + "search/products/?query=" + URLEncoder.encode(keyword, "UTF-8");
			System.out.println(searchCarousellUrl);
			HtmlPage carousellPage = client.getPage(searchCarousellUrl);
			client.waitForBackgroundJavaScriptStartingBefore(50000);
			
			WebResponse response = carousellPage.getWebResponse();
			String content = response.getContentAsString();
			File debug= new File("/home/kenneth/git/carousell_debug.html");
			debug.createNewFile();
			
			if(!debug.exists()) { 
		                debug.createNewFile();
		            }
		    FileWriter fw = new FileWriter(debug);
		    fw.write(content);
		    fw.close();		    
		    
			List<?> carousellItems = (List<?>) carousellPage.getByXPath("//*[@id=\"root\"]/div/div[1]/div[1]/div[2]/div[2]/div[4]/div[1]/div");
			System.out.println("size of carousellItems list= " + carousellItems.size());
			for (int i = 0; i < carousellItems.size(); i++) {
				HtmlElement htmlItem = (HtmlElement) carousellItems.get(i);
				HtmlAnchor itemAnchor = ((HtmlAnchor) htmlItem.getFirstByXPath("./div/figure/div/figcaption/a"));
				HtmlElement spanPrice = ((HtmlElement) htmlItem.getFirstByXPath("./div//figure/div/figcaption/a/div[2]/div[1]"));
				HtmlElement itemTitle = ((HtmlElement) htmlItem.getFirstByXPath("./div//figure/div/figcaption/a/div[1]/div"));
				HtmlElement itemPostedOffset = ((HtmlElement) htmlItem.getFirstByXPath("./div/figure/div/a/div[2]/time/span"));
				
				Item item = new Item();
				String offsetString= itemPostedOffset.asText();
				LocalDateTime currentDateTime= LocalDateTime.now();
				LocalDateTime finalPostedDate=currentDateTime;
				if(offsetString.contains("yesterday")) {
					finalPostedDate= currentDateTime.minusDays(1);
				}
				else if(offsetString.contains("New Carouseller")) {
					finalPostedDate=currentDateTime;
				}
				else if(offsetString.contains("last year")) {
					finalPostedDate= currentDateTime.minusYears(1);
				}
				else {
					String stringDigits= offsetString.replaceAll("\\D+","");
					int offsetAmount=0;
					
					if(!stringDigits.isEmpty()) {
						offsetAmount= Integer.parseInt(stringDigits);
					}
//					System.out.println(offsetAmount);
					
					if(offsetString.contains("hours")) {
						finalPostedDate= currentDateTime.minusHours(offsetAmount);
					}
					else if(offsetString.contains("days")) {
						finalPostedDate= currentDateTime.minusDays(offsetAmount);
					}
					else if(offsetString.contains("months")) {
						finalPostedDate= currentDateTime.minusMonths(offsetAmount);
					}
				}
				
				String itemPrice = spanPrice == null ? "0.0" : spanPrice.asText();
				itemPrice=itemPrice.replace(",", ""); //for commas in item price
				itemPrice=itemPrice.replace("HK$", "");
				Double finalPrice= new Double(itemPrice);
				finalPrice= finalPrice/7.8; //converting HKD to USD
				
				item.setTitle(itemTitle.asText());
				item.setUrl(CAROUSELL_DEFAULT_URL + itemAnchor.getHrefAttribute());
				item.setPrice(finalPrice);
				item.setOrigin("Carousell");
				item.setPostedDate(finalPostedDate);
//				System.out.println(finalPostedDate.toString());
//				System.out.println(itemAnchor.asText() + "\t" + SHOPEE_DEFAULT_URL + itemAnchor.getHrefAttribute()+"\t"+ finalPrice); //print to get item URL and price
				result.add(item);
			}
			client.close();
			//sorting by price and item origin
			Collections.sort(result,Item.COMPARE_BY_Price);
			return result;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

}
