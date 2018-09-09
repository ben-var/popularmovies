package model;

/**
 * Created by Benny on 9/7/2018.
 */

public class Trailer {
    private String id;
    private String youTubeUrlKey;
    private String name;
    private int size;
    private String site;

    public Trailer(String id, String youTubeUrlKey, String name, int size, String site) {
        this.id = id;
        this.youTubeUrlKey = youTubeUrlKey;
        this.name = name;
        this.size = size;
        this.site = site;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYouTubeUrlKey() {
        return youTubeUrlKey;
    }

    public void setYouTubeUrlKey(String youTubeUrlKey) {
        this.youTubeUrlKey = youTubeUrlKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public String toString() {
        return "id: " + id +
                "youtubeKey" + youTubeUrlKey +
                "name: " + name +
                "size: " + size +
                "site: " + site;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Trailer)) {
            return false;
        }

        Trailer other = (Trailer) obj;

        return other.getId().equals(id) &&
                other.getYouTubeUrlKey().equals(youTubeUrlKey) &&
                other.getName().equals(name) &&
                other.getSize() == size &&
                other.getSite().equals(site);
    }
}
