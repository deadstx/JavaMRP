package models;

public interface Media {
    int getId();
    void setId(int id);

    String getTitle();
    void setTitle(String title);

    String getDirector();
    void setDirector(String director);

    String getGenre();
    void setGenre(String genre);

    int getYear();
    void setYear(int year);

    float getRating();
    void setRating(float rating);

}
