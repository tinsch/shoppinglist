package mobanwendungen.shoppinglist.remotedatabase;

/**
 * Created by l on 11.01.17.
 */

public class Query {

    private String title;
    private String category;
    private String description;

    public Query(String title, String category, String description){
        this.title = title;
        this.category = category;
        this.description = description;
    }

    @Override
    public String toString() {
        return "('"+ title + "', '" + category + "', '" + description + "');";
    }

}
