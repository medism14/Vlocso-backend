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
import java.util.HashMap;
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
                Map<String, String> firstNamesMap = new HashMap<>();
                List<String> namesMap = new ArrayList<>();
                firstNamesMap.put("Thomas",
                                "https://img.freepik.com/vecteurs-premium/vecteur-icone-homme-affaires_52756-255.jpg");
                firstNamesMap.put("Nicolas",
                                "https://img.freepik.com/vecteurs-premium/vecteur-icone-homme-affaires_52756-255.jpg");
                firstNamesMap.put("Julien",
                                "https://img.freepik.com/vecteurs-premium/vecteur-icone-homme-affaires_52756-255.jpg");
                firstNamesMap.put("Pierre",
                                "https://img.freepik.com/vecteurs-premium/vecteur-icone-homme-affaires_52756-255.jpg");
                firstNamesMap.put("Marie",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzPRQ6LprnPzvvP-_vVO_nhSokwda8CMsnwQ&s");
                firstNamesMap.put("Sophie",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzPRQ6LprnPzvvP-_vVO_nhSokwda8CMsnwQ&s");
                firstNamesMap.put("Julie",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzPRQ6LprnPzvvP-_vVO_nhSokwda8CMsnwQ&s");
                firstNamesMap.put("Emma",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzPRQ6LprnPzvvP-_vVO_nhSokwda8CMsnwQ&s");
                firstNamesMap.put("Lucas",
                                "https://img.freepik.com/vecteurs-premium/vecteur-icone-homme-affaires_52756-255.jpg");
                firstNamesMap.put("Hugo",
                                "https://img.freepik.com/vecteurs-premium/vecteur-icone-homme-affaires_52756-255.jpg");
                firstNamesMap.put("Alice",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzPRQ6LprnPzvvP-_vVO_nhSokwda8CMsnwQ&s");
                firstNamesMap.put("Léa",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzPRQ6LprnPzvvP-_vVO_nhSokwda8CMsnwQ&s");
                firstNamesMap.put("Chloé",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzPRQ6LprnPzvvP-_vVO_nhSokwda8CMsnwQ&s");
                firstNamesMap.put("Maxime",
                                "https://img.freepik.com/vecteurs-premium/vecteur-icone-homme-affaires_52756-255.jpg");
                firstNamesMap.put("Antoine",
                                "https://img.freepik.com/vecteurs-premium/vecteur-icone-homme-affaires_52756-255.jpg");
                firstNamesMap.put("Clara",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzPRQ6LprnPzvvP-_vVO_nhSokwda8CMsnwQ&s");
                firstNamesMap.put("Gabriel",
                                "https://img.freepik.com/vecteurs-premium/vecteur-icone-homme-affaires_52756-255.jpg");
                firstNamesMap.put("Louis",
                                "https://img.freepik.com/vecteurs-premium/vecteur-icone-homme-affaires_52756-255.jpg");
                firstNamesMap.put("Inès",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzPRQ6LprnPzvvP-_vVO_nhSokwda8CMsnwQ&s");
                firstNamesMap.put("Émilie",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzPRQ6LprnPzvvP-_vVO_nhSokwda8CMsnwQ&s");
                firstNamesMap.put("Victor",
                                "https://img.freepik.com/vecteurs-premium/vecteur-icone-homme-affaires_52756-255.jpg");
                firstNamesMap.put("Noah",
                                "https://img.freepik.com/vecteurs-premium/vecteur-icone-homme-affaires_52756-255.jpg");
                firstNamesMap.put("Camille",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzPRQ6LprnPzvvP-_vVO_nhSokwda8CMsnwQ&s");
                firstNamesMap.put("Juliette",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzPRQ6LprnPzvvP-_vVO_nhSokwda8CMsnwQ&s");
                firstNamesMap.put("Théo",
                                "https://img.freepik.com/vecteurs-premium/vecteur-icone-homme-affaires_52756-255.jpg");
                firstNamesMap.put("Mathilde",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzPRQ6LprnPzvvP-_vVO_nhSokwda8CMsnwQ&s");
                firstNamesMap.put("Raphaël",
                                "https://img.freepik.com/vecteurs-premium/vecteur-icone-homme-affaires_52756-255.jpg");
                firstNamesMap.put("Zoé",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzPRQ6LprnPzvvP-_vVO_nhSokwda8CMsnwQ&s");

                namesMap.add("Dupont");
                namesMap.add("Martin");
                namesMap.add("Bernard");
                namesMap.add("Leroy");
                namesMap.add("Moreau");
                namesMap.add("Garnier");
                namesMap.add("Rousseau");
                namesMap.add("Gauthier");
                namesMap.add("Chevalier");
                namesMap.add("Lemoine");
                namesMap.add("Boucher");
                namesMap.add("Pichon");
                namesMap.add("Collet");
                namesMap.add("Lefevre");
                namesMap.add("Blanc");
                namesMap.add("Giraud");
                namesMap.add("Dufour");
                namesMap.add("Bourgeois");
                namesMap.add("Leroux");
                namesMap.add("Lemoine");
                namesMap.add("Fournier");
                namesMap.add("Gauthier");
                namesMap.add("Renaud");
                namesMap.add("Leroy");
                namesMap.add("Marchand");

                List<String> firstNames = new ArrayList<>(firstNamesMap.keySet());

                for (int i = 0; i < 1500; i++) {
                        User user = new User();
                        String firstName = firstNames.get(random.nextInt(firstNames.size()));
                        String lastName = namesMap.get(random.nextInt(namesMap.size()));

                        user.setFirstName(firstName);
                        user.setLastName(lastName);
                        user.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com");
                        user.setPassword(passwordService.hashPassword("password@123"));
                        user.setPhoneNumber(
                                        faker.numerify("+33 " + (random.nextBoolean() ? "6" : "7") + "## ## ## ##"));
                        user.setCity(faker.options().option(AnnonceData.CITY_ITEMS));
                        user.setBirthDate(LocalDate.now().minusYears(faker.number().numberBetween(20, 55)));
                        user.setEmailVerified(true);
                        user.setRole(UserRole.USER);
                        user.setType(UserType.REGULAR);
                        user.setUrlImageUser(firstNamesMap.get(firstName));

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
                HashMap<String, String[]> imagesCars = new HashMap<String, String[]>() {
                        {
                                put("Rouge", new String[] {
                                                "https://www.luxury-club.fr/blog/wp-content/uploads/2023/01/IMG_7291-scaled.jpg",
                                                "https://www.auto-infos.fr/mediatheque/2/4/4/000252442_896x598_c.png",
                                                "https://blogautomobile.fr/wp-content/uploads/2009/12/ferrari_f430-novitec_r10.jpg",
                                                "https://st2.depositphotos.com/1063296/8337/i/450/depositphotos_83378584-stock-photo-modern-compact-red-car-front.jpg",
                                                "https://serveur-statique.jam-difus.com/_telechargement/variance-auto/film/Covering-3M-2024-avant-G363-webp.webp?v=1" });
                                put("Bleu", new String[] {
                                                "https://images.bfmtv.com/90WE7QwsR4egwXBnYcMGKCN84TI=/4x3:1252x705/1248x0/images/-130182.jpg",
                                                "https://img.freepik.com/photos-premium/voiture-luxe-bleu-cobalt-vehicule-elegant-blanc-image-image-conception-voiture-moderne_1020697-748458.jpg?semt=ais_hybrid",
                                                "https://sf2.autoplus.fr/wp-content/uploads/autoplus/2020/01/et-plus-belle-voiture-annee-2019-est.jpg",
                                                "https://www.marketoy.com/media/catalog/product/cache/ffcb31fde135ed06fccea0a9cd4e2c34/4/3/4311904.jpg",
                                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSZRBQETWWBG5HRP1dhEE_XfWxhQmhqv39Xfg&s" });
                                put("Vert", new String[] {
                                                "https://voiture.kidioui.fr/image/img-auto/peugeot-308.jpg",
                                                "https://images.caradisiac.com/images/8/8/0/4/188804/S1-la-peinture-verte-tendance-2021-666459.jpg",
                                                "https://www.moteurnature.com/zvisu/1815/92/vert-Porsche-911.jpg",
                                                "https://www.largus.fr/images/images/ferrari-812-superfast-configurateur-9_1.jpg",
                                                "https://www.mon-code-peinture.com/img/modeles/code-peinture-renault-megane.jpg" });
                                put("Noir", new String[] {
                                                "https://i.gaw.to/content/photos/47/43/474302-bugatti-la-voiture-noire-la-voici-maintenant-en-vrai.jpg?1024x640",
                                                "https://cdn.motor1.com/images/mgl/wBNQm/s3/bugatti-la-voiture-noire.jpg",
                                                "https://cdn.webshopapp.com/shops/332413/files/420461150/image.jpg",
                                                "https://www.bmw.com/content/dam/bmw/marketBMWCOM/bmw_com/categories/Design/vantablack/vb-04-media-hd.jpg",
                                                "https://www.french-luxe.com/images/2023/moteurs/bugatti-voiture-noire-image2.webp" });
                                put("Blanc", new String[] {
                                                "https://images.caradisiac.com/logos/3/3/4/8/273348/S7-pourquoi-les-voitures-blanches-sont-elles-moins-salissantes-et-moins-couteuses-en-peinture-198132.jpg",
                                                "https://sf1.autoplus.fr/wp-content/uploads/autoplus/2022/04/shutterstock_editorial_1958982268.jpg",
                                                "https://www.largus.fr/images/styles/max_1300x1300/public/images/bmw-x5-hybride-2020-blanc.jpg?itok=57Q673BF",
                                                "https://serveur-statique.jam-difus.com/_telechargement/luminis/film/webp-tesla-arriere-h8002a.webp?v=1",
                                                "https://media.istockphoto.com/id/1150931120/fr/photo/illustration-3d-de-la-voiture-compacte-blanche-générique-vue-de-face.jpg?s=612x612&w=0&k=20&c=xGw7XJJlXXB1Kmcv9TAxoXln-ancfIX-W3lAJlL9Px0=" });
                                put("Gris", new String[] {
                                                "https://img.over-blog-kiwi.com/0/93/23/39/20170505/ob_322535_p2.jpg",
                                                "https://www.elitecovering.fr/865/gris-mat.jpg",
                                                "https://classic-auto.fr/wp-content/uploads/2022/11/RS7-Sportback-Nardo-gris-1024x543.png",
                                                "https://journalauto.com/wp-content/uploads/2023/01/Austral-gris.jpg",
                                                "https://www.peintureautomoto.fr/images/blog/peinture/peinture-audi-gris-nardo.png" });
                                put("Jaune", new String[] {
                                                "https://serveur-statique.jam-difus.com/_telechargement/variance-auto/film/Covering-HX-2024-avant-8009a.webp?v=1",
                                                "https://images.caradisiac.com/images/2/8/6/8/202868/S1-les-voitures-jaunes-sont-celles-qui-se-revendraient-le-mieux-759337.jpg",
                                                "https://previews.123rf.com/images/deusexlupus/deusexlupus1712/deusexlupus171200011/91449112-jaune-jaune-voiture-de-course-de-course-tir-de-beauté.jpg",
                                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTPkONtcXD3EqOnWWRYUU8ldS_sZBztmIM2Jw&s",
                                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQr8Bvv2ANvXtPQ_Yzfho3sntB_5EKMDjWYgA&s" });
                                put("Orange", new String[] {
                                                "https://static.moniteurautomobile.be/imgcontrol/images_tmp/clients/moniteur/c680-d465/content/medias/images/news/29000/100/90/hlzyzllbsqec.jpg",
                                                "https://neuf.chanas-auto.com/public/img/medium/clio5orangejpg_6202796c584ec.jpg",
                                                "https://www.largus.fr/images/styles/max_1300x1300/public/2024-10/smart-3-2023-orange-avgh-mk.jpg?itok=u-HCH-2T",
                                                "https://images.caradisiac.com/images/6/7/3/6/196736/S1-une-voiture-de-couleur-ca-fait-peur-mais-c-est-le-bonheur-718327.jpg",
                                                "https://st2.depositphotos.com/2760050/49237/i/450/depositphotos_492370420-stock-photo-los-angeles-california-usa-april.jpg" });
                                put("Violet", new String[] {
                                                "https://cdn.actronics.nl/image/lamborghini-aventador-s-roadster-vIola-parsifae_60ba3c2219119.jpg",
                                                "https://www.turbo.fr/sites/default/files/migration/newscast/field_image/000000007862357.jpg",
                                                "https://cdn.motor1.com/images/mgl/gW3wo/s1/kvc-lamborghini-violette.jpg",
                                                "https://cdn.actronics.nl/image/ferrari-488-pista-viola-hong-kong_60ba3c92701ac.jpg",
                                                "https://images.caradisiac.com/logos/6/0/9/5/146095/S8-Video-Mercedes-SLS-AMG-et-pourquoi-pas-en-violet-mat-58566.jpg" });
                                put("Rose", new String[] {
                                                "https://serveur-statique.jam-difus.com/_telechargement/variance-auto/film/Covering-HX-2024-avant-8034a.webp?v=1",
                                                "https://ih1.redbubble.net/image.5143786108.3339/flat,750x,075,f-pad,750x1000,f8f8f8.jpg",
                                                "https://cdn.pixabay.com/photo/2022/07/22/19/33/car-7338816_1280.jpg",
                                                "https://www.turbo.fr/sites/default/files/migration/newscast/field_image/000000008165112.jpg",
                                                "https://c8.alamy.com/compfr/hkjrgj/voiture-de-couleur-rose-vif-hkjrgj.jpg" });
                        }
                };

                HashMap<String, String[]> imagesMotos = new HashMap<String, String[]>() {
                        {
                                put("Rouge", new String[] {
                                                "https://stickeramoi.com/9175/sticker-mural-moto-rouge-crouse.jpg",
                                                "https://previews.123rf.com/images/emphasize12/emphasize121506/emphasize12150600043/41456884-modèle-du-sport-moto-rouge-moto-moto-moto.jpg",
                                                "https://www.newmotorz.com/media/catalog/product/cache/3/image/375x400/9df78eab33525d08d6e5fb8d27136e95/m/o/moto_homologuee_masai_x-ray_50_rouge.jpg",
                                                "https://www.injusa.com/769-home_default/moto-honda-cbr-12v-couleur-rouge.jpg",
                                                "https://moto-station.com/wp-content/uploads/2019/12/05/78269287_10157658937775279_1496340861892427776_o.jpg" });
                                put("Bleu", new String[] {
                                                "https://www.bleublancmotos.com/public/img/big/les-premieres-images-en-fuite-de-la-nouvelle-yamaha-yzf-r7.jpg",
                                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTyPB9QKiTkxbm0BgkRa-JC1nr8c8ERNkM2xA&s",
                                                "https://www.kutvek-kitgraphik.com/images/produits/original/replica-r6-bleu-SL-4926.jpg",
                                                "https://www.moto-net.com/sites/default/files/images/photos-articles/2017/nouveautes/yamaha/yzf-r1-2017-bleue.jpg",
                                                "https://www.bleublancmotos.com/public/img/big/gsx-s1000gt_m2_ysf_right_0.jpg" });
                                put("Vert", new String[] {
                                                "https://www.wkx-racing.com/35382-large_default/motocross-150cc-xr150-vert-wkx.jpg",
                                                "https://cdn.powergo.ca/media/catalog/2023/24/1444bc24b5594848b0004a2a84eab063_1024x768_webp/kawasaki-ninja-zx-10rr-vert-lime-2024-0.webp",
                                                "https://moto-station.com/wp-content/uploads/2010/12/1-57.jpg",
                                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT3taGuWNH5EAS4Y877YAsqIqVO87yB74jsSw&s",
                                                "https://acidmoto.ch/cms/sites/default/files/img/alpha/k/kawasaki-zx10r-2011_23.jpg?itok=WWBDMmo4" });
                                put("Noir", new String[] {
                                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQnX6ERq2T20upB3S3YRBkIiYTLbrnog_8Diw&s",
                                                "https://cdn.powergo.ca/media/catalog/2023/6/f6569b8006c64294b015c4e7c8496939_1024x768_webp/kawasaki-ninja-zx-4r-noir-etincelle-metallise-2023-0.webp",
                                                "https://images-rajhraciek-cdn.rshop.sk/lg/products/3c40dce40e70ab8170c1f5efc7422b63.jpg",
                                                "https://www.sudtrike.fr/wp-content/uploads/2022/05/LXR-NOIR.2.jpg",
                                                "https://www.cdiscount.com/pdt2/9/4/1/1/700x700/auc9065802457941/rw/couleur-zx6r-noir-sans-boite-modele-de-moto-de-cou.jpg" });
                                put("Blanc", new String[] {
                                                "https://cdn.powergo.ca/inventory/catalog/2021/19/4765317f9ada4d86b095c49c12493975_site/suzuki-gsx-r750-blanc-perle-brillant-2021-0.jpg",
                                                "https://www.jazt.com/v2/wp-content/uploads/2014/04/DSC00683-850x478.jpg",
                                                "https://sc04.alicdn.com/kf/HTB12gKQcL1H3KVjSZFBq6zSMXXaL.jpg",
                                                "https://previews.123rf.com/images/darijaurb/darijaurb1611/darijaurb161100039/65916389-homme-à-la-moto-blanche.jpg",
                                                "https://kitdeco-moto.fr/wp-content/uploads/2022/06/kit-deco-moto-yamaha-mt-09-anniversaire-blanc-mockup.jpg" });
                                put("Gris", new String[] {
                                                "https://images.caradisiac.com/images/9/1/5/5/169155/S1-nouveaute-ducati-la-supersport-passe-en-nuance-de-gris-555785.jpg",
                                                "https://i.ebayimg.com/thumbs/images/g/IQIAAOSwViJm6Eld/s-l1200.jpg",
                                                "https://c8.alamy.com/compfr/2h7ncrk/ducati-959-moto-couleur-gris-course-sao-paulo-sao-paulo-bresil-mars-2017-2h7ncrk.jpg",
                                                "https://www.cdiscount.com/pdt2/8/7/5/1/700x700/auc9065802482875/rw/couleur-gris-pas-de-boite-modele-de-moto-bmw-s1000.jpgs",
                                                "https://www.go2roues.com/wp-content/uploads/2023/08/aoa-pro-qj-motor-avant-gauche-gris.png" });
                                put("Jaune", new String[] {
                                                "https://www.marketoy.com/media/catalog/product/cache/8568c7e347f81f58c7fc88722be58d67/n/_/n_e_new57803a.jpg",
                                                "https://m.media-amazon.com/images/I/712H0hOzXHL._AC_UF1000,1000_QL80_.jpg",
                                                "https://www.kutvek-kitgraphik.com/images/produits/original/vintage-r1-jaune-SL-4922.jpg",
                                                "https://www.wkx-racing.com/35262-large_default/motocross-250cc-rs250-jaune-wkx.jpg",
                                                "https://www.kutvek-kitgraphik.com/images/produits/original/vintage-r125-jaune-SL-5041.jpg" });
                                put("Orange", new String[] {
                                                "https://img.pixers.pics/pho_wat(s3:700/FO/66/89/55/91/700_FO66895591_a67ec4dd2affe7d5400853bb107e5750.jpg,700,597,cms:2018/10/5bd1b6b8d04b8_220x50-watermark.png,over,480,547,jpg)/posters-image-3d-d-39-une-moto-moderne-orange.jpg.jpg",
                                                "https://img.lestendances.fr/produits/955x705/moto-cross-enfant-49cc-e-start-10-10-viper-orange-541144.jpg",
                                                "https://www.kutvek-kitgraphik.com/images/produits/original/krav-rc-125-noir-orange-28011.jpg",
                                                "https://m.media-amazon.com/images/I/61BVGn5JdbL._AC_UF1000,1000_QL80_.jpg",
                                                "https://c8.alamy.com/compfr/2d46317/umea-norrland-suede-14-avril-2020-moto-orange-moderne-dans-le-desert-2d46317.jpg" });
                                put("Violet", new String[] {
                                                "https://moto-station.com/wp-content/uploads/2019/03/26/mt10-kr-violet.jpg.jpeg",
                                                "https://cdn-ejpkk.nitrocdn.com/xdyCioSLBXYkmzJtNuIrPyoUMnPGKtBz/assets/images/optimized/rev-96e2850/kitdeco-moto.fr/wp-content/uploads/2023/07/kit-deco-honda-hm-craft-violet.jpg",
                                                "https://previews.123rf.com/images/rawpixel/rawpixel1504/rawpixel150408524/39108807-moto-moto-vélo-rider-concept-violet-contemporain.jpg",
                                                "https://kitdeco-moto.fr/wp-content/uploads/2022/09/kit-deco-yamaha-125-wrx-gotam-violet.jpg",
                                                "https://www.petitemaismotarde.com/wp-content/uploads/2020/04/IMG_20200307_141633-scaled.jpg" });
                                put("Rose", new String[] {
                                                "https://www.loisir-plein-air.com/159950-large_default/moto-dirt-bike-sano-rxf-enduro-125cc-rose-apollo.jpg",
                                                "https://thorn-bikes.com/wp-content/uploads/2024/02/GRAPHICS-KIT_KAWASAKI-ZX10R-2021_BLACK-PINK-BLUE_STANDARD.webp",
                                                "https://www.sandiego-bike.com/Files/119733/Img/20/full-jpeg-46-zoom.jpg",
                                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSyHftbCehCDGDe-Jt4RvhA5BwpIstjEmVAIQ&s",
                                                "https://m.media-amazon.com/images/I/61POOAaj3PL._AC_UF350,350_QL80_.jpg" });
                        }
                };

                for (int i = 0; i < 2000; i++) {
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
                                images = getRandomImages(color, typeVehicle, imagesCars, imagesMotos);
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
                                images = getRandomImages(color, typeVehicle, imagesCars, imagesMotos);
                        }

                        annonceDetails.setTitle(title);
                        annonceDetails.setTransaction(transaction);

                        if (transaction.equals("Vente")) {
                                annonceDetails.setPrice(
                                                Double.valueOf(faker.number().numberBetween(minPrice, maxPrice)));
                                annonceDetails.setQuantity(1);
                        } else {
                                annonceDetails.setPrice(
                                                Double.valueOf(faker.number().numberBetween(minRentalPrice, maxRentalPrice)));
                                annonceDetails.setQuantity(1);
                        }

                        int kmCount = faker.number().numberBetween(minKm, maxKm);
                        vehicleDetails.setKlmCounter(kmCount);
                        annonceDetails.setCity(city);
                        annonceDetails.setPhoneNumber(
                                        faker.numerify("+33 " + (random.nextBoolean() ? "6" : "7") + "## ## ## ##"));
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
                                description = String.format(
                                                "Superbe %s %s %s neuf%s. %s, %s. Disponible immédiatement pour %s.",
                                                brand, model, typeVehicle.toLowerCase(),
                                                typeVehicle.equals("Voiture") ? "ve" : "",
                                                gearbox, climatisation, transaction.toLowerCase());
                        } else {
                                description = String.format(
                                                "%s %s %s de %d avec %d km. %s, %s, %s. Véhicule bien entretenu, disponible pour %s.",
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
                                                        "utilitaire", "utilitaire grand volume",
                                                        "utilitaire frigorifique",
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

        private List<String> getRandomImages(String color, String typeVehicle, HashMap<String, String[]> imagesCars, HashMap<String, String[]> imagesMotos) {
                List<String> images = new ArrayList<>();
                String[] availableImages;
                
                if (typeVehicle.equals("Voiture")) {
                        availableImages = imagesCars.get(color);
                } else {
                        availableImages = imagesMotos.get(color);
                }

                if (availableImages != null) {
                        // Nombre aléatoire d'images entre 2 et 5
                        int numberOfImages = random.nextInt(4) + 2; // random.nextInt(4) donne 0-3, +2 donne 2-5
                        
                        // Créer une liste d'indices disponibles
                        List<Integer> indices = new ArrayList<>();
                        for (int i = 0; i < availableImages.length; i++) {
                                indices.add(i);
                        }
                        
                        // Sélectionner aléatoirement les images
                        for (int i = 0; i < Math.min(numberOfImages, availableImages.length); i++) {
                                int randomIndex = random.nextInt(indices.size());
                                int selectedIndex = indices.remove(randomIndex);
                                images.add(availableImages[selectedIndex]);
                        }
                }
                
                return images;
        }
}