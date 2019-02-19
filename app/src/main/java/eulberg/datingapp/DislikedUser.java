package eulberg.datingapp;

public class DislikedUser {

    private String dislikedUserID;

    /**
     * Konstruktor
     * @param dislikedUserID
     */
    public DislikedUser(String dislikedUserID){ this.dislikedUserID = dislikedUserID; }

    public DislikedUser(){}

    /**
     * sondierende Methode
     * @return ID
     */
    public String getDislikedUserID() {
        return dislikedUserID;
    }

    /**
     * verändernde Methode
     * @param dislikedUserID
     */
    public void setDislikedUserID(String dislikedUserID) {
        this.dislikedUserID = dislikedUserID;
    }
}