package user;

import org.apache.commons.lang3.RandomStringUtils;


public class User {
    private final String email;
    private final String password;
    private String name;
    private String accessToken;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static User getRandomUser() {
        return new User(RandomStringUtils.randomAlphabetic(5) + "@ru.ru", "12345678", RandomStringUtils.randomAlphabetic(10));
    }

    public static User getWithoutLogin() {
        return new User("", "pass", RandomStringUtils.randomAlphabetic(10));
    }

    public static User getWithoutPassword() {
        return new User(RandomStringUtils.randomAlphanumeric(10), "", RandomStringUtils.randomAlphabetic(10));
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
