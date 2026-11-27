package vn.mall.atlas;

import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.ComponentActivity;

public class MainActivity extends ComponentActivity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView title = new TextView(this);
        title.setText("Atlas · Sơ đồ tầng");
        setContentView(title);
    }
}
