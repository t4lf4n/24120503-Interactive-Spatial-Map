package vn.mall.atlas;

import org.junit.Test;
import java.util.Arrays;
import java.util.HashSet;
import static org.junit.Assert.*;

public class FloorTest {
    final VisitModel model = new VisitModel();
    final Viewport view = new Viewport();

    @Test public void floorHasFourZonesOf45UniqueShops() {
        HashSet<String> ids = new HashSet<>();
        int[] counts = new int[4], statuses = new int[3];
        for (Mall.Shop shop : model.mall.shops) {
            assertTrue(ids.add(shop.id));
            counts[shop.zone]++;
            statuses[shop.status]++;
            assertSame(shop, model.at(shop.x + 1, shop.y + 1));
        }
        assertArrayEquals(new int[]{45,45,45,45}, counts);
        for (int count : statuses) assertTrue(count > 0);
    }
    private void checkEdge() {
        Mall.Shop shop = model.byId("A03");
        float edge = (shop.x + Mall.Shop.W) * view.scale + view.tx;
        float y = (shop.y + Mall.Shop.H / 2f) * view.scale + view.ty;
        assertSame(shop, model.at(view.worldX(edge - 0.1f), view.worldY(y)));
        assertNull(model.at(view.worldX(edge + 0.1f), view.worldY(y)));
    }
    @Test public void edgesInDefaultPannedZoomedAndCombinedViews() {
        view.resize(1080, 1300);
        checkEdge();
        view.pan(50, 75);
        checkEdge();
        view.center(Mall.WIDTH / 2f, Mall.HEIGHT / 2f, 1);
        float x = view.worldX(300), y = view.worldY(400);
        view.pinch(300, 400, 320, 410, 2.5f);
        assertEquals(x, view.worldX(320), 0.001);
        assertEquals(y, view.worldY(410), 0.001);
        checkEdge();
        view.pan(-40, 25);
        checkEdge();
    }
    @Test public void cameraStaysVisibleAtBothZoomLimitsAndAllFourCorners() {
        view.resize(1080, 1500);
        for (float factor : new float[]{1000, 0.00001f}) {
            view.pinch(400, 500, 400, 500, factor);
            assertEquals(factor > 1 ? 6 : 1, view.scale / view.fit(), 0.001);
            for (int x : new int[]{-100000, 100000}) for (int y : new int[]{-100000, 100000}) {
                view.pan(x, y);
                assertTrue(view.tx < view.width && view.tx + Mall.WIDTH * view.scale > 0);
                assertTrue(view.ty < view.height && view.ty + Mall.HEIGHT * view.scale > 0);
            }
        }
    }
    @Test public void filteringAndSelectionPreserveRouteOrder() {
        Mall.Shop a = model.byId("A03"), c = model.byId("C07");
        model.tap(a);
        model.addSelected();
        model.addSelected();
        model.tap(a);
        assertNull(model.selected);
        model.tap(c);
        model.addSelected();
        model.search(" a0 ");
        assertNull(model.selected);
        assertNull(model.at(c.x+1, c.y+1));
        assertEquals(Arrays.asList("A03","C07"), new java.util.ArrayList<>(model.route));
        model.search("ZZZ");
        assertNull(model.at(a.x+1, a.y+1));
        model.search("");
        assertSame(c, model.at(c.x+1, c.y+1));
        model.route.remove(c.id);
        model.route.remove(a.id);
        assertTrue(model.route.isEmpty());
    }
}
