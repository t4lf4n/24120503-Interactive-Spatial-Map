package vn.mall.atlas;

import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class VisitFlowTest {
    private ActivityScenario<MainActivity> scenario;
    private final Instrumentation device = InstrumentationRegistry.getInstrumentation();
    private final boolean demo = "true".equals(InstrumentationRegistry.getArguments().getString("demo"));

    private void pause() {
        device.waitForIdleSync();
        SystemClock.sleep(demo ? 3000 : 150);
    }
    private void touch(long down, int action, float... xy) {
        int n = xy.length / 2;
        MotionEvent.PointerProperties[] props = new MotionEvent.PointerProperties[n];
        MotionEvent.PointerCoords[] coords = new MotionEvent.PointerCoords[n];
        for (int i = 0; i < n; i++) {
            props[i] = new MotionEvent.PointerProperties();
            props[i].id = i;
            props[i].toolType = MotionEvent.TOOL_TYPE_FINGER;
            coords[i] = new MotionEvent.PointerCoords();
            coords[i].x = xy[2*i];
            coords[i].y = xy[2*i+1];
            coords[i].pressure = 1;
            coords[i].size = 1;
        }
        MotionEvent e = MotionEvent.obtain(down, SystemClock.uptimeMillis(), action, n, props,
                coords, 0, 0, 1, 1, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        device.sendPointerSync(e);
        e.recycle();
    }
    private void tap(float x, float y) {
        long down = SystemClock.uptimeMillis();
        touch(down, MotionEvent.ACTION_DOWN, x, y);
        touch(down, MotionEvent.ACTION_UP, x, y);
        pause();
    }
    private float[] point(String id, float xOffset) {
        float[] p = new float[2];
        scenario.onActivity(a -> {
            Mall.Shop shop = a.model.byId(id);
            Viewport camera = a.map.camera;
            int[] origin = new int[2];
            a.map.getLocationOnScreen(origin);
            p[0] = origin[0] + camera.tx + (shop.x + xOffset) * camera.scale;
            p[1] = origin[1] + camera.ty + (shop.y + 15) * camera.scale;
        });
        return p;
    }
    private void tapShop(String id, float offset) {
        float[] p = point(id, offset);
        tap(p[0], p[1]);
    }
    private static View find(View v, String label) {
        if (label.contentEquals(v.getContentDescription() == null ? "" : v.getContentDescription())) return v;
        if (v instanceof TextView && label.contentEquals(((TextView) v).getText())) return v;
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            for (int i = 0; i < group.getChildCount(); i++) {
                View match = find(group.getChildAt(i), label);
                if (match != null) return match;
            }
        }
        return null;
    }
    private void click(String label) {
        scenario.onActivity(a -> {
            View v = find(a.root, label);
            assertNotNull(label, v);
            assertTrue(v.isShown());
            v.performClick();
        });
        pause();
    }
    private void panAndPinchAroundC07() {
        float[] p = point("C07", 25);
        long t = SystemClock.uptimeMillis();
        touch(t, MotionEvent.ACTION_DOWN, p[0], p[1]);
        for (int i = 1; i <= 16; i++) {
            touch(t, MotionEvent.ACTION_MOVE, p[0]+i*3, p[1]-i*2);
            SystemClock.sleep(16);
        }
        touch(t, MotionEvent.ACTION_UP, p[0]+48, p[1]-32);
        pause();
        p = point("C07", 25);
        t = SystemClock.uptimeMillis();
        touch(t, MotionEvent.ACTION_DOWN, p[0]-20, p[1]);
        touch(t, MotionEvent.ACTION_POINTER_DOWN | (1 << MotionEvent.ACTION_POINTER_INDEX_SHIFT),
                p[0]-20, p[1], p[0]+20, p[1]);
        for (int r = 21; r <= 48; r++) {
            touch(t, MotionEvent.ACTION_MOVE, p[0]-r, p[1], p[0]+r, p[1]);
            SystemClock.sleep(16);
        }
        touch(t, MotionEvent.ACTION_POINTER_UP | (1 << MotionEvent.ACTION_POINTER_INDEX_SHIFT),
                p[0]-48, p[1], p[0]+48, p[1]);
        touch(t, MotionEvent.ACTION_UP, p[0]-48, p[1]);
        pause();
        scenario.onActivity(a -> {
            assertEquals(2.4f, a.map.camera.scale / a.map.camera.fit(), 0.01f);
            assertEquals("A03", a.model.selected.id);
        });
    }
    private void rotate(int orientation, int expected) {
        scenario.onActivity(a -> a.setRequestedOrientation(orientation));
        long deadline = SystemClock.uptimeMillis() + 6000;
        boolean[] ready = {false};
        do {
            SystemClock.sleep(120);
            scenario.onActivity(a -> ready[0] = a.getResources().getConfiguration().orientation == expected
                    && a.map.getHeight() > 0 && a.details.isShown());
        } while (!ready[0] && SystemClock.uptimeMillis() < deadline);
        assertTrue("orientation", ready[0]);
        scenario.onActivity(a -> {
            assertEquals("C07", a.model.selected.id);
            assertEquals(2, a.model.route.size());
            assertNotNull(find(a.details, "Định vị A03"));
            assertNotNull(find(a.details, "Định vị C07"));
        });
        pause();
    }
    private void search(String query) {
        scenario.onActivity(a -> a.search.setText(query));
        pause();
    }
    @Test public void visitTwoShopsFilterResetRotateAndRemove() {
        try (ActivityScenario<MainActivity> activity = ActivityScenario.launch(MainActivity.class)) {
            scenario = activity;
            pause();
            if (demo) {
                click("Chú thích");
                device.sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK);
                pause();
            }
            tapShop("A03", 25);
            click("Thêm điểm");
            panAndPinchAroundC07();
            tapShop("C07", 49);
            scenario.onActivity(a -> assertEquals("C07", a.model.selected.id));
            tapShop("C07", 51);
            scenario.onActivity(a -> assertNull(a.model.selected));
            tapShop("C07", 49);
            tapShop("C07", 49);
            scenario.onActivity(a -> assertNull(a.model.selected));
            tapShop("C07", 25);
            click("Thêm điểm");
            scenario.onActivity(a -> a.summary.performClick());
            pause();
            click("Định vị A03");
            search("A0");
            scenario.onActivity(a -> {
                assertTrue(a.summary.getText().toString().contains("1/2"));
                assertNull(find(a.details, "Định vị C07"));
            });
            click("Toàn tầng");
            SystemClock.sleep(350);
            scenario.onActivity(a -> {
                assertEquals(1, a.map.camera.scale / a.map.camera.fit(), 0.001);
                assertEquals("A03", a.model.selected.id);
                assertEquals("A0", a.model.query);
                assertEquals(2, a.model.route.size());
            });
            search("ZZZ");
            scenario.onActivity(a -> assertEquals("Không tìm thấy gian phù hợp", a.result.getText().toString()));
            click("Xóa");
            click("Định vị C07");
            rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE);
            rotate(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT);
            click("Gỡ C07");
            scenario.onActivity(a -> {
                assertEquals(1, a.model.route.size());
                assertTrue(a.model.route.contains("A03"));
                assertFalse(a.model.route.contains("C07"));
            });
            click("Định vị A03");
            tapShop("A03", 25);
            tapShop("A03", 25);
            scenario.onActivity(a -> assertEquals("A03", a.model.selected.id));
        }
    }
}
