package kr.goldenmine.flowerserver.article;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Article {
    private int id;
    private String authorId;
    private String title;
    private String context;
    private Set<String> plusId;
    private int imageCount;
    private List<Comment> comments;

    private transient final Object commentsKey = new Object();
    private transient final Object plusKey = new Object();

    public Article(int id, String authorId, String title, String context, int imageCount, List<Comment> comments) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.context = context;
        this.imageCount = imageCount;
        this.comments = comments;

        this.plusId = new HashSet<>();

//        ConcurrentHashMap
    }

    public boolean addPlus(String id) {
        synchronized (plusKey) {
            return plusId.add(id);
        }
    }

    public boolean removePlus(String id) {
        synchronized (plusKey) {
            return plusId.remove(id);
        }
    }

    public boolean isPlus(String id) {
        return plusId.contains(id);
    }

    public int getPlusCount() {
        return plusId.size();
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


    @Override
    public String toString() {
        return "id: " + id + ", title" + title + ", context: " + context + ", imageCounts: " + imageCount + ", " + "comments: " + comments;
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

    public int getImageCount() {
        return imageCount;
    }
}
