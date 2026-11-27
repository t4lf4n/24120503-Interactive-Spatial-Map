package vn.mall.atlas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

final class MallMapView extends View {
    final Mall mall;
    private final Paint brush = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Typeface bold = Typeface.create("sans-serif-medium", Typeface.NORMAL);
    private static final int[] FILLS = {0xffb3d9c6, 0xfff2d294, 0xffd2d4d2};
    private static final int INK = 0xff153d34;
    MallMapView(Context context, Mall mall) {
        super(context);
        this.mall = mall;
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
        float scale = Math.min(getWidth() / (float) Mall.WIDTH, getHeight() / (float) Mall.HEIGHT);
        c.save();
        c.translate((getWidth() - Mall.WIDTH * scale) / 2, (getHeight() - Mall.HEIGHT * scale) / 2);
        c.scale(scale, scale);
        for (int z = 0; z < 4; z++) {
            int x = Mall.zx(z), y = Mall.zy(z);
            fill(0xfffdfcf6);
            c.drawRoundRect(x, y, x + 302, y + 397, 9, 9, brush);
            fill(INK);
            c.drawRoundRect(x + 12, y + 11, x + 44, y + 43, 6, 6, brush);
            label(c, ZONE_CODES[z], x + 21, y + 35, 22, 0xffffffff);
            label(c, Mall.ZONES[z], x + 55, y + 35, 21, INK);
        }
        for (Mall.Shop shop : mall.shops) drawShop(c, shop);
        c.restore();
    }
    private static final String[] ZONE_CODES = {"A", "B", "C", "D"};
    private void drawShop(Canvas c, Mall.Shop shop) {
        int x = shop.x, y = shop.y;
        fill(FILLS[shop.status]);
        c.drawRoundRect(x, y, x + Mall.Shop.W, y + Mall.Shop.H, 4, 4, brush);
        label(c, shop.id, x + 5, y + 20, 13, INK);
        fill(INK);
        if (shop.status == 1) c.drawCircle(x + 44, y + 6, 2.4f, brush);
        if (shop.status == 2) {
            brush.setStrokeWidth(2);
            c.drawLine(x + 40, y + 9, x + 46, y + 3, brush);
        }
    }
}
