# Kế hoạch một tuần · Sơ đồ gian hàng trung tâm thương mại

Bài cá nhân. Mục tiêu là mở ứng dụng, nhìn được bố cục toàn tầng, tìm một gian hàng, xem thông tin và thêm gian đó vào lộ trình tham quan. Kế hoạch dùng **Android/Java, một Custom View vẽ bằng Canvas và dữ liệu giả lập**. Lộ trình là danh sách gian muốn ghé theo thứ tự thêm. [Đề thi gốc, mục 1–8](./De_thi_giua_ky_CNTN2024.pdf)

**Chốt 180 gian hàng trên một Canvas.** Mục 2.1 và checklist mục 8 cho phép 50–250 đối tượng, nhưng thang điểm mục 6 yêu cầu **150–250**. Chia 4 khu Thời trang, Ẩm thực, Công nghệ và Gia dụng, mỗi khu 45 gian có mã riêng từ A01–A45 đến D01–D45. Chỉ đếm gian hàng. Ở góc nhìn mặc định phải thấy đủ bốn nhãn khu và các lối đi.

## 1. Lịch làm và commit

Mục 5.2 ghi “tối thiểu 05–07 commit có ý nghĩa, phân bổ trong thời gian thực hiện”. Dự kiến **7 commit chính trong 7 ngày tính từ lúc bắt đầu**. Ngày là mốc tương đối vì đề chỉ nêu thời lượng một tuần. Mỗi dòng dưới đây có một thay đổi có thể tự kiểm tra trước khi commit.

| Ngày | Nội dung và thông điệp commit gợi ý | Điều kiện để chốt commit |
| --- | --- | --- |
| 1 | `chore: khởi tạo ứng dụng Android và hướng dẫn chạy` — thêm Gradle Wrapper, màn hình khởi đầu, cấu hình bỏ qua tệp sinh tự động, kế hoạch và README có nhật ký AI. | Build bằng wrapper và mở được màn hình đầu trên máy ảo hoặc thiết bị. Ghi đúng phiên bản công cụ vừa dùng vào README. |
| 2 | `feat: vẽ 180 gian hàng và chú thích trên một Canvas` — tách dữ liệu gian hàng khỏi lớp vẽ, thêm hình học bốn khu, lối đi, nhãn và chú thích. | Đếm 180 ID duy nhất, mỗi khu 45. Nhìn toàn tầng ở mức zoom mặc định, chỉ ra được ba trạng thái và đối chiếu với chú thích. |
| 3 | `feat: giới hạn pan và zoom quanh tâm hai ngón` — dùng chung phép biến đổi tọa độ cho vẽ và tương tác. | Kéo tới bốn biên, sơ đồ vẫn trong tầm nhìn. Pinch ở góc lệch tâm màn hình, theo dõi cùng một gian dưới trung điểm hai ngón. Thử cả hai giới hạn zoom. |
| 4 | `feat: chọn gian sau pan zoom và đồng bộ lộ trình với Bottom Sheet` — chạm để xem, chạm lại để bỏ chọn, thêm hoặc gỡ điểm ghé, chọn dòng để định vị. | Thử tâm, sát mép trong và lối đi sát mép ngoài của cùng gian sau pan rồi zoom. Thêm hai gian, gỡ một từ sheet, kiểm tra ký hiệu trên Canvas và gian còn lại. |
| 5 | `feat: tìm gian theo mã và khôi phục góc nhìn` — thêm ô tìm theo tiền tố mã, xóa điều kiện, thông báo rỗng và Reset View có chuyển động. | Tìm `A0`, tìm mã không tồn tại rồi xóa. Khi còn điều kiện tìm và lộ trình, bấm Reset View rồi đối chiếu điều kiện, gian đang chọn và danh sách trước/sau. |
| 6 | `test: kiểm tra chọn sát biên và giữ lộ trình khi xoay` — bổ sung kiểm thử cho phép đổi tọa độ, trạng thái lựa chọn. Chạy thử dọc/ngang, rà hiệu năng và sửa lỗi quan sát được. | Chạy lại kiểm thử, xoay hai chiều với sheet đang mở và lộ trình nhiều mục. Pan/zoom liên tục khi hiện đủ 180 gian. Ghi lại thiết bị và kết quả thực tế. |
| 7 | `docs: hoàn thiện README nhật ký AI và video minh họa` — chạy thử bản bàn giao, quay video, cập nhật hướng dẫn và đường dẫn. | Người chấm có thể clone, build, mở app và làm lại kịch bản bên dưới. Kiểm tra quyền xem repo và video, đối chiếu checklist mục 8. |

**Cách giữ lịch sử Git có giá trị khi chấm.** Cuối mỗi mốc, xem `git diff --staged`, chạy phép thử ở cột cuối, cập nhật nhật ký AI nếu có rồi commit và push trong buổi làm đó. Nếu phát hiện lỗi khác với phần đang làm, tách riêng bản sửa có thể kiểm tra, chẳng hạn `fix: giữ đúng gian dưới tâm pinch khi đổi hướng kéo` nếu lỗi này thực sự xảy ra. Có thể có hơn 7 commit. Trước khi nộp, xem `git log --reverse --oneline` và lịch sử trên GitHub để kiểm tra các mốc đã được đẩy lên. Đề loại lịch sử dồn cả bài vào một hoặc hai commit cuối kỳ.

## 2. Thiết kế và cách tự chấm theo mục 6

| Hạng mục | Cách triển khai và dấu hiệu đạt |
| --- | --- |
| Canvas, bố cục, hiệu năng · **25%** | Một `MallMapView` tự vẽ nền, nhãn khu, lối đi và 180 gian. Mỗi gian có dữ liệu trạng thái mở cửa, sắp mở hoặc tạm đóng. Phân biệt bằng màu và nét viền/ký hiệu, dùng thêm viền chọn và dấu điểm ghé. Chú thích giải thích đủ các dấu, có thể thu gọn. Tạo trước `Paint`, vùng chữ nhật và hình học dùng lại. Chỉ yêu cầu vẽ lại khi dữ liệu, góc nhìn hoặc trạng thái hiển thị thay đổi. |
| Tương tác và tọa độ · **25%** | Dùng cùng tỷ lệ `s` và độ dịch `t` theo hai trục. Vẽ theo `screen = world × s + t`. Khi chạm, tính `world = (screen − t) / s` rồi dò hình chữ nhật của gian đang hiển thị. Khi pinch, lấy điểm bản đồ dưới trung điểm hai ngón trước khi đổi tỷ lệ, giới hạn tỷ lệ mới rồi tính lại độ dịch để giữ điểm đó dưới trung điểm mới. Áp giới hạn pan sau cùng. Phân biệt kết thúc kéo/pinch với tap. |
| Chức năng và trải nghiệm · **20%** | Sheet thu gọn hiện mã, tên, trạng thái gian đang xem và số điểm ghé. Khi mở rộng, xem chi tiết và cuộn danh sách trong khi vẫn nhìn và chạm được một phần Canvas. Chạm dòng để định vị và làm nổi gian, gỡ từng điểm để cập nhật ký hiệu trên sơ đồ. Reset View đưa vị trí và tỷ lệ về mặc định bằng hoạt ảnh. |
| Kiến trúc và trạng thái · **15%** | Model lưu ID ổn định, tên, khu, trạng thái và vùng hình học. ViewModel giữ ID đang xem, danh sách ID điểm ghé theo thứ tự và điều kiện tìm. Canvas và sheet cùng đọc trạng thái này. Khi xoay, tính lại khung nhìn theo kích thước mới và giữ các ID. Kiểm tra cả chiều ngang khi sheet mở. |
| Chất lượng mã và độ ổn định · **10%** | Đặt tên rõ, dùng chung hàm chuyển tọa độ. Thử chạm lối đi, thêm cùng ID hai lần, gỡ mục cuối, tìm rỗng, pinch tới giới hạn rồi chạm. Danh sách và phản hồi trên Canvas phải khớp sau từng bước. |
| Hồ sơ và quy trình · **5%** | Lịch sử Git thể hiện tiến độ thật. README chứa đủ nội dung mục 5.4–5.5. Video cho thấy thao tác và kết quả. Mở thử cả hai đường dẫn bằng tài khoản có quyền tương đương người chấm. |

**Quy ước chọn, lọc và lộ trình.** Chạm gian để xem thông tin. Chạm lại bỏ viền chọn, giữ đúng trạng thái kinh doanh và dấu điểm ghé đã có. Thêm gian vào lộ trình chỉ một lần cho mỗi ID. Khi tìm theo mã, ẩn các gian không khớp và bỏ chọn gian đang xem nếu nó bị ẩn. Giữ nguyên toàn bộ lộ trình, hiển thị số điểm ghé phù hợp trên tổng số và chỉ liệt kê các dòng phù hợp. Xóa điều kiện tìm sẽ hiện lại các điểm ghé. Reset View chỉ đổi góc nhìn.

## 3. Các trường hợp bị trừ hoặc giới hạn điểm ở mục 7

| Quy định trong đề | Phép kiểm tra trước khi nộp |
| --- | --- |
| App không biên dịch hoặc không khởi chạy được thì tổng tối đa **5,0/10,0**. Giảng viên chỉ chấm được phần có thể xác minh. | Clone từ GitHub sang thư mục mới, làm theo README. Trên Windows chạy `.\gradlew.bat assembleDebug` và cài, mở app trên thiết bị. Chạy lại sau bản sửa mã cuối cùng. |
| Dùng Canvas sai yêu cầu thì riêng hạng mục Canvas, bố cục và hiệu năng tối đa **5/25**. | Mở lớp vẽ và chỉ ra nơi một Canvas vẽ toàn bộ gian từ dữ liệu. Nút tìm, Reset và sheet thuộc giao diện xung quanh. Kiểm tra số gian bằng dữ liệu đầu vào của lớp vẽ. |
| Hit-testing chỉ đúng ở mặc định, sai sau Pan hoặc Zoom thì **mất phần điểm hit-testing**. | Với A03, thử lần lượt ở mặc định, chỉ pan, chỉ zoom, rồi kết hợp cả hai. Mỗi lần chạm bên trong sát mép phải hiện A03, chạm lối đi ngay ngoài mép phải không chọn nhầm gian. |
| Chức năng không chạy hoặc không thể kiểm tra sẽ không được tính điểm. Chức năng bổ sung không bù yêu cầu bắt buộc. | Đi hết luồng trong README trên bản clone. Dành thời gian sửa các mục chưa đạt trước khi thêm tiện ích. |
| Sao chép mã hoặc vi phạm học thuật được xử lý theo quy định của học phần và nhà trường. | Tự rà phần mã đã dùng, giải thích được phép đổi tọa độ, hit-testing và luồng cập nhật sheet. Ghi trung thực từng lần dùng AI theo mục 4 và 5.5. |

## 4. Hồ sơ nộp bài và nhật ký AI theo mục 4–5

**GitHub.** Nộp mã nguồn, tài nguyên mock và các tệp cấu hình cần để build, gồm cả Gradle Wrapper. Kiểm tra danh sách tệp được Git theo dõi để loại thư mục build, cache, cấu hình máy cá nhân và dữ liệu thừa. README ghi rõ cách chọn thiết bị, chạy ứng dụng và thử pinch nếu dùng máy ảo.

**Mười nội dung bắt buộc của README.md.** (1) Mô tả đề tài, (2) hướng dẫn chạy, (3) kiến trúc mã nguồn, (4) tổ chức dữ liệu, (5) công thức chuyển đổi tọa độ, (6) phương pháp hit-testing, (7) quản lý trạng thái, (8) tối ưu hiệu năng, (9) đường dẫn video, (10) nhật ký sử dụng AI Agent.

**Nhật ký AI nằm ngay trong README.md.** Mỗi lần dùng ghi ngày hoặc commit, tên Agent, nhiệm vụ, tệp/phần mã Agent sinh hay chỉnh sửa, lỗi hoặc hạn chế, cách sinh viên kiểm tra, điều chỉnh và kết quả. Sinh viên chịu trách nhiệm về toàn bộ mã nộp. Với buổi lập kế hoạch này có thể ghi “Codex đọc đề và soạn `plans.md`, chỉ tạo tài liệu”. Phần sinh viên tự kiểm tra có thể bắt đầu bằng việc đối chiếu số gian ở trang 1 và trang 4. Ghi kết quả sau khi đã thực hiện.

**Kịch bản video khoảng 3 phút** (đề cho phép 1–5 phút). Dùng hai gian A03 và C07 để người xem theo dõi được cùng một lộ trình xuyên suốt.

| Thời gian | Thao tác và kết quả cần nhìn thấy |
| --- | --- |
| 0:00–0:25 | Mở sơ đồ mặc định, chỉ bốn khu, lối đi, chú thích. Chạm A03 và thêm vào lộ trình. |
| 0:25–1:00 | Pan một ngón, pinch hai ngón quanh C07. Chạm gần mép trong của C07, chỉ mã trên sheet, chạm lại bỏ chọn rồi chọn lại và thêm vào lộ trình. |
| 1:00–1:40 | Thu gọn/mở rộng sheet, chọn dòng A03 để định vị. Tìm `A0`, thấy 1/2 điểm ghé phù hợp. Bấm Reset View, thấy góc nhìn đổi trong khi điều kiện tìm và tổng hai điểm còn nguyên. |
| 1:40–2:15 | Tìm `ZZZ` để thấy thông báo rỗng. Xóa tìm kiếm, thấy đủ hai dòng. Chọn C07 từ sheet và xoay ngang rồi về dọc, đối chiếu mã đang chọn, hai điểm ghé và phần Canvas còn thao tác được. |
| 2:15–3:00 | Gỡ C07 từ sheet, quay lại vị trí gian để thấy dấu điểm ghé biến mất, A03 vẫn còn. Chọn/bỏ chọn A03 trên Canvas để thấy sheet cập nhật ngay. |

## 5. Checklist bàn giao đối chiếu mục 8

Kết quả chi tiết và thông số máy thử nằm trong [README](README.md).

- [x] Một Canvas có 180 gian hàng và ít nhất 3 trạng thái trực quan phân biệt được.
- [x] Pan có biên, pinch đúng tâm hai ngón trong khoảng zoom cho phép, hit-testing đúng sau Pan và Zoom, kể cả sát mép.
- [x] Chú thích dễ mở, tìm theo mã có báo rỗng và có thể xóa điều kiện. Reset View hoạt động và giữ lựa chọn, lộ trình, điều kiện tìm.
- [x] Bottom Sheet thu gọn, mở rộng, cuộn và đồng bộ hai chiều. Chọn dòng định vị đúng gian, xóa một điểm chỉ cập nhật điểm đó.
- [x] Xoay ngang/dọc giữ gian đang xem và các điểm ghé. Cả hai bố cục dùng được, các luồng chính chạy ổn định.
- [x] Build, cài và khởi chạy được từ bản clone sạch theo README. Mã nguồn và tệp cần thiết đã có trên GitHub.
- Theo yêu cầu thực hiện, bảy commit theo mốc được tạo liên tiếp trong một phiên. Tiêu chí phân bổ trong tuần của kế hoạch ban đầu chưa đạt.
- [x] Video dài 1–5 phút thể hiện đủ các thao tác ở mục 5.3. README đủ 10 nội dung, gồm nhật ký AI đã cập nhật.
- Đường dẫn bàn giao: [repository](https://github.com/t4lf4n/24120503-Interactive-Spatial-Map) và [video](docs/atlas-demo.mp4).
