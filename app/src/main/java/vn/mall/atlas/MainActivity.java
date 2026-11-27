package vn.mall.atlas;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;
import androidx.lifecycle.ViewModelProvider;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.ComponentActivity;

public class MainActivity extends ComponentActivity {
    static final int INK = 0xff183e35, PAPER = 0xfffcfbf6, SOFT = 0xffdfebe3;
    VisitModel model;
    LinearLayout details;
    ScrollView scroll;
    TextView summary;
    Button add;
    LinearLayout summaryRow;
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
        model = new ViewModelProvider(this).get(VisitModel.class);
        map = new MallMapView(this, model, this::refresh);
        root.addView(map, new LinearLayout.LayoutParams(-1, 0, 1));
        buildSheet();
        refresh();
    }
    private void buildSheet() {
        summaryRow = row();
        summaryRow.setPadding(dp(14), 0, dp(12), 0);
        summary = text("", 15, INK, true);
        summary.setMaxLines(2);
        grow(summaryRow, summary);
        summary.setOnClickListener(v -> toggleSheet());
        summary.setOnTouchListener(new View.OnTouchListener() {
            float down;
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN: down = event.getRawY(); return true;
                    case MotionEvent.ACTION_UP:
                        float dy = event.getRawY() - down;
                        if (Math.abs(dy) > ViewConfiguration.get(MainActivity.this).getScaledTouchSlop()) {
                            model.expanded = dy < 0;
                            refresh();
                        } else v.performClick();
                        return true;
                    default: return true;
                }
            }
        });
        add = action("Thêm điểm", () -> { model.addSelected(); refresh(); });
        summaryRow.addView(add, -2, dp(42));
        root.addView(summaryRow, -1, dp(62));
        scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(14), 0, dp(12), dp(8));
        scroll.addView(details);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 0.55f));
    }
    private void toggleSheet() {
        model.expanded = !model.expanded;
        refresh();
    }
    private void refresh() {
        map.invalidate();
        Mall.Shop shop = model.selected;
        String count = model.route.size() + " điểm ghé";
        String handle = model.expanded ? "▾  " : "▴  ";
        String info = shop == null ? "Lộ trình tham quan" : shop.id + " · " + shop.name;
        summary.setText(handle + info + "\n" + (shop == null ? "" : Mall.STATUS[shop.status] + "  ·  ") + count);
        summary.setContentDescription((model.expanded ? "Thu gọn" : "Mở rộng") + " lộ trình. " + summary.getText());
        add.setVisibility(shop == null ? View.GONE : View.VISIBLE);
        boolean visited = shop != null && model.route.contains(shop.id);
        add.setText(visited ? "Đã thêm ✓" : "Thêm điểm");
        add.setEnabled(!visited);
        scroll.setVisibility(model.expanded ? View.VISIBLE : View.GONE);
        details.removeAllViews();
        if (!model.expanded) return;
        if (shop != null) details.addView(text("Khu " + Mall.ZONES[shop.zone] + "  ·  Tầng 01", 14, INK, false));
        if (model.route.isEmpty()) {
            details.addView(text("Chạm một gian trên sơ đồ rồi chọn Thêm điểm.", 15, INK, false), -1, dp(50));
        }
        for (String id : model.route) {
            Mall.Shop stop = model.byId(id);
            LinearLayout line = row();
            TextView link = text(stop.id + "   " + stop.name, 16, INK, true);
            link.setMinimumHeight(dp(48));
            link.setContentDescription("Định vị " + stop.id);
            link.setOnClickListener(v -> {
                model.selected = stop;
                map.locate(stop);
                refresh();
            });
            grow(line, link);
            Button remove = action("Gỡ", () -> {
                model.route.remove(stop.id);
                refresh();
            });
            remove.setContentDescription("Gỡ " + stop.id);
            line.addView(remove, -2, dp(40));
            details.addView(line, -1, dp(50));
        }
    }
    private void legend() {
        new AlertDialog.Builder(this).setTitle("Chú thích sơ đồ").setMessage(
                "Xanh ngọc  ·  Mở cửa\nVàng, dấu chấm  ·  Sắp mở\nXám, nét chéo  ·  Tạm đóng\nViền xanh lam  ·  Đang xem\nChấm xanh lam  ·  Điểm ghé")
                .setPositiveButton("Đóng", null).show();
    }
}
