package com.vlosco.backend.service;

import net.datafaker.Faker;

import com.vlosco.backend.data.AnnonceData;
import com.vlosco.backend.data.InteractionData;
import com.vlosco.backend.data.VehicleData;
import com.vlosco.backend.dto.AnnonceCreationDTO;
import com.vlosco.backend.dto.AnnonceDetailsCreationDTO;
import com.vlosco.backend.dto.VehicleCreationDTO;
import com.vlosco.backend.enums.UserRole;
import com.vlosco.backend.enums.UserType;
import com.vlosco.backend.model.Annonce;
import com.vlosco.backend.model.Interaction;
import com.vlosco.backend.model.User;
import com.vlosco.backend.repository.AnnonceRepository;
import com.vlosco.backend.repository.InteractionRepository;
import com.vlosco.backend.repository.UserRepository;
import com.vlosco.backend.repository.VehicleRepository;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DataSeederService {
        private static final Logger log = LoggerFactory.getLogger(DataSeederService.class);
        private static final int BATCH_SIZE = 50;
        private final UserRepository userRepository;
        private final AnnonceService annonceService;
        private final PasswordService passwordService;
        private final AnnonceRepository annonceRepository;
        private final InteractionRepository interactionRepository;
        private final Faker faker;
        private final Random random;

        public DataSeederService(
                        UserRepository userRepository,
                        VehicleRepository vehicleRepository,
                        AnnonceRepository annonceRepository,
                        InteractionRepository interactionRepository,
                        PasswordService passwordService,
                        AnnonceService annonceService) {
                this.userRepository = userRepository;
                this.annonceService = annonceService;
                this.passwordService = passwordService;
                this.annonceRepository = annonceRepository;
                this.interactionRepository = interactionRepository;
                this.faker = new Faker(new Locale("fr"));
                this.random = new Random();
        }

        @PostConstruct
        @Transactional
        public void seedData() {
                try {
                        if (userRepository.count() == 0) {
                                log.info("Début du seeding des données...");
                                seedUsersInBatches();
                                seedVehiclesAndAnnoncesBatched();
                                seedInteractionsInBatches();
                                log.info("Seeding terminé avec succès");
                        }
                } catch (Exception e) {
                        log.error("Erreur lors du seeding: {}", e.getMessage(), e);
                }
        }

        @Transactional
        private void seedUsersInBatches() {
                List<User> userBatch = new ArrayList<>();
                String[] firstNames = {"Thomas", "Nicolas", "Julien", "Pierre", "Marie", "Sophie", "Julie", "Emma", "Lucas", "Hugo"};
                String[] lastNames = {"Martin", "Bernard", "Dubois", "Petit", "Robert", "Richard", "Durand", "Moreau", "Simon", "Laurent"};
                
                for (int i = 0; i < 2000; i++) {
                        User user = new User();
                        String firstName = firstNames[random.nextInt(firstNames.length)];
                        String lastName = lastNames[random.nextInt(lastNames.length)];
                        user.setFirstName(firstName);
                        user.setLastName(lastName);
                        user.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com");
                        user.setPassword(passwordService.hashPassword("password@123"));
                        user.setPhoneNumber(faker.numerify("+33 " + (random.nextBoolean() ? "6" : "7") + "########"));
                        user.setCity(faker.options().option(AnnonceData.CITY_ITEMS));
                        user.setBirthDate(LocalDate.now().minusYears(faker.number().numberBetween(25, 55)));
                        user.setEmailVerified(true);
                        user.setRole(UserRole.USER);
                        user.setType(UserType.REGULAR);
                        userBatch.add(user);

                        if (userBatch.size() >= BATCH_SIZE) {
                                userRepository.saveAll(userBatch);
                                userBatch.clear();
                        }
                }
                if (!userBatch.isEmpty()) {
                        userRepository.saveAll(userBatch);
                }
        }

        @Transactional
        private void seedVehiclesAndAnnoncesBatched() {
                List<User> users = userRepository.findAll();
                List<AnnonceCreationDTO> batch = new ArrayList<>();

                Map<String, List<String>> carModels = VehicleData.carModels;
                List<String> carBrands = new ArrayList<>(carModels.keySet());

                Map<String, List<String>> motoModels = VehicleData.motoModels;
                List<String> motoBrands = new ArrayList<>(motoModels.keySet());

                String[] typeVehiculeItems = AnnonceData.TYPE_VEHICULE_ITEMS;
                String[] transactionItems = AnnonceData.TRANSACTION_ITEMS;
                String[] conditionItems = AnnonceData.CONDITION_ITEMS;
                String[] typeGearBoxItems = AnnonceData.TYPE_GEARBOX_ITEMS;
                String[] climatisationItems = AnnonceData.CLIMATISATION_ITEMS;
                String[] fuelTypeItems = AnnonceData.FUEL_TYPE_ITEMS;
                String[] colorItems = AnnonceData.COLOR_ITEMS;
                String[] categoryItemsCars = AnnonceData.CATEGORY_ITEMS_CARS;
                String[] categoryItemsMotos = AnnonceData.CATEGORY_ITEMS_MOTOS;
                String[] cityItems = AnnonceData.CITY_ITEMS;

                for (int i = 0; i < 1500; i++) {
                        AnnonceCreationDTO annonceCreationDTO = new AnnonceCreationDTO();
                        AnnonceDetailsCreationDTO annonceDetails = new AnnonceDetailsCreationDTO();
                        VehicleCreationDTO vehicleDetails = new VehicleCreationDTO();

                        String typeVehicle = faker.options().option(typeVehiculeItems);
                        String transaction = faker.options().option(transactionItems);
                        String condition = faker.options().option(conditionItems);
                        String gearbox = faker.options().option(typeGearBoxItems);
                        String climatisation = faker.options().option(climatisationItems);
                        String fuelType = faker.options().option(fuelTypeItems);
                        String color = faker.options().option(colorItems);
                        String categoryCar = faker.options().option(categoryItemsCars);
                        String categoryMoto = faker.options().option(categoryItemsMotos);
                        String city = faker.options().option(cityItems);

                        String carBrand = carBrands.get(random.nextInt(carBrands.size()));
                        String motoBrand = motoBrands.get(random.nextInt(motoBrands.size()));

                        String title;
                        String model;
                        String brand;
                        String category;
                        List<String> images = new ArrayList<>();
                        int minKm;
                        int maxKm;
                        int minPrice;
                        int maxPrice;
                        int minRentalPrice;
                        int maxRentalPrice;

                        if (typeVehicle.equals("Voiture")) {
                                List<String> allModels = carModels.get(carBrand);
                                model = allModels.get(random.nextInt(allModels.size()));
                                brand = carBrand;
                                category = categoryCar;
                                String etat = condition.equals("Neuf") ? "Neuve" : "Occasion";
                                minKm = 5000;
                                maxKm = 150000;
                                minPrice = 5000;
                                maxPrice = 45000;
                                minRentalPrice = 50;
                                maxRentalPrice = 200;
                                title = String.format("%s %s %s %s - %s %s km",
                                                brand, model, etat,
                                                faker.number().numberBetween(2010, 2024),
                                                faker.number().numberBetween(minKm, maxKm),
                                                fuelType);
                                images.add("https://i.pinimg.com/736x/89/e3/9f/89e39f7142f79ffd51212336c212d632.jpg");
                                images.add("https://i.pinimg.com/736x/f2/c5/b6/f2c5b6adfcef67811cc3daf5cab67e4f.jpg");
                                images.add("https://i.pinimg.com/736x/0a/e0/f9/0ae0f96c175ab4928a9cab8ad8f8b90b.jpg");
                        } else {
                                List<String> allMotoModels = motoModels.get(motoBrand);
                                model = allMotoModels.get(random.nextInt(allMotoModels.size()));
                                brand = motoBrand;
                                category = categoryMoto;
                                minKm = 1000;
                                maxKm = 50000;
                                minPrice = 2000;
                                maxPrice = 15000;
                                minRentalPrice = 30;
                                maxRentalPrice = 100;
                                title = String.format("%s %s %s - %s km - %s",
                                                brand, model,
                                                faker.number().numberBetween(2010, 2024),
                                                faker.number().numberBetween(minKm, maxKm),
                                                condition);
                                images.add("https://i.pinimg.com/736x/3a/00/03/3a0003928caa1ecb8eedd782ea48ad6c.jpg");
                                images.add("https://i.pinimg.com/736x/d8/e3/7c/d8e37c93acf7970bf26e8be4b1cb6b3c.jpg");
                                images.add("https://i.pinimg.com/736x/cb/a1/12/cba1121a03dccfdb354fedf7121ef26c.jpg");
                        }

                        annonceDetails.setTitle(title);
                        annonceDetails.setTransaction(transaction);

                        if (transaction.equals("Vente")) {
                                annonceDetails.setPrice(String.valueOf(faker.number().numberBetween(minPrice, maxPrice)));
                                annonceDetails.setQuantity(1);
                        } else {
                                annonceDetails.setPrice(String.valueOf(faker.number().numberBetween(minRentalPrice, maxRentalPrice)));
                                annonceDetails.setQuantity(1);
                        }

                        int kmCount = faker.number().numberBetween(minKm, maxKm);
                        vehicleDetails.setKlm_counter(String.valueOf(kmCount));
                        annonceDetails.setCity(city);
                        annonceDetails.setPhoneNumber(faker.numerify("+33 " + (random.nextBoolean() ? "6" : "7") + "########"));
                        annonceDetails.setUserId(users.get(random.nextInt(users.size())).getUserId());
                        annonceCreationDTO.setAnnonce(annonceDetails);

                        vehicleDetails.setType(typeVehicle);
                        vehicleDetails.setMark(brand);
                        vehicleDetails.setModel(model);
                        vehicleDetails.setCategory(category);

                        vehicleDetails.setYear(faker.number().numberBetween(2010, 2024));
                        vehicleDetails.setGearbox(gearbox);
                        vehicleDetails.setClimatisation(climatisation);
                        vehicleDetails.setCondition(condition);
                        vehicleDetails.setFuelType(fuelType);
                        vehicleDetails.setColor(color);

                        String description;
                        if (condition.equals("Neuf")) {
                            description = String.format("Superbe %s %s %s neuf%s. %s, %s. Disponible immédiatement pour %s.",
                                brand, model, typeVehicle.toLowerCase(), typeVehicle.equals("Voiture") ? "ve" : "",
                                gearbox, climatisation, transaction.toLowerCase());
                        } else {
                            description = String.format("%s %s %s de %d avec %d km. %s, %s, %s. Véhicule bien entretenu, disponible pour %s.",
                                brand, model, typeVehicle.toLowerCase(),
                                vehicleDetails.getYear(), kmCount,
                                gearbox, climatisation, fuelType,
                                transaction.toLowerCase());
                        }
                        
                        vehicleDetails.setDescription(description);
                        annonceCreationDTO.setVehicle(vehicleDetails);
                        annonceCreationDTO.setImages(images);

                        batch.add(annonceCreationDTO);

                        if (batch.size() >= BATCH_SIZE) {
                                saveAnnonceBatch(batch);
                                batch.clear();
                        }
                }
                if (!batch.isEmpty()) {
                        saveAnnonceBatch(batch);
                }
        }

        @Transactional
        private void saveAnnonceBatch(List<AnnonceCreationDTO> batch) {
                for (AnnonceCreationDTO dto : batch) {
                        try {
                                annonceService.createAnnonce(dto);
                        } catch (Exception e) {
                                log.error("Erreur lors de la création d'une annonce: {}", e.getMessage());
                        }
                }
        }

        @Transactional
        private void seedInteractionsInBatches() {
                List<User> users = userRepository.findAll();
                List<Annonce> annonces = annonceRepository.findAll();
                List<Interaction> interactionBatch = new ArrayList<>();

                String[] INTERACTIONS_TYPES = InteractionData.INTERACTIONS_TYPES;

                if (users.isEmpty() || annonces.isEmpty()) {
                        return;
                }

                for (User user : users) {
                        if (random.nextInt(100) < 5)
                                continue;

                        int numberOfInteractions = random.nextInt(20) + 1;
                        for (int j = 0; j < numberOfInteractions; j++) {
                                Interaction interaction = new Interaction();
                                Annonce annonce = annonces.get(random.nextInt(annonces.size()));
                                String actualInteractionType = faker.options().option(INTERACTIONS_TYPES);

                                interaction.setUser(user);
                                interaction.setAnnonce(annonce);
                                interaction.setInteractionType(actualInteractionType);

                                if (actualInteractionType.equals("Search")) {
                                        String[] searchTerms = {
                                            "voiture occasion", "voiture pas cher", "voiture propre",
                                            "moto sportive", "moto custom", "moto trail", "moto routière",
                                            "4x4 diesel", "4x4 essence", "4x4 hybride", "4x4 électrique",
                                            "citadine essence", "citadine électrique", "citadine hybride",
                                            "utilitaire", "utilitaire grand volume", "utilitaire frigorifique",
                                            "berline automatique", "berline luxe", "berline familiale",
                                            "break familial", "break occasion", "break diesel",
                                            "cabriolet", "coupé sport", "SUV familial",
                                            "voiture collection", "voiture ancienne", "voiture neuve",
                                            "scooter 125", "scooter électrique", "quad",
                                            "camping car", "van aménagé", "fourgon aménagé",
                                            "voiture boite auto", "voiture faible kilométrage",
                                            "voiture première main", "voiture garantie",
                                            "moto permis A2", "moto petit budget",
                                            "véhicule commercial", "véhicule société"
                                        };
                                        interaction.setContent(searchTerms[random.nextInt(searchTerms.length)]);
                                }

                                interactionBatch.add(interaction);

                                if (interactionBatch.size() >= BATCH_SIZE) {
                                        interactionRepository.saveAll(interactionBatch);
                                        interactionBatch.clear();
                                }
                        }
                }
                if (!interactionBatch.isEmpty()) {
                        interactionRepository.saveAll(interactionBatch);
                }
        }
}