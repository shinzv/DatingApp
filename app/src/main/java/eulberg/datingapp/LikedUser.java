package eulberg.datingapp;

public class LikedUser {

    private String likedUserID;

    public LikedUser(String likedUserID){ this.likedUserID = likedUserID; }

    public LikedUser(){}

    public String getLikedUserID() {
        return likedUserID;
    }

    public void setLikedUserID(String likedUserID) {
        this.likedUserID = likedUserID;
    }
}
