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
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
    Button add, clear;
    EditText search;
    TextView result;
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
        buildSearch();
        map = new MallMapView(this, model, this::refresh);
        root.addView(map, new LinearLayout.LayoutParams(-1, 0, 1));
        buildSheet();
        refresh();
    }
    private void buildSearch() {
        LinearLayout tools = row();
        tools.setPadding(dp(12), 0, dp(12), 0);
        search = new EditText(this);
        search.setSingleLine();
        search.setTextSize(16);
        search.setHint("Tìm theo mã gian");
        search.setContentDescription("Tìm theo mã gian");
        search.setPadding(dp(12), 0, dp(6), 0);
        search.setTextColor(INK);
        search.setBackground(background(0xffeeeee7, 10));
        search.setImeOptions(EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        search.setText(model.query);
        search.setSelection(search.length());
        search.setOnEditorActionListener((v, id, event) -> {
            hideKeyboard();
            return true;
        });
        tools.addView(search, new LinearLayout.LayoutParams(0, dp(42), 1));
        clear = action("Xóa", () -> {
            search.setText("");
            hideKeyboard();
        });
        tools.addView(clear, -2, dp(40));
        Button reset = action("Toàn tầng", () -> { hideKeyboard(); map.resetView(); });
        reset.setContentDescription("Reset View · Toàn tầng");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(40));
        params.leftMargin = dp(8);
        tools.addView(reset, params);
        root.addView(tools, -1, dp(46));
        result = text("", 13, INK, false);
        result.setPadding(dp(16), 0, dp(12), 0);
        result.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        root.addView(result, -1, dp(28));
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                model.search(s.toString());
                refresh();
            }
            public void afterTextChanged(Editable s) {}
        });
    }
    private void hideKeyboard() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(search.getWindowToken(), 0);
        root.requestFocus();
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
        int matches = 0, stops = 0;
        for (Mall.Shop item : model.mall.shops) if (model.matches(item.id)) matches++;
        for (String id : model.route) if (model.matches(id)) stops++;
        result.setText(matches == 0 ? "Không tìm thấy gian phù hợp" : model.filtering()
                ? matches + " gian phù hợp" : "180 gian hàng  ·  4 khu vực");
        clear.setVisibility(model.query.isEmpty() ? View.GONE : View.VISIBLE);
        String count = (model.filtering() ? stops + "/" : "") + model.route.size() + " điểm ghé";
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
        if (!model.route.isEmpty() && stops == 0)
            details.addView(text("Không có điểm ghé phù hợp.", 15, INK, false), -1, dp(48));
        for (String id : model.route) {
            if (!model.matches(id)) continue;
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
