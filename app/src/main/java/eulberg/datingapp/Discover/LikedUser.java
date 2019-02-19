package eulberg.datingapp.Discover;

public class LikedUser {

    private String likedUserID;

    /**
     * Konstruktor
     * @param likedUserID
     */
    public LikedUser(String likedUserID){ this.likedUserID = likedUserID; }

    public LikedUser(){}

    /**
     * sondierende Methode
     * @return ID
     */
    public String getLikedUserID() {
        return likedUserID;
    }

    /**
     * verändernde Methode
     * @param likedUserID
     */
    public void setLikedUserID(String likedUserID) {
        this.likedUserID = likedUserID;
    }
}
