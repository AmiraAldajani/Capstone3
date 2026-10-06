package com.example.ejadwebapplication.Config;

import com.example.ejadwebapplication.Enums.LocationType;
import com.example.ejadwebapplication.Model.*;
import com.example.ejadwebapplication.Repository.AdminRepository;
import com.example.ejadwebapplication.Repository.LocationRepository;
import com.example.ejadwebapplication.Repository.StaffRepository;
import com.example.ejadwebapplication.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// نعبي بيانات تجريبية أول مرة يشتغل فيها المشروع
// TODO: لما يخلص العضو 2 و 3، تنضاف هنا Categories و Reports
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final LocationRepository locationRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;

    @Override
    public void run(String... args) {
        // إذا البيانات موجودة من تشغيل سابق، لا تكررها
        if (locationRepository.count() > 0) {
            return;
        }

        Location airport = createLocation("King Khalid International Airport",
                "Lost and found desk, Terminal 1", "Riyadh", LocationType.AIRPORT);
        Location mall = createLocation("Riyadh Park",
                "Customer service desk, ground floor", "Riyadh", LocationType.MALL);
        Location metro = createLocation("KAFD Metro Station",
                "Station security office", "Riyadh", LocationType.METRO);
        Location university = createLocation("King Saud University",
                "Campus security office", "Riyadh", LocationType.UNIVERSITY);
        locationRepository.saveAll(List.of(airport, mall, metro, university));

        Admin admin = fillAccount(new Admin(), "System Admin", "admin",
                "admin@example.com", "Admin1234", "0500000000");
        adminRepository.save(admin);

        User sara = fillAccount(new User(), "Sara Alqahtani", "sara",
                "sara@example.com", "Sara1234", "0511111111");
        User fahad = fillAccount(new User(), "Fahad Alotaibi", "fahad",
                "fahad@example.com", "Fahad1234", "0522222222");
        userRepository.saveAll(List.of(sara, fahad));

        Staff khalid = fillAccount(new Staff(), "Khalid Alharbi", "khalid.staff",
                "khalid.staff@example.com", "Khalid1234", "0533333333");
        khalid.setLocation(airport);
        khalid.setIsVerified(true);

        // غير موثّقة عشان تجربون endpoint التوثيق
        Staff noura = fillAccount(new Staff(), "Noura Alshehri", "noura.staff",
                "noura.staff@example.com", "Noura1234", "0544444444");
        noura.setLocation(mall);
        noura.setIsVerified(false);

        staffRepository.saveAll(List.of(khalid, noura));
    }

    private Location createLocation(String name, String description, String city, LocationType type) {
        Location location = new Location();
        location.setName(name);
        location.setDescription(description);
        location.setCity(city);
        location.setType(type);
        return location;
    }

    private <T extends BaseAccount> T fillAccount(T account, String fullName, String username,
                                                   String email, String password, String phone) {
        account.setFullName(fullName);
        account.setUsername(username);
        account.setEmail(email);
        account.setPassword(password);
        account.setPhone(phone);
        return account;
    }
}
