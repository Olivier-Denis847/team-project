package entity;

//Available categories to an entry
// To be honest I want to make this a Java file but I'm having trouble incorporating filtering function

public class Category {
    private String categoryName;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // ★ ADD THIS so your new code works without breaking old code
    public String getName() {
        return categoryName;
    }
}





