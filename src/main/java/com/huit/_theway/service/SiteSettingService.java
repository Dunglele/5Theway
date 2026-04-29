package com.huit._theway.service;

import com.huit._theway.model.SiteSetting;
import com.huit._theway.repository.SiteSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SiteSettingService {

    private final SiteSettingRepository siteSettingRepository;

    public SiteSetting getSettings() {
        return siteSettingRepository.findById(1L).orElseGet(() -> {
            SiteSetting defaultSettings = new SiteSetting();
            defaultSettings.setId(1L);
            
            // Defaults (matching current hardcoded values)
            defaultSettings.setSlide1Image("/assets/images/blackpink-jump-8k-8256x5504-23197.jpeg");
            defaultSettings.setSlide1Eyebrow("New Season Drop");
            defaultSettings.setSlide1Title("DEAL SÂU<br/>CHƯA TỪNG CÓ");
            defaultSettings.setSlide1Subtitle("Streetwear chính hãng, giá tốt nhất năm. Số lượng có hạn.");
            defaultSettings.setSlide1LinkUrl("#products");
            defaultSettings.setSlide1LinkText("Khám phá ngay");

            defaultSettings.setSlide2Image("/assets/images/blackpink-pubg-mobile-pink-background-3413x1920-3105.jpg");
            defaultSettings.setSlide2Eyebrow("Limited Stock");
            defaultSettings.setSlide2Title("OUTLET<br/>COLLECTION");
            defaultSettings.setSlide2Subtitle("Phụ kiện, túi xách và nhiều hơn nữa với mức giá không tưởng.");
            defaultSettings.setSlide2LinkUrl("#accessories");
            defaultSettings.setSlide2LinkText("Xem ACCESSORIES");

            defaultSettings.setCategory1Slug("tops");
            defaultSettings.setCategory1Title("TOPS");
            defaultSettings.setCategory1Subtitle("Bộ sưu tập");

            defaultSettings.setCategory2Slug("accessories");
            defaultSettings.setCategory2Title("ACCESSORIES");
            defaultSettings.setCategory2Subtitle("Phụ kiện");

            return siteSettingRepository.save(defaultSettings);
        });
    }

    public void saveSettings(SiteSetting settings) {
        settings.setId(1L);
        siteSettingRepository.save(settings);
    }
}
