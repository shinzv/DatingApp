package eulberg.datingapp;

public class UserSettings {

    private long age;
    private String description;
    private String gender;
    private String username;

    public UserSettings(long age, String description, String gender, String username) {
        this.age = age;
        this.description = description;
        this.gender = gender;
        this.username = username;
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



    @Override
    public String toString() {
        return "UserSettings{" +
                "age=" + age +
                ", description='" + description + '\'' +
                ", gender='" + gender + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
