package kr.goldenmine.flowerserver.profile;

import java.util.ArrayList;
import java.util.List;

public class Profile implements Cloneable {
    private String id;
    private String password;
    private String imageUrl;
    private String rank;
    private String introduction;
    private List<Integer> articleIds;

    private transient final Object articleIdsKey;

    public Profile() {
        articleIdsKey = new Object();
    }

    public Profile(String id, String password, String imageUrl, String rank, String introduction, List<Integer> articleIds) {
        articleIdsKey = new Object();

        this.id = id;
        this.password = password;
        this.imageUrl = imageUrl;
        this.rank = rank;
        this.introduction = introduction;
        this.articleIds = articleIds;
    }

    @Override
    protected Profile clone() throws CloneNotSupportedException {
        Profile profile = new Profile();
        profile.id = id;
        profile.password = password;
        profile.imageUrl = imageUrl;
        profile.rank = rank;
        profile.introduction = introduction;
        if(articleIds != null) {
            profile.articleIds = new ArrayList<>(articleIds);
        } else {
            profile.articleIds = null;
        }
        return profile;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getRank() {
        return rank;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getIntroduction() {
        return introduction;
    }

    public List<Integer> getArticleIds() {
        return new ArrayList<>(articleIds);
    }

    public void addNewArticleId(int id) {
        synchronized (articleIdsKey) {
            if(articleIds == null) articleIds = new ArrayList<>();
            articleIds.add(id);
        }
    }

    void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "id: " + id + ", password: " + password + ", imageUrl: " + imageUrl + ", rank: " + rank + ", introduction" + introduction + ", articleIds: " + articleIds;
    }
}
