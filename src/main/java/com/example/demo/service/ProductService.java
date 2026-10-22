package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    public void initProductsIfEmpty() {
        if (productRepository.count() == 0) {
            // 1. Qishki Pufik Kurtka
            productRepository.save(new Product(
                    "Qishki Pufik Kurtka (Waterproof Down)",
                    "Kurtkalar",
                    new BigDecimal("750000.00"),
                    new BigDecimal("890000.00"),
                    "Sovuqqa -25°C gacha chidamli, suv va shamol o'tkazmaydigan tabiiy patli premium kurtka.",
                    "https://images.unsplash.com/photo-1544923246-77307dd654cb?w=600&auto=format&fit=crop&q=80",
                    4.9, 142, 15, "M, L, XL, XXL", "Qora, To'q ko'k, Zaytun", true
            ));

            // 2. Charm Bayker Kurtka
            productRepository.save(new Product(
                    "Charm Bayker Kurtka (Classic Biker)",
                    "Kurtkalar",
                    new BigDecimal("920000.00"),
                    new BigDecimal("1100000.00"),
                    "100% tabiiy sifatli charm, metall furnituralar bilan bezatilgan klassik bayker fason.",
                    "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=600&auto=format&fit=crop&q=80",
                    4.8, 98, 16, "S, M, L, XL", "Klassik Qora, Qahva rang", true
            ));

            // 3. Qishki Parka Kurtka
            productRepository.save(new Product(
                    "Klassik Parka Kurtka (Nordic Fur)",
                    "Kurtkalar",
                    new BigDecimal("850000.00"),
                    new BigDecimal("990000.00"),
                    "Mo'ynali kapyushon, uzun bichim, qishki sovuq va qorli kunlar uchun ideal himoya.",
                    "https://images.unsplash.com/photo-1539533018447-63fcce667883?w=600&auto=format&fit=crop&q=80",
                    4.9, 76, 14, "M, L, XL", "Xaki, Qora, To'q kulrang", false
            ));

            // 4. Bahorgi Bomber Kurtka
            productRepository.save(new Product(
                    "Engil Bomber Kurtka (Air Aviation)",
                    "Kurtkalar",
                    new BigDecimal("480000.00"),
                    new BigDecimal("550000.00"),
                    "Sport va casual uslubidagi zamonaviy yengil bomber kurtka, qulay cho'ntaklar bilan.",
                    "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=600&auto=format&fit=crop&q=80",
                    4.7, 54, 12, "S, M, L, XL, XXL", "Yashil, Qora, Bej", false
            ));

            // 5. Sport Krossovka
            productRepository.save(new Product(
                    "Sport Krossovka (Nike Air Flex Pro)",
                    "Poyabzal",
                    new BigDecimal("520000.00"),
                    new BigDecimal("620000.00"),
                    "Amortizatsiyali taglik, yugurish va kundalik sayr uchun nafas oluvchi material.",
                    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=80",
                    4.9, 210, 16, "40, 41, 42, 43, 44", "Qizil-Oq, Qora, Moviy", true
            ));

            // 6. Qishki Charm Botinka
            productRepository.save(new Product(
                    "Qishki Charm Botinka (Winter Boots)",
                    "Poyabzal",
                    new BigDecimal("690000.00"),
                    new BigDecimal("790000.00"),
                    "Ichki tabiiy jun qoplamali, sirpanmaydigan qalin protektor taglikli qishki botinka.",
                    "https://images.unsplash.com/photo-1520639888713-7851133b1ed0?w=600&auto=format&fit=crop&q=80",
                    4.8, 89, 12, "40, 41, 42, 43, 44", "Jigarrang, Qora", false
            ));

            // 7. Oversize Hudi
            productRepository.save(new Product(
                    "Oversize Winter Hoodie (Qalin Hudi)",
                    "Kiyim-kechak",
                    new BigDecimal("290000.00"),
                    new BigDecimal("350000.00"),
                    "100% paxta, yumshoq flis astarli, zamonaviy erkin bichimli iliq hudi.",
                    "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=600&auto=format&fit=crop&q=80",
                    4.7, 165, 17, "S, M, L, XL", "Kulrang, Qora, Bej", false
            ));

            // 8. Smart Watch Pro
            productRepository.save(new Product(
                    "Smart Soat (Watch Pro Series 9)",
                    "Elektronika",
                    new BigDecimal("580000.00"),
                    new BigDecimal("680000.00"),
                    "AMOLED ekran, qon bosimi va puls o'lchagich, qo'ng'iroqlarni qabul qilish va sport rejimlar.",
                    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
                    4.9, 312, 14, "Universal", "Qora, Kumush, Tilla", true
            ));

            // 9. Simsiz Quloqchin
            productRepository.save(new Product(
                    "Simsiz Quloqchin (AirSound Pro ANC)",
                    "Elektronika",
                    new BigDecimal("380000.00"),
                    new BigDecimal("450000.00"),
                    "Shovqinni faol to'suvchi (Active Noise Cancelling), 30 soatgacha batareya va kuchli bass.",
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
                    4.8, 178, 15, "Universal", "Oq, Qora", false
            ));

            // 10. Charm Hamyon to'plami
            productRepository.save(new Product(
                    "Charm Hamyon & Kamar To'plami",
                    "Aksessuarlar",
                    new BigDecimal("210000.00"),
                    new BigDecimal("260000.00"),
                    "Tabiiy charmdan tayyorlangan erkaklar hamyoni va klassik kamar, sovg'abop qutida.",
                    "https://images.unsplash.com/photo-1627123424574-724758594e93?w=600&auto=format&fit=crop&q=80",
                    4.9, 95, 19, "Universal", "Qora, Jigarrang", false
            ));
        }
    }
}
