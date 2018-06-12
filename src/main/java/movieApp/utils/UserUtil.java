package movieApp.utils;

import movieApp.model.user.User;

import java.util.Map;
import java.util.UUID;

public final class UserUtil {

    public static User getUser(Map<String, User> users, String token){
//        if(!users.containsKey(userId))
//            users.put(userId, new User(userId));

        return users.get(token);
    }

    public static void setMovieStatus(User user, String title, Boolean status) {
        user.setMovieStatus(title, status);
    }

    public static String getUniqueToken() {
        return UUID.randomUUID().toString();
    }
}