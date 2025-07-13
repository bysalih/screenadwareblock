# Reklam Engelleyici - Android Uygulaması

Bu Android uygulaması, cihazınızdaki reklamlı ve kötü amaçlı uygulamaları tespit edip kaldırmanıza yardımcı olur.

## Özellikler

### 🔍 Akıllı Tehdit Tespiti
- **Çoklu Tespit Algoritması**: Paket ismi, izinler ve davranış analizi
- **Gerçek Zamanlı Tarama**: Yeni yüklenen uygulamaları otomatik tarar
- **Tehdit Seviyeleri**: 
  - ✅ Güvenli (Seviye 0)
  - ⚠️ Şüpheli (Seviye 1) 
  - ⚡ Tehlikeli (Seviye 2)
  - 🚨 Reklam/Kötü Amaçlı (Seviye 3)

### 📊 Veritabanı Sistemi
- **Room Database**: Yerel SQLite veritabanı ile hızlı veri erişimi
- **Tehdit İmzaları**: Güncellenebilir kötü amaçlı uygulama veritabanı
- **Uygulama Geçmişi**: Yükleme tarihleri ve tarama sonuçları

### 🛡️ Gelişmiş Koruma
- **İzin Analizi**: Şüpheli izinleri tespit eder
- **Overlay Kontrolü**: Ekran üstü çizim izinlerini kontrol eder
- **Sistem Uygulaması Tespiti**: Sistem ve kullanıcı uygulamalarını ayırır

### 🎨 Modern Arayüz
- **Material Design 3**: Modern ve kullanıcı dostu tasarım
- **Renkli Tehdit Gösterimi**: Tehdit seviyelerine göre renk kodları
- **Akıllı Filtreler**: Sadece tehditli uygulamaları gösterme seçeneği
- **Swipe to Refresh**: Aşağı çekerek yenileme

## Tespit Edilen Tehditler

### Bilinen Reklam Uygulamaları
- Sahte temizleyici uygulamaları
- Kamera güçlendirici yalanları
- Gereksiz optimizasyon araçları
- Batarya tasarruf hileleri

### Şüpheli İzinler
- SMS okuma/gönderme
- Telefon aramalar
- Kişi listesi erişimi
- Konum bilgisi
- Mikrofon ve kamera erişimi
- Cihaz yöneticisi yetkileri

## Kurulum

### APK Olarak Yükleme
1. Releases sayfasından en son APK dosyasını indirin
2. Android cihazınızda "Bilinmeyen kaynaklardan yükleme" iznini açın
3. APK dosyasını açıp yükleyin
4. Uygulamayı çalıştırın ve izinleri verin

### Kaynak Koddan Derleme
```bash
# Projeyi klonlayın
git clone <repository_url>
cd ScreenAdwareBlock

# Debug APK oluşturun
./gradlew assembleDebug

# APK dosyası: app/build/outputs/apk/debug/app-debug.apk
```

## Kullanım

### İlk Kurulum
1. Uygulamayı açın
2. Gerekli izinleri verin:
   - Yüklü uygulamaları görme
   - Paket bilgilerine erişim
3. İlk tarama otomatik başlar

### Ana Özellikler
- **Tarama**: FAB butonuna tıklayarak manuel tarama
- **Filtreleme**: Menu > "Sadece Tehditler" ile sadece tehlikeli uygulamaları görün
- **Kaldırma**: Her uygulamanın yanındaki "Kaldır" butonu
- **Güvenli Liste**: Güvendiğiniz uygulamaları işaretleyin

### Otomatik Koruma
- Yeni uygulama yüklendiğinde otomatik taranır
- Arka plan servisi ile sürekli izleme (opsiyonel)
- Tehdit tespitinde bildirim

## Teknik Detaylar

### Sistem Gereksinimleri
- Android 7.0 (API 24) ve üzeri
- Minimum 50 MB boş alan
- İnternet bağlantısı (tehdit veritabanı güncellemeleri için)

### Kullanılan Teknolojiler
- **Kotlin**: Ana programlama dili
- **Android Architecture Components**: ViewModel, LiveData, Room
- **Material Design 3**: Modern UI bileşenleri
- **Coroutines**: Asenkron işlemler
- **Room Database**: Yerel veri depolama

### İzinler
```xml
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES"/>
<uses-permission android:name="android.permission.REQUEST_DELETE_PACKAGES"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
```

## Güvenlik

### Gizlilik
- Hiçbir kişisel veri sunucuya gönderilmez
- Tüm analiz cihazda yerel olarak yapılır
- Sadece tehdit imzaları güncellenir

### Açık Kaynak
Bu proje açık kaynak kodludur ve topluluk katkılarına açıktır.

## Katkıda Bulunma

### Hata Bildirimi
Issues sekmesinden hata bildirebilirsiniz.

### Kod Katkısı
1. Fork yapın
2. Feature branch oluşturun
3. Değişikliklerinizi commit edin
4. Pull request açın

## Lisans

Bu proje MIT lisansı altında yayınlanmıştır.

## İletişim

Sorularınız için GitHub Issues kullanabilirsiniz.

---

⚠️ **Uyarı**: Bu uygulama yardımcı bir araçtır. Sistem uygulamalarını kaldırmak cihazınıza zarar verebilir. Dikkatli kullanın.