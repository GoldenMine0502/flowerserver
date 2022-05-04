package kr.goldenmine.flowerserver.article;

import java.util.List;

public class Article {
    private int id;
    private String authorId;
    private String title;
    private String context;
    private List<Integer> imageIds;

    public Article(int id, String authorId, String title, String context, List<Integer> imageIds) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.context = context;
        this.imageIds = imageIds;
    }

    public int getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getTitle() {
        return title;
    }

    public String getContext() {
        return context;
    }

    public List<Integer> getImageIds() {
        return imageIds;
    }

    @Override
    public String toString() {
        return "id: " + id + ", title" + title + ", context: " + context + ", imageIds: " + imageIds;
    }
}