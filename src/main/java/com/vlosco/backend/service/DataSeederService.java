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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class DataSeederService {
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
        public void seedData() {
                if (userRepository.count() == 0) {
                        seedUsers();
                        seedVehiclesAndAnnonces();
                        seedInteractions();
                }
        }

        private void seedUsers() {
                List<User> users = new ArrayList<>();
                for (int i = 0; i < 10000; i++) {
                        User user = new User();
                        user.setFirstName(faker.name().firstName());
                        user.setLastName(faker.name().lastName());
                        user.setEmail(faker.internet().emailAddress());
                        user.setPassword(passwordService.hashPassword("password123"));
                        user.setPhoneNumber(faker
                                        .numerify(random.nextBoolean() ? "+33 6 ## ## ## ##" : "+33 7 ## ## ## ##"));
                        user.setCity(faker.options().option(AnnonceData.CITY_ITEMS));
                        user.setBirthDate(LocalDate.now().minusYears(faker.number().numberBetween(18, 70)));
                        user.setEmailVerified(true);
                        user.setRole(UserRole.USER);
                        user.setType(UserType.REGULAR);
                        users.add(user);
                }
                userRepository.saveAll(users);
        }

        private void seedVehiclesAndAnnonces() {
                List<User> users = userRepository.findAll();

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

                for (int i = 0; i < 10000; i++) {
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

                        int wordCountTitle = faker.number().numberBetween(2, 11);
                        String title = String.join(" ", faker.lorem().words(wordCountTitle));
                        annonceDetails.setTitle(title);
                        annonceDetails.setTransaction(transaction);

                        if (transaction.equals("Vente")) {
                                int randomChance = random.nextInt(100);
                                annonceDetails.setPrice(String.valueOf(faker.number().numberBetween(3000, 90000)));
                                annonceDetails.setQuantity(
                                                randomChance >= 90 ? faker.number().numberBetween(1, 30) : 1);
                        } else {
                                annonceDetails.setPrice(String.valueOf(faker.number().numberBetween(30, 1500)));
                                annonceDetails.setQuantity(1);
                        }

                        vehicleDetails.setKlm_counter(String.valueOf(faker.number().numberBetween(0, 200000)));
                        annonceDetails.setCity(city);
                        annonceDetails.setPhoneNumber(faker
                                        .numerify(random.nextBoolean() ? "+33 6 ## ## ## ##" : "+33 7 ## ## ## ##"));
                        annonceDetails.setUserId(users.get(random.nextInt(users.size())).getUserId());
                        annonceCreationDTO.setAnnonce(annonceDetails);

                        vehicleDetails.setType(typeVehicle);

                        if (typeVehicle.equals("Voiture")) {
                                List<String> allModels = carModels.get(carBrand);
                                String carModel = allModels.get(random.nextInt(allModels.size()));
                                vehicleDetails.setMark(carBrand);
                                vehicleDetails.setModel(carModel);
                                vehicleDetails.setCategory(categoryCar);
                        } else {
                                List<String> allMotoModels = motoModels.get(motoBrand);
                                String motoModel = allMotoModels.get(random.nextInt(allMotoModels.size()));
                                vehicleDetails.setMark(motoBrand);
                                vehicleDetails.setModel(motoModel);
                                vehicleDetails.setCategory(categoryMoto);
                        }

                        vehicleDetails.setYear(faker.number().numberBetween(1970, 2024));
                        vehicleDetails.setGearbox(gearbox);
                        vehicleDetails.setClimatisation(climatisation);
                        vehicleDetails.setCondition(condition);
                        vehicleDetails.setFuelType(fuelType);
                        vehicleDetails.setColor(color);
                        vehicleDetails.setDescription(
                                        String.join(" ", faker.lorem().words(faker.number().numberBetween(10, 40))));
                        annonceCreationDTO.setVehicle(vehicleDetails);

                        List<String> images = new ArrayList<>();
                        images.add("https://next-images.123rf.com/index/_next/image/?url=https://assets-cdn.123rf.com/index/static/assets/top-section-bg.jpeg&w=3840&q=75");
                        images.add("https://images.pexels.com/photos/1302434/pexels-photo-1302434.jpeg?cs=srgb&dl=pexels-alexfu-1302434.jpg&fm=jpg");
                        annonceCreationDTO.setImages(images);

                        annonceService.createAnnonce(annonceCreationDTO);
                }
        }

        private void seedInteractions() {
                List<User> users = userRepository.findAll();
                List<Annonce> annonces = annonceRepository.findAll();
                String[] INTERACTIONS_TYPES = InteractionData.INTERACTIONS_TYPES;

                if (users.isEmpty() || annonces.isEmpty()) {
                        return;
                }

                // Sélectionner 40 utilisateurs au hasard qui n'auront pas d'interactions
                List<User> usersWithoutInteractions = new ArrayList<>(users);
                Collections.shuffle(usersWithoutInteractions);
                Set<Long> excludedUserIds = usersWithoutInteractions.subList(0, 40).stream()
                        .map(User::getUserId)
                        .collect(Collectors.toSet());

                for (User user : users) {
                        if (excludedUserIds.contains(user.getUserId())) {
                                continue;
                        }

                        int numberOfInteractions = random.nextInt(500);

                        for (int j = 0; j < numberOfInteractions; j++) {
                                Interaction interaction = new Interaction();
                                Annonce annonce = annonces.get(random.nextInt(annonces.size()));
                                String actualInteractionType = faker.options().option(INTERACTIONS_TYPES);

                                interaction.setUser(user);
                                interaction.setAnnonce(annonce);
                                interaction.setInteractionType(actualInteractionType);

                                if (actualInteractionType.equals("Search")) {
                                        interaction.setContent(String.join(" ",
                                                        faker.lorem().words(faker.number().numberBetween(1, 7))));
                                }

                                interactionRepository.save(interaction);
                        }
                }
        }
}
