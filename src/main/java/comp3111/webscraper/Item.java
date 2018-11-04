package comp3111.webscraper;
import java.util.Comparator;


public class Item  {
	private String title ; 
	private Double price ;
	private String url ;
	private String origin;
	
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
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin=origin;
	}
	
	
	public static Comparator<Item> COMPARE_BY_Price = new Comparator<Item>() {
        public int compare(Item one, Item other) {
            return one.price.compareTo(other.price);
        }
    };
    
    public static Comparator<Item> COMPARE_BY_Origin = new Comparator<Item>() {
        public int compare(Item one, Item other) {
            return other.origin.compareTo(one.origin);
        }
    };
	

}
