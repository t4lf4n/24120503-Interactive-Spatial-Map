package vn.mall.atlas;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.ComponentActivity;

public class MainActivity extends ComponentActivity {
    static final int INK = 0xff183e35, PAPER = 0xfffcfbf6, SOFT = 0xffdfebe3;
    final Mall mall = new Mall();
    LinearLayout root;
    MallMapView map;

    int dp(int n) { return Math.round(n * getResources().getDisplayMetrics().density); }

    TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }
    GradientDrawable background(int color, int radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(radius));
        return shape;
    }
    Button action(String value, Runnable click) {
        Button b = new Button(this);
        b.setText(value);
        b.setTextColor(INK);
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setMinWidth(0);
        b.setMinimumWidth(0);
        b.setPadding(dp(12), 0, dp(12), 0);
        b.setBackground(background(SOFT, 10));
        b.setOnClickListener(v -> click.run());
        return b;
    }
    LinearLayout row() {
        LinearLayout line = new LinearLayout(this);
        line.setGravity(Gravity.CENTER_VERTICAL);
        return line;
    }
    void grow(LinearLayout row, View child) {
        row.addView(child, new LinearLayout.LayoutParams(0, -2, 1));
    }
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(PAPER);
        root.setFocusableInTouchMode(true);
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets.consumeSystemWindowInsets();
        });
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(root);
        LinearLayout head = row();
        head.setPadding(dp(16), 0, dp(12), 0);
        grow(head, text("Atlas  /  Tầng 01", 24, INK, true));
        head.addView(action("Chú thích", this::legend), -2, dp(40));
        root.addView(head, -1, dp(56));
        map = new MallMapView(this, mall);
        root.addView(map, new LinearLayout.LayoutParams(-1, 0, 1));
    }
    private void legend() {
        new AlertDialog.Builder(this).setTitle("Chú thích sơ đồ").setMessage(
                "Xanh ngọc  ·  Mở cửa\nVàng, dấu chấm  ·  Sắp mở\nXám, nét chéo  ·  Tạm đóng")
                .setPositiveButton("Đóng", null).show();
    }
}
