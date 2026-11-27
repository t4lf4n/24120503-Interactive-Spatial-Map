package vn.mall.atlas;

import androidx.lifecycle.ViewModel;
import java.util.LinkedHashSet;

public final class VisitModel extends ViewModel {
    final Mall mall = new Mall();
    final LinkedHashSet<String> route = new LinkedHashSet<>();
    Mall.Shop selected;
    boolean expanded;
    String query = "";
    private String prefix = "";

    void search(String input) {
        query = input;
        prefix = input.trim().toUpperCase(java.util.Locale.ROOT);
        if (selected != null && !matches(selected.id)) selected = null;
    }
    boolean filtering() { return !prefix.isEmpty(); }
    boolean matches(String id) { return id.startsWith(prefix); }

    void tap(Mall.Shop shop) { selected = selected == shop ? null : shop; }
    void addSelected() { if (selected != null) route.add(selected.id); }
    Mall.Shop byId(String id) {
        for (Mall.Shop shop : mall.shops) if (shop.id.equals(id)) return shop;
        return null;
    }
    Mall.Shop at(float x, float y) {
        for (Mall.Shop shop : mall.shops) if (matches(shop.id) && shop.contains(x, y)) return shop;
        return null;
    }
}
