package vn.mall.atlas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.View;

final class MallMapView extends View {
    private final VisitModel model;
    private final Runnable changed;
    private float tapX, tapY;
    final Viewport camera = new Viewport();
    private float lastX, lastY, downX, downY, lastSpan;
    private boolean moved;
    private final int slop;
    private final Paint brush = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Typeface bold = Typeface.create("sans-serif-medium", Typeface.NORMAL);
    private static final int[] FILLS = {0xffb3d9c6, 0xfff2d294, 0xffd2d4d2};
    private static final int INK = 0xff153d34;
    MallMapView(Context context, VisitModel model, Runnable changed) {
        super(context);
        this.model = model;
        this.changed = changed;
        slop = ViewConfiguration.get(context).getScaledTouchSlop();
        setContentDescription("Sơ đồ tương tác, bốn khu gian hàng");
        setBackgroundColor(0xfff3f2ea);
    }
    private void fill(int color) {
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(color);
    }
    private void label(Canvas c, String text, float x, float y, float size, int color) {
        fill(color);
        brush.setTypeface(bold);
        brush.setTextSize(size);
        c.drawText(text, x, y, brush);
    }
    @Override protected void onDraw(Canvas c) {
        super.onDraw(c);
        c.save();
        c.translate(camera.tx, camera.ty);
        c.scale(camera.scale, camera.scale);
        for (int z = 0; z < 4; z++) {
            int x = Mall.zx(z), y = Mall.zy(z);
            fill(0xfffdfcf6);
            c.drawRoundRect(x, y, x + 302, y + 397, 9, 9, brush);
            fill(INK);
            c.drawRoundRect(x + 12, y + 11, x + 44, y + 43, 6, 6, brush);
            label(c, ZONE_CODES[z], x + 21, y + 35, 22, 0xffffffff);
            label(c, Mall.ZONES[z], x + 55, y + 35, 21, INK);
        }
        for (Mall.Shop shop : model.mall.shops) drawShop(c, shop);
        c.restore();
    }
    @Override protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        camera.resize(width, height);
    }

    // Rebase on every pointer transition so lifting either finger cannot jump or select.
    private void sample(MotionEvent e, int skip, boolean apply) {
        int a = skip == 0 ? 1 : 0;
        int b = a + 1;
        if (b == skip) b++;
        float x = e.getX(a), y = e.getY(a), span = 0;
        if (b < e.getPointerCount()) {
            float dx = e.getX(b) - x, dy = e.getY(b) - y;
            x += dx / 2;
            y += dy / 2;
            span = (float) Math.hypot(dx, dy);
            if (apply && span > 0 && lastSpan > 0)
                camera.pinch(lastX, lastY, x, y, span / lastSpan);
        } else if (apply) {
            if (!moved && Math.hypot(x - downX, y - downY) > slop) moved = true;
            if (moved) camera.pan(x - lastX, y - lastY);
        }
        lastX = x;
        lastY = y;
        lastSpan = span;
        if (apply && moved) invalidate();
    }
    @Override public boolean onTouchEvent(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                moved = false;
                downX = e.getX();
                downY = e.getY();
                sample(e, -1, false);
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                moved = true;
                sample(e, -1, false);
                return true;
            case MotionEvent.ACTION_MOVE:
                sample(e, -1, true);
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                sample(e, e.getActionIndex(), false);
                return true;
            case MotionEvent.ACTION_UP:
                if (!moved && Math.hypot(e.getX() - downX, e.getY() - downY) <= slop)
                {
                    tapX = e.getX();
                    tapY = e.getY();
                    performClick();
                }
                return true;
            default:
                return true;
        }
    }
    @Override public boolean performClick() {
        super.performClick();
        model.tap(model.at(camera.worldX(tapX), camera.worldY(tapY)));
        changed.run();
        return true;
    }

    private static final String[] ZONE_CODES = {"A", "B", "C", "D"};
    void locate(Mall.Shop shop) {
        camera.center(shop.x + Mall.Shop.W / 2f, shop.y + Mall.Shop.H / 2f, 3);
        invalidate();
    }
    private void drawShop(Canvas c, Mall.Shop shop) {
        int x = shop.x, y = shop.y;
        fill(FILLS[shop.status]);
        c.drawRoundRect(x, y, x + Mall.Shop.W, y + Mall.Shop.H, 4, 4, brush);
        if (shop == model.selected) {
            brush.setStyle(Paint.Style.STROKE);
            brush.setStrokeWidth(3);
            brush.setColor(0xff174a91);
            c.drawRoundRect(x, y, x + Mall.Shop.W, y + Mall.Shop.H, 4, 4, brush);
        }
        if (model.route.contains(shop.id)) {
            fill(0xff174a91);
            c.drawCircle(x + 43, y + 23, 4, brush);
        }
        label(c, shop.id, x + 5, y + 20, 13, INK);
        fill(INK);
        if (shop.status == 1) c.drawCircle(x + 44, y + 6, 2.4f, brush);
        if (shop.status == 2) {
            brush.setStrokeWidth(2);
            c.drawLine(x + 40, y + 9, x + 46, y + 3, brush);
        }
    }
}
