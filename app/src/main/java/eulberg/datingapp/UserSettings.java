package eulberg.datingapp;

public class UserSettings {

    private long age;
    private String description;
    private String gender;
    private String username;
    private String profile_picture;

    public UserSettings(long age, String description, String gender, String username, String profile_picture) {
        this.age = age;
        this.description = description;
        this.gender = gender;
        this.username = username;
        this.profile_picture = profile_picture;
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

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "age=" + age +
                ", description='" + description + '\'' +
                ", gender='" + gender + '\'' +
                ", username='" + username + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                '}';
    }
}
