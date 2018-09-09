package model;

/**
 * Created by Benny on 9/8/2018.
 */

public class Review {

    private String id;
    private String author;
    private String content;

    public Review(String id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return " id: " + id +
                " author: " + author +
                " content: " + content;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Review)) {
            return false;
        }

        Review other = (Review) obj;

        return other.getId().equals(id) &&
                other.getAuthor().equals(author) &&
                other.getContent().equals(content);
    }
}
