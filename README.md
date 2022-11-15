# JHttp
Non blocking Http Client for Java and Android

## HTTP GET example (JDK 17 + Android):
```java
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.yourcompany.yourproject.utils.JHttp;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            TextView textView = findViewById(R.id.greeting);

            JHttp.Client client = new JHttp.Client();
            JHttp.Response response = client.send(JHttp.Method.GET, "https://dog.ceo/api/breeds/image/random");

            if (response.getStatus() == 200) {
                JSONObject json = response.getBodyAsJson();
                textView.setText(json.getString("message"));
            } else {
                textView.setText("Status: " + response.getStatus());
            }

            response.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
        }
    }
}
```

## HTTP POST example
```java
try {
    TextView textView = findViewById(R.id.greeting);

    JHttp.Client client = new JHttp.Client();

    HashMap<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
        
    String jsonToPost = "{ \"title\":\"My test post\", \"content\":\"<h1>Hello World!</h1>\" }";

    JHttp.Response response = client.send(JHttp.Method.POST, "https://myservice.com/api/v1/post/create", headers, jsonToPost);

    if (response.getStatus() == 200) {
        JSONObject jsonResponse = response.getBodyAsJson();
        textView.setText(jsonResponse.getString("result"));
    } else {
        textView.setText("Status: " + response.getStatus());
    }
    
    response.close();
} catch (Exception e) {
    Log.e(getClass().getName(), e.getMessage());
}
```
