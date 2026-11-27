package vn.mall.atlas;

import androidx.lifecycle.ViewModel;
import java.util.LinkedHashSet;

public final class VisitModel extends ViewModel {
    final Mall mall = new Mall();
    final LinkedHashSet<String> route = new LinkedHashSet<>();
    Mall.Shop selected;
    boolean expanded;

    void tap(Mall.Shop shop) { selected = selected == shop ? null : shop; }
    void addSelected() { if (selected != null) route.add(selected.id); }
    Mall.Shop byId(String id) {
        for (Mall.Shop shop : mall.shops) if (shop.id.equals(id)) return shop;
        return null;
    }
    Mall.Shop at(float x, float y) {
        for (Mall.Shop shop : mall.shops) if (shop.contains(x, y)) return shop;
        return null;
    }
}
