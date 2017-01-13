package mobanwendungen.shoppinglist.remotedatabase;

/**
 * Created by l on 11.01.17.
 */

public class OwnQuery {

    private String title;
    private String category;
    private String description;

    public OwnQuery(String title, String category, String description){
        this.title = title;
        this.category = category;
        this.description = description;
    }

    @Override
    public String toString() {
        return "('"+ category + "', '" + title + "', '" + description + "');";
    }

}
