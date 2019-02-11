package eulberg.datingapp;

import java.io.Serializable;

public class User implements Serializable {

    //Referenzen
    private String user_id;
    private String username;
    private String email;

    /**
     * Instanziierung: User wird benutzt, um die Daten der Nutzer online zu speichern und lokalt damit zu arbeiten. usw.
     * @param user_id
     * @param username
     * @param email
     */
    public User(String user_id, String username, String email) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
    }

    public User() {

    }

    /**
     * sondierende Methode
     * @return userID
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * sondierende Methode
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * sondierende Methode
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * verändernde Methode
     * @param user_id userID
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * verändernde Methode
     * @param username Benutzername
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * verändernde Methode
     * @param email E-Mail-Adresse
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * gibt alle Daten als zusammengefassten String zurück
     * @return Ergebnis-String.
     */
    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
