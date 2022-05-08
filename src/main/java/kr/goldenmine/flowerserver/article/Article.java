package kr.goldenmine.flowerserver.article;

import java.util.ArrayList;
import java.util.List;

public class Article {
    private int id;
    private String authorId;
    private String title;
    private String context;
    private List<Integer> imageIds;
    private List<Integer> commentIds;

    private transient final Object commentsKey = new Object();

    public Article(int id, String authorId, String title, String context, List<Integer> imageIds, List<Integer> commentIds) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.context = context;
        this.imageIds = imageIds;
        this.commentIds = commentIds;
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

    public List<Integer> getCommentIds() {
        return commentIds;
    }

    @Override
    public String toString() {
        return "id: " + id + ", title" + title + ", context: " + context + ", imageIds: " + imageIds + ", comments: " + commentIds;
    }


    public void addNewCommentId(int index, int commentId) {
        synchronized (commentsKey) {
            if (commentIds == null) commentIds = new ArrayList<>();

            commentIds.add(index, commentId);
        }
    }



}
