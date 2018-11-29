package comp3111.webscraper;
import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Item class to contain each element of the scraped website
 * @author kennethid
 */
public class Item  {
	private String title ; 
	private Double price ;
	private String url ;
	private String origin;
	private LocalDateTime postedDate;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * Helper method to get the String object, origin
	 * @author kenneth-id
	 * @return A String object, origin
	 */
	public String getOrigin() {
		return origin;
	}
	
	/**
	 * Helper method to set the String object origin 
	 * @author kenneth-id
	 * @param origin - String object to be set as origin of the Item 
	 */
	public void setOrigin(String origin) {
		this.origin=origin;
	}
	
	/**
	 * Helper method to get the LocalDateTime object postedDate 
	 * @author kenneth-id
	 * @return LocalDateTime object posted date
	 */
	public LocalDateTime getPostedDate() {
		return postedDate;
	}
	
	/**
	 * Helper method to set postedDate attribute 
	 * @author kenneth-id
	 * @param postedDate LocalDateTime object to be set as the attribute postedDate 
	 */
	public void setPostedDate(LocalDateTime postedDate) {
		this.postedDate= postedDate;
	}
	
	/**
	 * Helper method to get an Arraylist of Item objects from the list of lists ItemLists 
	 * @author kenneth-id
	 * @param index - integer that specifies the index you want to get 
	 * @return The ArrayList of Items specified by the index, returns -1 if String is not found
	 * @throws IndexOutOfBoundsException - if the index is out of range (index < 0 || index >= size())
	 */
	
	/**
	 * Comparator object to sort the result from webscraper by price ascending, if the price is the same, craiglist items are listed first 
	 * @author kenneth-id
	 */
	public static Comparator<Item> COMPARE_BY_Price = new Comparator<Item>() {
        public int compare(Item one, Item other) {
            int value1 =one.price.compareTo(other.price);
            if(value1==0) {
            	int value2 = other.origin.compareTo(one.origin);
            	return value2;
            }
            else {
            return value1;
            }
        }
    };
}
