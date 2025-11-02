package models;

public class Series implements Media {
    private int id;
    private String title;
    private String director;
    private String genre;
    private int year;
    private float rating;

    // Getter/Setter
    @Override
    public int getId() { return id; }
    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public String getTitle() { return title; }
    @Override
    public void setTitle(String title) { this.title = title; }

    @Override
    public String getDirector() { return director; }
    @Override
    public void setDirector(String director) { this.director = director; }

    @Override
    public String getGenre() { return genre; }
    @Override
    public void setGenre(String genre) { this.genre = genre; }

    @Override
    public int getYear() { return year; }
    @Override
    public void setYear(int year) { this.year = year; }

    @Override
    public float getRating() { return rating; }
    @Override
    public void setRating(float rating) { this.rating = rating; }
}
