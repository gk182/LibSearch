package model;

public class Shelf {
    private int id;
    private String shelfName;

    // Constructors
    public Shelf() {}

    public Shelf(int id, String shelfName) {
        this.id = id;
        this.shelfName = shelfName;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShelfName() {
        return shelfName;
    }

    public void setShelfName(String shelfName) {
        this.shelfName = shelfName;
    }
}
	