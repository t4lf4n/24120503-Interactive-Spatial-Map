# Atlas · Sơ đồ tầng
Ứng dụng Android/Java cho bài thực hành sơ đồ gian hàng tương tác. Kế hoạch thực hiện nằm trong [plans.md](plans.md).

## Chạy trên Windows
Cài Android SDK Platform 37.0, Build Tools 36.0.0 và dùng JDK đi kèm Android Studio. Đặt `JAVA_HOME` trỏ tới JDK, `ANDROID_HOME` trỏ tới SDK. Mở máy ảo hoặc kết nối điện thoại Android 7 trở lên có bật USB debugging.

```powershell
.\gradlew.bat installDebug
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n vn.mall.atlas/.MainActivity
```

## Nhật ký AI
Môi trường build đầu tiên dùng OpenJDK 25.0.3, Gradle 9.6.0 và Android Gradle Plugin 9.4.1.

Codex đọc kế hoạch và thiết lập dự án Gradle, manifest, màn hình khởi đầu cùng hướng dẫn chạy. Các mốc kiểm tra được ghi khi thực hiện.
