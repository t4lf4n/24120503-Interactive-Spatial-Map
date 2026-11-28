# Atlas · Sơ đồ gian hàng

Atlas vẽ mặt bằng một tầng trung tâm thương mại bằng Java trên Android. Chạm một gian để xem tên và trạng thái, thêm vào lộ trình rồi chạm một điểm ghé để quay lại đúng vị trí. Toàn bộ dữ liệu là giả lập và có sẵn khi mở ứng dụng.

**[Xem video thao tác trên máy ảo Android](docs/atlas-demo.mp4)** · [Kế hoạch thực hiện](plans.md) · [Mã nguồn trên GitHub](https://github.com/t4lf4n/24120503-Interactive-Spatial-Map)

## Chạy ứng dụng

Môi trường đã dùng trên Windows gồm OpenJDK 25.0.3 (JBR của Android Studio), Gradle Wrapper 9.6.0, Android Gradle Plugin 9.4.1, SDK Platform 37.0 và Build Tools 36.0.0. Thiết bị thử là AVD Medium Phone, Android 17 / API 37, màn hình 1080 × 2400. Ứng dụng khai báo hỗ trợ từ Android 7.0 (API 24).

1. Clone repo và mở thư mục gốc trong Android Studio. Trong SDK Manager, cài Android SDK Platform 37.0, Build Tools 36.0.0 và Platform-Tools. Lần build đầu cần kết nối mạng để tải Gradle và thư viện.
2. Trong Device Manager, tạo và khởi động máy ảo hoặc kết nối điện thoại đã bật USB debugging. Chọn thiết bị ở thanh công cụ, chọn cấu hình `app` và bấm **Run**.
3. Để chạy bằng PowerShell, đặt `JAVA_HOME` và `ANDROID_HOME` theo thư mục đã cài. Ví dụ với đường dẫn mặc định trên Windows:

```powershell
git clone https://github.com/t4lf4n/24120503-Interactive-Spatial-Map.git
cd 24120503-Interactive-Spatial-Map
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat assembleDebug installDebug
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n vn.mall.atlas/.MainActivity
```

APK debug nằm tại `app/build/outputs/apk/debug/app-debug.apk`. Trên Linux/macOS dùng `./gradlew` và cấu hình đường dẫn JDK/SDK tương ứng.

## Thử trong hai phút

1. Mở **Chú thích** để xem ba trạng thái kinh doanh. Chọn **A03**, bấm **Thêm điểm**. Viền xanh lam chỉ gian đang xem, chấm xanh lam chỉ gian đã thêm.
2. Kéo sơ đồ và pinch quanh **C07**. Trên Android Emulator cho Windows, giữ **Ctrl**, di chuột tới tâm mong muốn, sau đó giữ chuột trái và kéo để thay đổi khoảng cách hai ngón. Chạm sát mép trong C07, chạm lại để bỏ chọn, chọn lại và thêm điểm.
3. Chạm dòng tóm tắt dưới sơ đồ hoặc vuốt dòng này lên để mở Bottom Sheet. Chạm **A03** trong danh sách để định vị. Danh sách cuộn khi vượt chiều cao của sheet.
4. Nhập `A0` rồi nhấn phím tìm trên bàn phím. Có 9 gian khớp và 1/2 điểm ghé hiển thị. Bấm **Toàn tầng** để xem góc mặc định. A03 vẫn được chọn. Nhập `ZZZ` để xem thông báo rỗng rồi bấm **Xóa** để thấy cả hai điểm ghé.
5. Định vị C07 từ sheet, xoay ngang rồi về dọc. Mở sheet nếu đang thu gọn, bấm **Gỡ** trên dòng C07 và kiểm tra A03 vẫn có chấm điểm ghé. Bấm dòng tóm tắt hoặc vuốt xuống để thu gọn.

## Dữ liệu và kiến trúc

Các lớp chính nằm trong [vn.mall.atlas](app/src/main/java/vn/mall/atlas).

| Lớp | Trách nhiệm |
| --- | --- |
| `Mall`, `Mall.Shop` | Tạo bốn khu 5 cột × 9 hàng. Mỗi gian có ID ổn định, tên, khu, trạng thái và hình chữ nhật trong mặt bằng 664 × 856 đơn vị. |
| `Viewport` | Giữ tỷ lệ, độ dịch, giới hạn pan và phép biến đổi theo tâm pinch. Tính lại tỷ lệ vừa khung khi kích thước View thay đổi. |
| `VisitModel` | AndroidX ViewModel giữ dữ liệu, gian đang xem, từ khóa, trạng thái mở sheet và lộ trình. `LinkedHashSet` duy trì thứ tự thêm và tính duy nhất của ID. |
| `MallMapView` | Một Custom View vẽ nền khu, nhãn, 180 gian cùng các dấu trạng thái, tiếp nhận chuỗi sự kiện chạm. |
| `MainActivity` | Hiển thị tìm kiếm, chú thích, Bottom Sheet và kết nối các thao tác giao diện với ViewModel. Khi xoay, Activity mới lấy lại cùng ViewModel. |

Các mã A01–A45, B01–B45, C01–C45 và D01–D45 lần lượt thuộc Thời trang, Ẩm thực, Công nghệ và Gia dụng. Số thứ tự chia hết cho 8 là gian tạm đóng, các số còn lại chia hết cho 5 là gian sắp mở, các gian khác đang mở. Lối đi nằm giữa các hình chữ nhật.

## Tọa độ và hit-testing

Với điểm bản đồ `(x, y)`, điểm trên View là `(x × s + tx, y × s + ty)`. Tỷ lệ mặc định lấy giá trị nhỏ hơn giữa tỷ lệ vừa chiều rộng và vừa chiều cao. Mức zoom cho phép là 1–6 lần tỷ lệ mặc định.

Khi pinch, lấy tọa độ bản đồ dưới trung điểm cũ bằng phép nghịch đảo. Sau khi giới hạn tỷ lệ mới, tính `tx = focusX_mới − x × s_mới` và tương tự cho trục y. Cuối cùng giới hạn độ dịch để vẫn thấy mặt bằng khi kéo đến biên.

Khi tap, đổi ngược vị trí chạm thành `((px − tx) / s, (py − ty) / s)`. Dò các gian đang hiển thị theo quy ước `trái ≤ x < phải`, `trên ≤ y < dưới`. Chạm lối đi sẽ bỏ chọn. Vượt ngưỡng kéo của hệ thống hoặc đặt ngón thứ hai sẽ loại thao tác đó khỏi xử lý tap.

## Trạng thái và hiệu năng

- Canvas và Bottom Sheet đọc cùng một ViewModel. Chọn lại trên Canvas bỏ viền chọn. Nút **Gỡ** chỉ cập nhật ID tương ứng trong lộ trình.
- Tìm theo tiền tố mã, bỏ qua hoa thường và khoảng trắng đầu cuối. Ẩn các gian và dòng không khớp, đồng thời bỏ chọn gian vừa bị ẩn. Đếm điểm ghé phù hợp trên tổng số. Nút **Toàn tầng** chỉ nội suy tâm và tỷ lệ.
- Dữ liệu hình học, mã gian, Paint và Typeface được tạo trước. Vòng vẽ dùng các tọa độ có sẵn trên một Canvas. Chỉ yêu cầu vẽ lại khi có thay đổi hoặc trong hoạt ảnh reset.
- Khi xoay, ứng dụng tính góc nhìn cho kích thước mới và khôi phục lựa chọn, từ khóa, độ mở sheet và thứ tự điểm ghé. Lộ trình có phạm vi phiên làm việc.

## Kiểm thử

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat connectedDebugAndroidTest
```

| Phép kiểm tra | Kết quả trên phiên bản bàn giao |
| --- | --- |
| [4 unit test](app/src/test/java/vn/mall/atlas/FloorTest.java) | Đạt. Kiểm tra 180 ID, 45 gian/khu, ba trạng thái, biên trong/ngoài A03, tâm pinch, giới hạn camera và thứ tự lộ trình khi lọc. |
| [1 kiểm thử trên máy ảo](app/src/androidTest/java/vn/mall/atlas/VisitFlowTest.java) | Đạt. Phát chuỗi chạm một và hai ngón, kiểm tra chọn sát mép C07, lọc A0/ZZZ, reset, xoay dọc/ngang và gỡ điểm từ sheet. |
| Bản clone từ GitHub | Build APK bằng wrapper, chạy lại kiểm thử và mở MainActivity trên Medium Phone API 37. |
| Danh sách dài | Thêm A01–A12, cuộn Bottom Sheet từ dòng A01 tới A12. |
| Pan khi hiện 180 gian | Tám lần kéo trên AVD dùng bộ dựng hình phần mềm, không ghi video trong lúc đo. Android gfxinfo ghi 154 khung, trung vị 13 ms, phân vị 90 là 20 ms và 20 khung trễ (12,99%). |

## Nhật ký sử dụng AI Agent

| Mốc | Phần Codex thực hiện | Kiểm tra, điều chỉnh hoặc giới hạn |
| --- | --- | --- |
| Kế hoạch | Đọc `plans.md` có sẵn và đối chiếu đề PDF trong thư mục làm việc. | Chốt 180 gian theo thang điểm yêu cầu 150–250. Bản PDF của đề được bỏ qua trong Git theo cấu hình ban đầu. |
| 1 · Khởi tạo | Tạo Gradle Wrapper, cấu hình Android, manifest, Activity và README. | Dùng bộ SDK/JDK đã cài. Chọn đúng tên AVD và khởi động ở chế độ chỉ đọc để cài, mở APK. |
| 2 · Canvas | Tạo `Mall`, `MallMapView`, chú thích. | Xem ảnh màn hình mặc định trên máy ảo và đối chiếu bốn nhãn khu. Unit test ở mốc 6 kiểm lại số gian và ID. |
| 3 · Camera | Viết `Viewport` và xử lý chuyển tiếp ngón tay. | Build bằng wrapper. Kiểm thử ở mốc 6 xét cả tâm pinch dịch chuyển và hai giới hạn zoom. |
| 4 · Lộ trình | Viết `VisitModel`, Bottom Sheet, định vị và gỡ từng điểm. | Thêm A03 và C07 trên máy ảo, mở sheet và đối chiếu điểm ghé với Canvas. |
| 5 · Tìm kiếm | Thêm tìm theo tiền tố, xóa tìm kiếm, đếm kết quả và reset có hoạt ảnh. | Kiểm tra trên giao diện rằng A0 trả về 9 gian. Luồng tự động xác nhận reset giữ bộ lọc, lựa chọn và hai điểm ghé. |
| 6 · Kiểm thử | Viết `FloorTest`, `VisitFlowTest`; gom công cụ tìm kiếm lên thanh tiêu đề khi xoay ngang để dành chỗ cho sơ đồ. | 4 unit test và 1 kiểm thử thiết bị đạt. Kiểm thử thiết bị xác nhận sheet còn mở và chọn đúng C07 sau hai lần xoay. |
| 7 · Bàn giao | Clone lại dự án, ghi hình kịch bản chạy thật, hoàn thiện README và kiểm tra đường dẫn. | Video 1 phút 21 giây, có khoảng dừng giữa các thao tác để quan sát thay đổi. Thiết bị đã kiểm tra là máy ảo API 37. |
