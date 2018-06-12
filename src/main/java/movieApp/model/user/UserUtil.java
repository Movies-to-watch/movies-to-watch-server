package movieApp.model.user;

import java.util.Map;

public final class UserUtil {

    public static User getUser(Map<String, User> users, String userId){
        if(!users.containsKey(userId))
            users.put(userId, new User(userId));

        return users.get(userId);
    }

    public static void setMovieStatus(User user, String title, Boolean status) {
        user.setMovieStatus(title, status);
    }
}