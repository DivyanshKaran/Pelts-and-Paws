package com.peltspaws.pelts_paws_api.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final JdbcTemplate         jdbc;
    private final PasswordEncoder       passwordEncoder;
    private final RestTemplate          restTemplate;

    @Value("${app.catapi.key:}")
    private String catApiKey;

    @Value("${app.dogapi.key:}")
    private String dogApiKey;

    private static final String CAT_BASE = "https://api.thecatapi.com/v1";
    private static final String DOG_BASE = "https://api.thedogapi.com/v1";

    // ── Entry point ──────────────────────────────────────────────────────────

    @Override
    public void run(String... args) {
        boolean forceReseed = Arrays.asList(args).contains("--reseed");

        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        if (count != null && count > 0) {
            if (!forceReseed) {
                log.info("[Seeder] {} user(s) found — skipping seed. Run with --reseed to wipe and re-seed.", count);
                return;
            }
            log.info("[Seeder] --reseed flag detected. Clearing all tables...");
            jdbc.execute("TRUNCATE gems, pet_vaccinations, pet_health, pets, categories, users RESTART IDENTITY CASCADE");
            log.info("[Seeder] Tables cleared.");
        }

        log.info("[Seeder] Starting full database seed...");

        // Users
        Long adminId  = insertUser("admin",      "admin@peltspaws.com",  "Admin@123",    "ADMIN");
        Long owner1   = insertUser("john_doe",   "john@peltspaws.com",   "Password@123", "USER");
        Long owner2   = insertUser("jane_smith", "jane@peltspaws.com",   "Password@123", "USER");

        // Categories
        Long catCatId = insertCategory("Cats", "All domestic and exotic cat breeds");
        Long dogCatId = insertCategory("Dogs", "All domestic dog breeds");

        // API seeding
        if (catApiKey.isBlank()) {
            log.warn("[Seeder] app.catapi.key is empty — skipping cat breed seeding.");
        } else {
            seedCats(catCatId, new Long[]{owner1, owner2, adminId});
        }

        if (dogApiKey.isBlank()) {
            log.warn("[Seeder] app.dogapi.key is empty — skipping dog breed seeding.");
        } else {
            seedDogs(dogCatId, new Long[]{owner2, owner1, adminId});
        }

        log.info("[Seeder] Database seeded successfully.");
    }

    // ── Cat API ──────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void seedCats(Long categoryId, Long[] owners) {
        try {
            HttpEntity<Void> auth = authHeader(catApiKey);
            List<Map<String, Object>> breeds = fetchAllBreeds(CAT_BASE, auth);
            int i = 0;
            for (Map<String, Object> b : breeds) {
                String name        = str(b, "name");
                String description = str(b, "description");
                String temperament = str(b, "temperament");
                int    age         = midLifeSpan(str(b, "life_span"));
                double weight      = parseWeightMetric(b, "weight");
                String imageUrl    = fetchImageUrl(CAT_BASE, str(b, "reference_image_id"), auth);

                Long petId = insertPet(name, "Cat", name, age,
                        i % 2 == 0 ? "Female" : "Male",
                        description + " Temperament: " + temperament,
                        imageUrl, owners[i % owners.length], categoryId);

                insertHealth(petId, weight, "Temperament: " + temperament);
                insertGem(petId, owners[0], i % 3 == 0 ? "ACHIEVEMENT" : "DAILY", (i + 1) * 10);
                i++;
            }
            log.info("[Seeder] {} cat breeds inserted.", i);
        } catch (Exception e) {
            log.error("[Seeder] Cat seed failed: {}", e.getMessage());
        }
    }

    // ── Dog API ──────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void seedDogs(Long categoryId, Long[] owners) {
        try {
            HttpEntity<Void> auth = authHeader(dogApiKey);
            List<Map<String, Object>> breeds = fetchAllBreeds(DOG_BASE, auth);
            int i = 0;
            for (Map<String, Object> b : breeds) {
                String name        = str(b, "name");
                String breedGroup  = str(b, "breed_group");
                String temperament = str(b, "temperament");
                int    age         = midLifeSpan(str(b, "life_span"));
                double weight      = parseWeightMetric(b, "weight");
                String imageUrl    = fetchImageUrl(DOG_BASE, str(b, "reference_image_id"), auth);

                Long petId = insertPet(name, "Dog", name, age,
                        i % 2 == 0 ? "Male" : "Female",
                        "Breed group: " + breedGroup + ". Temperament: " + temperament,
                        imageUrl, owners[i % owners.length], categoryId);

                insertHealth(petId, weight, "Temperament: " + temperament);
                insertGem(petId, owners[0], i % 3 == 0 ? "BONUS" : "DAILY", (i + 2) * 10);
                i++;
            }
            log.info("[Seeder] {} dog breeds inserted.", i);
        } catch (Exception e) {
            log.error("[Seeder] Dog seed failed: {}", e.getMessage());
        }
    }

    // ── DB helpers ───────────────────────────────────────────────────────────

    private Long insertUser(String username, String email, String raw, String role) {
        return jdbc.queryForObject(
                "INSERT INTO users (username, email, password, role) VALUES (?,?,?,?) RETURNING id",
                Long.class, username, email, passwordEncoder.encode(raw), role);
    }

    private Long insertCategory(String name, String desc) {
        return jdbc.queryForObject(
                "INSERT INTO categories (name, description) VALUES (?,?) RETURNING id",
                Long.class, name, desc);
    }

    private Long insertPet(String name, String species, String breed, int age,
                           String gender, String desc, String imageUrl,
                           Long ownerId, Long categoryId) {
        return jdbc.queryForObject("""
                INSERT INTO pets (name, species, breed, age, gender, description,
                                  image_url, owner_id, category_id, gems)
                VALUES (?,?,?,?,?,?,?,?,?,0) RETURNING id
                """, Long.class,
                name, species, breed, age, gender, desc, imageUrl, ownerId, categoryId);
    }

    private void insertHealth(Long petId, double weight, String notes) {
        Long healthId = jdbc.queryForObject("""
                INSERT INTO pet_health (pet_id, weight, last_checkup, notes)
                VALUES (?,?,?,?) RETURNING id
                """, Long.class,
                petId, weight,
                LocalDate.now().minusMonths(new Random().nextInt(12) + 1L),
                notes);

        // Insert sample vaccinations
        for (String vax : List.of("Rabies", "FVRCP")) {
            jdbc.update("INSERT INTO pet_vaccinations (pet_health_id, vaccination) VALUES (?,?)",
                    healthId, vax);
        }
    }

    private void insertGem(Long petId, Long userId, String type, int amount) {
        jdbc.update("INSERT INTO gems (pet_id, user_id, type, amount) VALUES (?,?,?,?)",
                petId, userId, type, amount);
        jdbc.update("UPDATE pets SET gems = gems + ? WHERE id = ?", amount, petId);
    }

    // ── API helpers ──────────────────────────────────────────────────────────

    private HttpEntity<Void> authHeader(String key) {
        HttpHeaders h = new HttpHeaders();
        h.set("x-api-key", key);
        return new HttpEntity<>(h);
    }

    /** Fetches ALL breeds by paginating page-by-page (limit=100) until an empty page is returned. */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchAllBreeds(String base, HttpEntity<Void> auth) {
        List<Map<String, Object>> all = new ArrayList<>();
        int page = 0;
        final int limit = 100;
        while (true) {
            String url = base + "/breeds?limit=" + limit + "&page=" + page;
            log.debug("[Seeder] Fetching breeds page {}: {}", page, url);
            ResponseEntity<List> resp = restTemplate.exchange(url, HttpMethod.GET, auth, List.class);
            List<Map<String, Object>> batch = resp.getBody();
            if (batch == null || batch.isEmpty()) break;
            all.addAll(batch);
            log.info("[Seeder] Page {} → {} breeds (running total: {})", page, batch.size(), all.size());
            if (batch.size() < limit) break; // last page — no need for another round-trip
            page++;
        }
        return all;
    }

    @SuppressWarnings("unchecked")
    private String fetchImageUrl(String base, String refId, HttpEntity<Void> auth) {
        if (refId == null || refId.isBlank()) return null;
        try {
            ResponseEntity<Map> resp = restTemplate.exchange(
                    base + "/images/" + refId, HttpMethod.GET, auth, Map.class);
            Map<String, Object> body = resp.getBody();
            return body != null ? (String) body.get("url") : null;
        } catch (Exception e) {
            return null;
        }
    }

    /** Parses "10 - 14 years" or "10 - 14" → midpoint integer */
    private int midLifeSpan(String raw) {
        if (raw == null) return 5;
        try {
            String cleaned = raw.replaceAll("[^0-9\\-]", "");
            String[] parts = cleaned.split("-");
            if (parts.length == 2) {
                return (Integer.parseInt(parts[0].trim()) + Integer.parseInt(parts[1].trim())) / 2;
            }
            return Integer.parseInt(parts[0].trim());
        } catch (Exception e) { return 5; }
    }

    /** Extracts the metric weight midpoint from { "metric": "3 - 5" } */
    @SuppressWarnings("unchecked")
    private double parseWeightMetric(Map<String, Object> breed, String key) {
        try {
            Object nested = breed.get(key);
            if (nested instanceof Map<?,?> m) {
                String metric = (String) m.get("metric");
                if (metric != null) {
                    String[] parts = metric.split("-");
                    return Double.parseDouble(parts[0].trim());
                }
            }
        } catch (Exception ignored) {}
        return 0.0;
    }

    private String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v instanceof String s ? s : "";
    }
}
