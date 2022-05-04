package kr.goldenmine.flowerserver.article;

import java.util.LinkedList;
import java.util.List;

public class Article {
    private int id;
    private String authorId;
    private String title;
    private String context;
    private List<Integer> imageIds;
    private List<Comment> comments;

    private transient final Object commentsKey = new Object();

    public Article(int id, String authorId, String title, String context, List<Integer> imageIds, List<Comment> comments) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.context = context;
        this.imageIds = imageIds;
        this.comments = comments;
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

    public int getRootParentComment(int start) {
        for(int i = start; i >= 0; i--) {
            if(!comments.get(i).isInserted()) return i;
        }

        return -1;
    }

    public int getLastInsertedComment(int start) {
        for(int i = start + 1; i < comments.size(); i++) {
            if(!comments.get(i).isInserted()) return i - 1;
        }

        return comments.size();
    }

    public void writeComment(int parentId, Comment comment) {
        synchronized (commentsKey) {
            if (comments == null) comments = new LinkedList<>();

            if (parentId > -1) {
                comments.add(parentId, comment);
            } else {
                comments.add(comment);
            }
        }
    }
}
