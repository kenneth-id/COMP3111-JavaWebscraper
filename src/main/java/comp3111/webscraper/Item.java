package comp3111.webscraper;
import java.time.LocalDateTime;
import java.util.Comparator;


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
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin=origin;
	}
	
	public LocalDateTime getPostedDate() {
		return postedDate;
	}
	
	public void setPostedDate(LocalDateTime postedDate) {
		this.postedDate= postedDate;
	}
	
	
	
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
    
    public static Comparator<Item> COMPARE_BY_Origin = new Comparator<Item>() {
        public int compare(Item one, Item other) {
            return other.origin.compareTo(one.origin);
        }
    };
}
