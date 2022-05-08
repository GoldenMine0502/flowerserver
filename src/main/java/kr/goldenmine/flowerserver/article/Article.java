package kr.goldenmine.flowerserver.article;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Article {
    private int id;
    private String authorId;
    private String title;
    private String context;
    private Set<String> plusId;
    private int imageCount;
    private List<Integer> commentIds;

    private transient final Object commentsKey = new Object();
    private transient final Object plusKey = new Object();

    public Article(int id, String authorId, String title, String context, int imageCounts, List<Integer> commentIds) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.context = context;
        this.imageCount = imageCount;
        this.commentIds = commentIds;
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

    public int getImageCount() {
        return imageCount;
    }

    public List<Integer> getCommentIds() {
        return commentIds;
    }

    @Override
    public String toString() {
        return "id: " + id + ", title" + title + ", context: " + context + ", imageCounts: " + imageCount + ", " + "comments: " + commentIds;
    }


    public void addNewCommentId(int index, int commentId) {
        synchronized (commentsKey) {
            if (commentIds == null) commentIds = new ArrayList<>();

            commentIds.add(index, commentId);
        }
    }



}
