package movieApp.jsonUtil;

import movieApp.utils.JsonUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JsonUtilTest {

    @Test
    public void getParsedFieldNonExistingTest() throws JSONException {
        String s = "{\"Title\":\"Matrix\",\"Year\":\"1993–\",\"Rated\":\"N/A\",\"Released\":\"01 Mar 1993\"," +
                "\"Runtime\":\"60 min\",\"Genre\":\"Action, Drama, Fantasy\",\"Director\":\"N/A\"," +
                "\"Writer\":\"Grenville Case\",\"Actors\":\"Nick Mancuso, Phillip Jarrett, Carrie-Anne Moss, " +
                "John Vernon\"}";

        JSONObject jsonObject = new JSONObject(s);

        String result = JsonUtil.getParsedField(jsonObject, "Production");
        assertTrue(result.equals(""));
    }

    @Test
    public void getParsedFieldExistingTest() throws JSONException {
        String s = "{\"Title\":\"Matrix\",\"Year\":\"1993–\",\"Rated\":\"N/A\",\"Released\":\"01 Mar 1993\"," +
                "\"Runtime\":\"60 min\",\"Genre\":\"Action, Drama, Fantasy\",\"Director\":\"N/A\"," +
                "\"Writer\":\"Grenville Case\",\"Actors\":\"Nick Mancuso, Phillip Jarrett, Carrie-Anne Moss, " +
                "John Vernon\"}";

        JSONObject jsonObject = new JSONObject(s);

        String result = JsonUtil.getParsedField(jsonObject, "Title");
        assertTrue(result.equals("Matrix"));
    }

    @Test
    public void getParsedArrayNonExistingTest() throws JSONException {
        String s = "{\"Title\":\"Matrix\",\"Year\":\"1993–\",\"Rated\":\"N/A\",\"Released\":\"01 Mar 1993\"," +
                "\"Runtime\":\"60 min\",\"Genre\":\"Action, Drama, Fantasy\",\"Director\":\"N/A\"," +
                "\"Writer\":\"Grenville Case\",\"Actors\":\"Nick Mancuso, Phillip Jarrett, Carrie-Anne Moss, " +
                "John Vernon\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"8.4/10\"}]}";

        JSONObject jsonObject = new JSONObject(s);

        JSONArray result = JsonUtil.getParsedJsonArray(jsonObject, "RandomArray");
        assertTrue(result.equals(new JSONArray()));
    }

    @Test
    public void getParsedArrayExistingTest() throws JSONException {
        String s = "{\"Title\":\"Matrix\",\"Year\":\"1993–\",\"Rated\":\"N/A\",\"Released\":\"01 Mar 1993\"," +
                "\"Runtime\":\"60 min\",\"Genre\":\"Action, Drama, Fantasy\",\"Director\":\"N/A\"," +
                "\"Writer\":\"Grenville Case\",\"Actors\":\"Nick Mancuso, Phillip Jarrett, Carrie-Anne Moss, " +
                "John Vernon\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"8.4/10\"}]}";

        JSONObject jsonObject = new JSONObject(s);

        JSONArray result = JsonUtil.getParsedJsonArray(jsonObject, "Ratings");
        JSONArray expected = jsonObject.getJSONArray("Ratings");
        assertTrue(result.equals(expected));
    }
}
