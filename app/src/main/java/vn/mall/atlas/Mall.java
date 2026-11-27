package vn.mall.atlas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

final class Mall {
    static final int WIDTH = 664, HEIGHT = 856;
    static final String[] ZONES = {"Thời trang", "Ẩm thực", "Công nghệ", "Gia dụng"};
    static final String[] STATUS = {"Mở cửa", "Sắp mở", "Tạm đóng"};
    private static final String[][] NAMES = {
        {"Lụa", "Sợi", "Nếp", "Mộc Miên", "Chàm"},
        {"Bếp Nhà", "Lá Thơm", "Hạt Nâu", "Phố Bánh", "Gốm Café"},
        {"Pixel", "Quang Học", "Tần Số", "Âm Sắc", "Mạch"},
        {"Nhà Xinh", "Đan Mây", "Góc Bếp", "Đất Nung", "Đèn Mộc"}
    };
    static final class Shop {
        final String id, name;
        final int zone, status, x, y;
        static final int W = 50, H = 30;
        Shop(int zone, int row, int col) {
            int number = row * 5 + col + 1;
            this.zone = zone;
            id = String.format(Locale.ROOT, "%c%02d", 'A' + zone, number);
            name = NAMES[zone][col] + " " + (row + 1);
            status = number % 8 == 0 ? 2 : number % 5 == 0 ? 1 : 0;
            x = zx(zone) + 12 + 57 * col;
            y = zy(zone) + 55 + 37 * row;
        }
        boolean contains(float wx, float wy) {
            return wx >= x && wx < x + W && wy >= y && wy < y + H;
        }
    }
    static int zx(int zone) { return 15 + (zone % 2) * 334; }
    static int zy(int zone) { return 14 + (zone / 2) * 430; }

    final List<Shop> shops;
    Mall() {
        List<Shop> all = new ArrayList<>(180);
        for (int zone = 0; zone < 4; zone++)
            for (int row = 0; row < 9; row++)
                for (int col = 0; col < 5; col++) all.add(new Shop(zone, row, col));
        shops = Collections.unmodifiableList(all);
    }
}
