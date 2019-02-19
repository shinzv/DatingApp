package eulberg.datingapp;

public class MatchedUser {

    String matchedUserID;

    /**
     * Konstruktor: Initialisiert die Attribute der Klasse.
     * @param matchedUserID ist die UserId des gemachten Users.
     */
    public MatchedUser(String matchedUserID) {
        this.matchedUserID = matchedUserID;
    }

    /**
     * Dieser leere Konstruktor wird für Firebase benötigt
     */
    public MatchedUser() {
    }

    /**
     * sondierende Methode
     * @return matchedUserID: gibt die UserId des gematchten Users zurück.
     */
    public String getMatchedUserID() {
        return matchedUserID;
    }

    /**
     * verändernde Methode
     * @param matchedUserID neue UserId des gematchten Users
     */
    public void setMatchedUserID(String matchedUserID) {
        this.matchedUserID = matchedUserID;
    }
}
