package com.vlosco.backend.utils;

import java.util.*;

import com.vlosco.backend.data.AnnonceData;
import com.vlosco.backend.data.VehicleData;

public class SearchUtils {
    private static final Map<String, List<String>> KEYWORDS_MAPPING = new HashMap<>();
    private static final Map<String, String> NORMALIZED_TERMS = new HashMap<>();
    private static final double SIMILARITY_THRESHOLD = 0.75;

    static {
        // Types de véhicules
        KEYWORDS_MAPPING.put("vehicle.type", Arrays.asList(AnnonceData.TYPE_VEHICULE_ITEMS));

        // Marques courantes - Voitures
        List<String> carBrands = new ArrayList<>(VehicleData.carModels.keySet());
        // Marques courantes - Motos  
        List<String> motoBrands = new ArrayList<>(VehicleData.motoModels.keySet());
        // Combine toutes les marques
        List<String> allBrands = new ArrayList<>();
        allBrands.addAll(carBrands);
        allBrands.addAll(motoBrands);
        KEYWORDS_MAPPING.put("vehicle.mark", allBrands);

        // Modèles de voitures et motos
        List<String> allModels = new ArrayList<>();
        // Ajoute tous les modèles de voitures
        VehicleData.carModels.values().forEach(allModels::addAll);
        // Ajoute tous les modèles de motos
        VehicleData.motoModels.values().forEach(allModels::addAll);
        KEYWORDS_MAPPING.put("vehicle.model", allModels);

        // Catégories voitures
        List<String> carCategories = Arrays.asList(AnnonceData.CATEGORY_ITEMS_CARS);
        // Catégories motos
        List<String> motoCategories = Arrays.asList(AnnonceData.CATEGORY_ITEMS_MOTOS);
        // Combine toutes les catégories
        List<String> allCategories = new ArrayList<>();
        allCategories.addAll(carCategories);
        allCategories.addAll(motoCategories);
        KEYWORDS_MAPPING.put("vehicle.category", allCategories);

        // Carburants
        KEYWORDS_MAPPING.put("vehicle.fuelType", Arrays.asList(AnnonceData.FUEL_TYPE_ITEMS));

        // Couleurs
        KEYWORDS_MAPPING.put("vehicle.color", Arrays.asList(AnnonceData.COLOR_ITEMS));

        // Transactions
        KEYWORDS_MAPPING.put("annonce.transaction", Arrays.asList(AnnonceData.TRANSACTION_ITEMS));

        // Années (de 1950 à aujourd'hui)
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = 1950; year <= currentYear; year++) {
            years.add(String.valueOf(year));
        }
        KEYWORDS_MAPPING.put("vehicle.year", years);

        // Normalisation des termes
        initializeNormalizedTerms();
    }

    private static void initializeNormalizedTerms() {
        // Voitures
        Arrays.asList("car", "voitur", "auto", "automobile", "vehicule", "véhicule", "voiturette",
                     "caisse", "bagnole", "char", "berline", "carosse", "bolide", "tacot", "4 roues",
                     "quatre roues", "4roues", "quatreroues", "4x4", "suv", "monospace", "break",
                     "citadine", "familiale", "coupé", "coupe", "cabriolet", "cabrio", "decapotable",
                     "décapotable", "deca", "déca", "sportive", "sport")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Voiture"));
        
        // Motos
        Arrays.asList("moto", "motard", "motorcycle", "motocyclette", "biker", "motocycliste", "bike",
                     "becane", "bécane", "deux-roues", "deuxroues", "mob", "mobylette", "cyclo",
                     "2 roues", "2roues", "deux roues", "scooter", "scoot", "trail", "roadster",
                     "custom", "sportive", "enduro", "cross", "supermotard", "super motard", "naked",
                     "cruiser", "chopper", "racing", "race")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Moto"));
        
        // Carburants
        Arrays.asList("electric", "electrique", "elec", "électrique", "électric", "100% electrique",
                     "100% électrique", "full electric", "full électrique", "zero emission",
                     "zéro émission", "batterie", "ev", "bev", "tout electrique", "tout électrique")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Électrique"));
        Arrays.asList("hybrid", "hybride", "hyb", "hybr", "semi electrique", "semi électrique",
                     "mi electrique", "mi électrique", "phev", "plug in", "plug-in", "rechargeable")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Hybride")); 
        Arrays.asList("gas", "petrol", "essence", "sp95", "sp98", "sans plomb", "sansplomb",
                     "super", "95", "98", "e10", "e5", "ethanol", "éthanol", "sp", "super plus")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Essence"));
        Arrays.asList("diesel", "gazole", "gasoil", "go", "tdi", "hdi", "dci", "d", "bluehdi",
                     "blue hdi", "blue-hdi", "jtd", "dtec", "d-4d")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Diesel"));
        
        // Couleurs
        Arrays.asList("red", "rouge", "rge", "carmin", "bordeaux", "pourpre", "vermillon",
                     "rouge foncé", "rouge clair", "rouge vif", "rouge métallisé", "rouge metal",
                     "rouge mat", "rouge brillant", "rouge sombre", "grenat", "rouge ferrari")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Rouge"));
        Arrays.asList("blue", "bleu", "azur", "marine", "cyan", "indigo", "turquoise",
                     "bleu foncé", "bleu clair", "bleu nuit", "bleu roi", "bleu ciel",
                     "bleu électrique", "bleu métallisé", "bleu metal", "bleu mat")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Bleu")); 
        Arrays.asList("green", "vert", "emeraude", "émeraude", "olive", "sapin",
                     "vert foncé", "vert clair", "vert bouteille", "vert pomme",
                     "vert métallisé", "vert metal", "vert mat", "vert militaire")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Vert"));
        Arrays.asList("black", "noir", "ebene", "ébène", "charbon", "jais",
                     "noir brillant", "noir mat", "noir métallisé", "noir metal",
                     "noir profond", "noir intense", "black edition", "full black")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Noir"));
        Arrays.asList("white", "blanc", "ivoire", "neige", "creme", "crème",
                     "blanc nacré", "blanc pur", "blanc métallisé", "blanc metal",
                     "blanc perlé", "blanc brillant", "blanc mat", "pearl white")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Blanc"));
        Arrays.asList("grey", "gray", "gris", "anthracite", "acier", "souris",
                     "gris foncé", "gris clair", "gris métallisé", "gris metal",
                     "gris mat", "gris perle", "gris titanium", "gun metal")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Gris"));
        Arrays.asList("yellow", "jaune", "citron", "poussin", "paille", "or", "doré",
                     "jaune vif", "jaune pâle", "jaune métallisé", "jaune metal",
                     "jaune mat", "jaune soleil", "jaune moutarde")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Jaune"));
        Arrays.asList("brown", "marron", "brun", "chocolat", "cafe", "café", "caramel",
                     "chatain", "châtain", "marron glacé", "marron foncé", "terre de sienne",
                     "havane", "tabac", "cognac", "cuivre")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Marron"));
        Arrays.asList("purple", "violet", "mauve", "lilas", "prune", "aubergine",
                     "violet foncé", "violet clair", "violet métallisé", "violet metal",
                     "violet mat", "améthyste", "byzantin")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Violet"));
        Arrays.asList("pink", "rose", "fuchsia", "saumon", "corail", "magenta",
                     "rose pâle", "rose vif", "rose métallisé", "rose metal",
                     "rose bonbon", "rose poudré", "rose pastel")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Rose"));
        Arrays.asList("beige", "creme", "crème", "sable", "champagne", "ecru", "écru",
                     "beige clair", "beige foncé", "beige rosé", "beige métallisé",
                     "beige metal", "cappuccino", "vanille")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Beige"));
        Arrays.asList("silver", "argent", "argenté", "gris métallisé", "chrome",
                     "aluminium", "metal", "métallisé", "metallise", "alu",
                     "argent mat", "argent brillant", "quicksilver")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Argent"));

        // Dernier modèle
        Arrays.asList("dernier modele", "dernier modèle", "nouveau modele", "nouveau modèle",
                     "recent", "récent", "nouvelle version", "latest", "new model",
                     "nouvelle modele", "nouvelle modèle", "nouveau", "nouvelle",
                     "derniere version", "dernière version", "toute derniere",
                     "tout dernier", "plus recent", "plus récent", "actuel",
                     "cette année", "annee en cours", "année en cours")
            .forEach(term -> NORMALIZED_TERMS.put(term, String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));

        // Normalisation des transactions
        Arrays.asList("vente", "vendre", "buy", "sale", "selling")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Vente"));
        Arrays.asList("location", "louer", "rent", "rental")
            .forEach(term -> NORMALIZED_TERMS.put(term, "Location"));
    }

    public static Map<String, String> analyzeSearchText(String searchText) {
        Map<String, String> searchCriteria = new HashMap<>();
        if (searchText == null || searchText.trim().isEmpty()) {
            return searchCriteria;
        }

        // Rechercher d'abord les expressions de plusieurs mots
        String text = searchText.toLowerCase().trim();
        for (String key : NORMALIZED_TERMS.keySet()) {
            if (key.contains(" ") && text.contains(key)) {
                String normalizedTerm = NORMALIZED_TERMS.get(key);
                for (Map.Entry<String, List<String>> entry : KEYWORDS_MAPPING.entrySet()) {
                    String category = entry.getKey();
                    List<String> keywords = entry.getValue();
                    if (keywords.contains(normalizedTerm) && !searchCriteria.containsKey(category)) {
                        searchCriteria.put(category, normalizedTerm);
                        // Retirer l'expression trouvée du texte
                        text = text.replace(key, "");
                        break;
                    }
                }
            }
        }

        // Traiter les mots individuels restants
        String[] words = text.split("\\s+");
        for (String word : words) {
            // Ignorer les mots trop courts
            if (word.length() < 3) continue;

            // Vérifier si c'est une année
            if (word.matches("\\d{4}") && Integer.parseInt(word) >= 1950 && 
                Integer.parseInt(word) <= Calendar.getInstance().get(Calendar.YEAR)) {
                searchCriteria.put("vehicle.year", word);
                continue;
            }

            // D'abord vérifier dans les termes normalisés
            String normalizedTerm = findNormalizedTerm(word);
            if (normalizedTerm != null) {
                // Trouver la catégorie correspondante
                for (Map.Entry<String, List<String>> entry : KEYWORDS_MAPPING.entrySet()) {
                    String category = entry.getKey();
                    List<String> keywords = entry.getValue();
                    
                    if (keywords.contains(normalizedTerm) && !searchCriteria.containsKey(category)) {
                        searchCriteria.put(category, normalizedTerm);
                        break;
                    }
                }
                continue;
            }

            // Si pas trouvé dans les termes normalisés, chercher dans les mots-clés
            for (Map.Entry<String, List<String>> entry : KEYWORDS_MAPPING.entrySet()) {
                String category = entry.getKey();
                List<String> keywords = entry.getValue();

                if (!searchCriteria.containsKey(category)) {
                    for (String keyword : keywords) {
                        if (word.contains(keyword.toLowerCase()) || 
                            keyword.toLowerCase().contains(word) || 
                            calculateSimilarity(word, keyword.toLowerCase()) >= SIMILARITY_THRESHOLD) {
                            searchCriteria.put(category, keyword);
                            break;
                        }
                    }
                }
            }
        }
       
        // Garder le texte original pour la recherche générale
        searchCriteria.put("searchText", searchText);
        
        return searchCriteria;
    }

    private static String findNormalizedTerm(String word) {
        // Recherche directe
        if (NORMALIZED_TERMS.containsKey(word)) {
            return NORMALIZED_TERMS.get(word);
        }

        // Recherche avec similarité
        for (Map.Entry<String, String> entry : NORMALIZED_TERMS.entrySet()) {
            if (calculateSimilarity(word, entry.getKey()) >= SIMILARITY_THRESHOLD) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static double calculateSimilarity(String s1, String s2) {
        // Algorithme de distance de Levenshtein normalisé
        int distance = levenshteinDistance(s1, s2);
        int maxLength = Math.max(s1.length(), s2.length());
        return 1.0 - ((double) distance / maxLength);
    }

    private static int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                        dp[i - 1][j - 1],
                        Math.min(
                            dp[i - 1][j],
                            dp[i][j - 1]
                        )
                    );
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }
}