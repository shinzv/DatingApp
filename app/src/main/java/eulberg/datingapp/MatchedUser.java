package eulberg.datingapp;

public class MatchedUser {
    String matchedUserID;

    public MatchedUser(String matchedUserID) {
        this.matchedUserID = matchedUserID;
    }

    public MatchedUser() {
    }

    public String getMatchedUserID() {
        return matchedUserID;
    }

    public void setMatchedUserID(String matchedUserID) {
        this.matchedUserID = matchedUserID;
    }
}
