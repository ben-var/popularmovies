package com.example.android.moviesone;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

/**
 * Movie model class if data is to be cached into the App's memory for fast access time.
 *
 * The instance variables are based off the JSONObject returned according to themoviedb.org
 * documentation: https://developers.themoviedb.org/3/discover/movie-discover
 */
@Entity(tableName = "favorites")
public class Movie implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int favoriteId;

    private String posterPath;
    private boolean adult;
    private String overview;
    private String releaseDate;
    private List<Integer> genreIdArray;
    private int movieId;
    private String originalTitle;
    private String originalLanguage;
    private String title;
    private String backdropPath;
    private double popularity;
    private int voteCount;
    private boolean video;
    private double voteAverage;

    /**
     * Constructor that should contain every single value inside the JSON object
     * returned from a query to TheMovieDB. Error handling will need to be taken care of
     * where the object is being instantiated.
     *
     * @param posterPath
     * @param adult
     * @param overview
     * @param releaseDate
     * @param genreIdArray
     * @param movieId
     * @param originalTitle
     * @param originalLanguage
     * @param title
     * @param backdropPath
     * @param popularity
     * @param voteCount
     * @param video
     * @param voteAverage
     */
    @Ignore
    public Movie(String posterPath, boolean adult, String overview, String releaseDate,
                 List<Integer> genreIdArray, int movieId, String originalTitle, String originalLanguage,
                 String title, String backdropPath, double popularity, int voteCount,
                 boolean video, double voteAverage) {

        this.setPosterPath(posterPath);
        this.setAdult(adult);
        this.setOverview(overview);
        this.setReleaseDate(releaseDate);
        this.setGenreIdArray(genreIdArray);
        this.setMovieId(movieId);
        this.setOriginalTitle(originalTitle);
        this.setOriginalLanguage(originalLanguage);
        this.setTitle(title);
        this.setBackdropPath(backdropPath);
        this.setPopularity(popularity);
        this.setVoteCount(voteCount);
        this.setVideo(video);
        this.setVoteAverage(voteAverage);
    }

    /**
     * Constructor that allows for favoriteId to be used by Room when the favorites table
     * is generated.
     *
     * @param favoriteId
     * @param posterPath
     * @param adult
     * @param overview
     * @param releaseDate
     * @param genreIdArray
     * @param movieId
     * @param originalTitle
     * @param originalLanguage
     * @param title
     * @param backdropPath
     * @param popularity
     * @param voteCount
     * @param video
     * @param voteAverage
     */
    public Movie(int favoriteId, String posterPath, boolean adult, String overview, String releaseDate,
                 List<Integer> genreIdArray, int movieId, String originalTitle, String originalLanguage,
                 String title, String backdropPath, double popularity, int voteCount,
                 boolean video, double voteAverage) {

        this.setPosterPath(posterPath);
        this.setAdult(adult);
        this.setOverview(overview);
        this.setReleaseDate(releaseDate);
        this.setGenreIdArray(genreIdArray);
        this.setMovieId(movieId);
        this.setOriginalTitle(originalTitle);
        this.setOriginalLanguage(originalLanguage);
        this.setTitle(title);
        this.setBackdropPath(backdropPath);
        this.setPopularity(popularity);
        this.setVoteCount(voteCount);
        this.setVideo(video);
        this.setVoteAverage(voteAverage);
    }

    /** Setters and getters for each variable of the Movie class */

    public int getFavoriteId() { return favoriteId; }

    public void setFavoriteId(int inFavoriteId) {
        favoriteId = inFavoriteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String inTitle) {
        title = inTitle;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double inPopularity) {
        this.popularity = inPopularity;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int inMovieId) {
        this.movieId = inMovieId;
    }

    public boolean getAdult() {
        return adult;
    }

    public void setAdult(boolean inAdult) {
        this.adult = inAdult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String inBackdropPath) {
        this.backdropPath = inBackdropPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String inPosterPath) {
        this.posterPath = inPosterPath;
    }

    public List<Integer> getGenreIdArray() {
        return genreIdArray;
    }

    public void setGenreIdArray(List<Integer> inGenreIdArray) {
        this.genreIdArray = inGenreIdArray;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String inOverview) {
        this.overview = inOverview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String inReleaseDate) {
        this.releaseDate = inReleaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String inOriginalTitle) {
        this.originalTitle = inOriginalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String inOriginalLanguage) {
        this.originalLanguage = inOriginalLanguage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int inVoteCount) {
        this.voteCount = inVoteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean inVideo) {
        this.video = inVideo;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double inVoteAverage) {
        this.voteAverage = inVoteAverage;
    }

    /**
     * Simple toString method used for debugging. Should not be used in user facing
     * portion of app without refactoring.
     *
     * @return Simple information about the Movie object.
     */
    @Override
    public String toString() {
        return "Poster Path: " + posterPath + "\n" +
                "Adult: " + adult + "\n" +
                "Overview: " + overview + "\n" +
                "Release Date: " + releaseDate + "\n" +
                "Genre IDs: " + genreIdArray + "\n" +
                "Movie ID: " + movieId + "\n" +
                "Original Title: " + originalTitle + "\n" +
                "Original Language: " + originalLanguage + "\n" +
                "Title: " + title + "\n" +
                "Backdrop Path: " + backdropPath + "\n" +
                "Popularity: " + popularity + "\n" +
                "Vote Count: " + voteCount + "\n" +
                "Video: " + video + "\n" +
                "Vote Average: " + voteAverage;
    }

    /**
     * Overridden equals method specifically for Movies. This method ONLY checks values
     * that are unlikely to be changed over the long run, as things such as vote count
     * and popularity are subject to constant fluctuations.
     * @param obj object to compare to within this instance
     * @return whether the obj is the same movie or not, represented by a boolean
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Movie)) {
            return false;
        }

        Movie other = (Movie) obj;

        return other.getMovieId() == movieId &&
                other.getReleaseDate().equals(releaseDate) &&
                other.getTitle().equals(title) &&
                other.originalLanguage.equals(originalLanguage);
    }


}

