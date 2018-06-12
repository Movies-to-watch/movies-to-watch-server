package movieApp.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUtil {

    public static String getParsedField(JSONObject obj, String field) {
        try {
            return obj.getString(field);
        } catch (Exception ex) {
            return "";
        }
    }

    public static JSONArray getParsedJsonArray(JSONObject obj, String field) {
        try {
            return obj.getJSONArray(field);
        } catch (Exception ex) {
            return new JSONArray();
        }
    }
}
