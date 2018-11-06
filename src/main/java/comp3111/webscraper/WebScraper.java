package comp3111.webscraper;

import java.io.File;
import java.io.FileWriter;
import java.net.URLEncoder;
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
//TODO: how to handle a range of item price

/**
 * WebScraper provide a sample code that scrape web content. After it is constructed, you can call the method scrape with a keyword, 
 * the client will go to the default url and parse the page by looking at the HTML DOM.  
 * <br/>
 * In this particular sample code, it access to craigslist.org. You can directly search on an entry by typing the URL
 * <br/>
 * https://newyork.craigslist.org/search/sss?sort=rel&amp;query=KEYWORD
 *  <br/>
 * where KEYWORD is the keyword you want to search.
 * <br/>
 * Assume you are working on Chrome, paste the url into your browser and press F12 to load the source code of the HTML. You might be freak
 * out if you have never seen a HTML source code before. Keep calm and move on. Press Ctrl-Shift-C (or CMD-Shift-C if you got a mac) and move your
 * mouse cursor around, different part of the HTML code and the corresponding the HTML objects will be highlighted. Explore your HTML page from
 * body &rarr; section class="page-container" &rarr; form id="searchform" &rarr; div class="content" &rarr; ul class="rows" &rarr; any one of the multiple 
 * li class="result-row" &rarr; p class="result-info". You might see something like this:
 * <br/>
 * <pre>
 * {@code
 *    <p class="result-info">
 *        <span class="icon icon-star" role="button" title="save this post in your favorites list">
 *           <span class="screen-reader-text">favorite this post</span>
 *       </span>
 *       <time class="result-date" datetime="2018-06-21 01:58" title="Thu 21 Jun 01:58:44 AM">Jun 21</time>
 *       <a href="https://newyork.craigslist.org/que/clt/d/green-star-polyp-gsp-on-rock/6596253604.html" data-id="6596253604" class="result-title hdrlnk">Green Star Polyp GSP on a rock frag</a>
 *       <span class="result-meta">
 *               <span class="result-price">$15</span>
 *               <span class="result-tags">
 *                   pic
 *                   <span class="maptag" data-pid="6596253604">map</span>
 *               </span>
 *               <span class="banish icon icon-trash" role="button">
 *                   <span class="screen-reader-text">hide this posting</span>
 *               </span>
 *           <span class="unbanish icon icon-trash red" role="button" aria-hidden="true"></span>
 *           <a href="#" class="restore-link">
 *               <span class="restore-narrow-text">restore</span>
 *               <span class="restore-wide-text">restore this posting</span>
 *           </a>
 *       </span>
 *   </p>
 *}
 *</pre>
 * <br/>
 * The code 
 * <pre>
 * {@code
 * List<?> items = (List<?>) page.getByXPath("//li[@class='result-row']");
 * }
 * </pre>
 * extracts all result-row and stores the corresponding HTML elements to a list called items. Later in the loop it extracts the anchor tag 
 * &lsaquo; a &rsaquo; to retrieve the display text (by .asText()) and the link (by .getHrefAttribute()). It also extracts  
 * 
 *
 */
public class WebScraper {

	private static final String CRAIGLIST_DEFAULT_URL = "https://newyork.craigslist.org/";
	private static final String CAROUSELL_DEFAULT_URL = "https://hk.carousell.com/";
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

				// It is possible that an item doesn't have any price, we set the price to 0.0
				// in this case
				String itemPrice = spanPrice == null ? "0.0" : spanPrice.asText();

				Item item = new Item();
				item.setTitle(itemAnchor.asText());
				item.setUrl(CRAIGLIST_DEFAULT_URL + itemAnchor.getHrefAttribute());

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
				
				Item item = new Item();
				item.setTitle(itemTitle.asText());
				item.setUrl(CAROUSELL_DEFAULT_URL + itemAnchor.getHrefAttribute());
				String itemPrice = spanPrice == null ? "0.0" : spanPrice.asText();
				itemPrice=itemPrice.replace(",", ""); //for commas in item price
				itemPrice=itemPrice.replace("HK$", "");
				Double finalPrice= new Double(itemPrice);
				finalPrice= finalPrice/7.8; //converting HKD to USD
				
				
				item.setPrice(finalPrice);
				item.setOrigin("Carousell");
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
