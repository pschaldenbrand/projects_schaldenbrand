import org.json.*;
import java.io.*;

public class OpenReview {
	public static void main ( String [] args ) throws IOException {
		File file = new File("");

        Object object = jsonParser.parse(new FileReader(file));

        jsonObject = (JSONObject) object;

	}
	
	
}