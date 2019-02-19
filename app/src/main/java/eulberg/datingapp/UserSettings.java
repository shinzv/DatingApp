package eulberg.datingapp;

import java.io.Serializable;

public class UserSettings implements Serializable {

    private long age;
    private String description;
    private String gender;
    private String username;
    private String city;
    //private double latitude;
    //private double longtitude;
    // private String phoneNumber;

    /**
     * Konstruktor
     * @param age alter
     * @param description Beschreibung
     * @param gender Geschlecht
     * @param username Usernamen
     */
    public UserSettings(long age, String description, String gender, String username) {
        this.age = age;
        this.description = description;
        this.gender = gender;
        this.username = username;
    }

    /**
     * Dieser leere Konstruktor wird für Firebase benötigt
     */
    public UserSettings() {

    }

    /**
     * sondierende Methode
     * @return age jetziges Alter
     */
    public long getAge() {
        return age;
    }

    /**
     * verändernde Methode
     * @param age neue Alter
     */
    public void setAge(long age) {
        this.age = age;
    }

    /**
     * sondierende Methode
     * @return description derzeitige Beschreibung
     */
    public String getDescription() {
        return description;
    }

    /**
     * verändernde Methode
     * @param description neue Beschreibung
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * sondierende Methode
     * @return gender derzeitiges Geschlecht
     */
    public String getGender() {
        return gender;
    }

    /**
     * verändernde Methode
     * @param gender neues Geschlecht
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * sondierende Methode
     * @return username derzeitiger username
     */
    public String getUsername() {
        return username;
    }

    /**
     * verändernde Methode
     * @param username neuer Benutzername
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /*public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }*/


    /**
     *
     * @return String, der die Attribute dieser Klasse als einen Text darstellt.
     */
    @Override
    public String toString() {
        return "UserSettings{" +
                "age=" + age +
                ", description='" + description + '\'' +
                ", gender='" + gender + '\'' +
                ", username='" + username + '\'' +
                //", phonenumber='" + phoneNumber + '\'' +
                '}';
    }

    /**
     * sondierende Methode
     * @return Stadtnamen
     */
    public String getCity() {
        return city;
    }

    /**
     * verändernde Methode
     * @param city neue Stadtname
     */
    public void setCity(String city) {
        this.city = city;
    }

    /*public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }*/
}
