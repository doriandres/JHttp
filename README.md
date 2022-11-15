# JHttp
Http Client for Java and Android

## Usage example (JDK 17):
```java
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.dojami.droide.net.JHttp;
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
