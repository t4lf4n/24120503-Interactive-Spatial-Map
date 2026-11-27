package vn.mall.atlas;

/** Coordinates are pixels on the View and logical units on the floor plan. */
final class Viewport {
    float width, height, scale = 1, tx, ty;
    float fit() { return Math.min(width / Mall.WIDTH, height / Mall.HEIGHT); }
    void resize(int w, int h) {
        if (w <= 0 || h <= 0) return;
        float oldFit = fit();
        float zoom = oldFit > 0 ? scale / oldFit : 1;
        float cx = oldFit > 0 ? worldX(width / 2) : Mall.WIDTH / 2f;
        float cy = oldFit > 0 ? worldY(height / 2) : Mall.HEIGHT / 2f;
        width = w;
        height = h;
        center(cx, cy, zoom);
    }
    void center(float x, float y, float zoom) {
        scale = fit() * clamp(zoom, 1, 6);
        tx = width / 2 - x * scale;
        ty = height / 2 - y * scale;
        constrain();
    }
    float worldX(float px) { return (px - tx) / scale; }
    float worldY(float py) { return (py - ty) / scale; }
    void pan(float dx, float dy) {
        tx += dx;
        ty += dy;
        constrain();
    }
    void pinch(float oldX, float oldY, float newX, float newY, float factor) {
        float anchorX = worldX(oldX), anchorY = worldY(oldY);
        scale = clamp(scale * factor, fit(), fit() * 6);
        tx = newX - anchorX * scale;
        ty = newY - anchorY * scale;
        constrain();
    }
    private void constrain() {
        tx = boundAxis(tx, width, Mall.WIDTH * scale);
        ty = boundAxis(ty, height, Mall.HEIGHT * scale);
    }
    private static float boundAxis(float offset, float view, float span) {
        float gap = view - span;
        float slack = Math.min(view, span) * 0.22f;
        return clamp(offset, Math.min(0, gap) - slack, Math.max(0, gap) + slack);
    }
    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
