package hello;

import java.util.Map;

public class UserUtil {

    protected static User getUser(Map<String, User> users, String userId){
        if(!users.containsKey(userId))
            users.put(userId, new User(userId));

        return users.get(userId);
    }

    protected static void setMovieStatus(User user, String title, Boolean status) {
        user.setMovieStatus(title, status);
    }
}
