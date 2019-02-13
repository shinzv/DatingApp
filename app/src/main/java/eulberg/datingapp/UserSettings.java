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

    public UserSettings(long age, String description, String gender, String username) {
        this.age = age;
        this.description = description;
        this.gender = gender;
        this.username = username;
        //this.phoneNumber = phoneNumber;
    }

    public UserSettings() {

    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /*public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }*/



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

    public String getCity() {
        return city;
    }

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
