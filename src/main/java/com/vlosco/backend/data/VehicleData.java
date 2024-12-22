package com.vlosco.backend.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleData {
    public static final Map<String, List<String>> carModels = new HashMap<>();
    public static final Map<String, List<String>> motoModels = new HashMap<>();

    static {
        carModels.put("Ford", Arrays.asList("Mustang", "Fiesta", "Focus", "Explorer", "F-150", "Fusion", "Edge",
                "Escape", "Expedition", "Bronco"));
        carModels.put("Chevrolet", Arrays.asList("Camaro", "Silverado", "Malibu", "Impala", "Tahoe", "Suburban",
                "Equinox", "Traverse", "Colorado", "Blazer"));
        carModels.put("Toyota", Arrays.asList("Corolla", "Camry", "Prius", "Highlander", "RAV4", "Avalon", "Yaris",
                "Sienna", "Tacoma", "Tundra"));
        carModels.put("Honda", Arrays.asList("Civic", "Accord", "CR-V", "Pilot", "Fit", "HR-V", "Odyssey", "Ridgeline",
                "Insight", "Passport"));
        carModels.put("BMW", Arrays.asList("Serie 3", "Serie 5", "Serie 7", "Serie 1", "Serie 2", "Serie 4",
                "Serie 6", "Serie 8", "X1", "X2"));
        carModels.put("Mercedes-Benz", Arrays.asList("Classe A", "Classe B", "Classe C", "Classe E", "Classe S", "CLA",
                "CLS", "GLA", "GLB", "GLC"));
        carModels.put("Audi", Arrays.asList("A1", "A3", "A4", "A5", "A6", "A7", "A8", "Q2", "Q3", "Q5"));
        carModels.put("Volkswagen",
                Arrays.asList("Golf", "Passat", "Tiguan", "Jetta", "Polo", "Beetle", "Arteon", "Atlas",
                        "CC", "Eos"));
        carModels.put("Nissan", Arrays.asList("Altima", "Sentra", "Maxima", "Rogue", "Murano", "Pathfinder", "Armada",
                "Versa", "Kicks", "Leaf"));
        carModels.put("Hyundai", Arrays.asList("Elantra", "Sonata", "Tucson", "Santa Fe", "Kona", "Accent", "Azera",
                "Veloster", "Ioniq", "Palisade"));

        motoModels.put("Harley-Davidson",
                Arrays.asList("Sportster", "Softail", "Touring", "Street", "CVO", "Trike",
                        "LiveWire", "Dyna", "V-Rod", "Road King"));
        motoModels.put("Honda", Arrays.asList("CBR1000RR", "Gold Wing", "Africa Twin", "CBR600RR", "Rebel 500",
                "Shadow Phantom", "CRF450R", "CB500F", "CB650R", "CB300R"));
        motoModels.put("Yamaha", Arrays.asList("YZF-R1", "MT-07", "MT-09", "Tracer 900", "XSR900", "FJR1300",
                "Tenere 700", "Bolt", "V Star 250", "WR250R"));
        motoModels.put("Kawasaki",
                Arrays.asList("Ninja H2", "Ninja ZX-10R", "Ninja 400", "Z900", "Z650", "Versys 650",
                        "Vulcan S", "KX450F", "KLX250", "Ninja 250"));
        motoModels.put("Suzuki", Arrays.asList("GSX-R1000", "Hayabusa", "V-Strom 650", "SV650", "DR-Z400SM",
                "Boulevard M109R", "GSX-S750", "Burgman 400", "RM-Z450", "Katana"));
        motoModels.put("Ducati", Arrays.asList("Panigale V4", "Monster", "Multistrada", "Scrambler", "Diavel",
                "Hypermotard", "SuperSport", "Streetfighter", "XDiavel", "DesertX"));
        motoModels.put("BMW",
                Arrays.asList("S1000RR", "R1250GS", "F850GS", "R nineT", "K1600GTL", "G310R", "F900R",
                        "R1250RT", "F750GS", "C400X"));
        motoModels.put("Triumph",
                Arrays.asList("Bonneville", "Street Triple", "Tiger 900", "Rocket 3", "Speed Triple",
                        "Thruxton", "Scrambler 1200", "Daytona Moto2", "Tiger 1200",
                        "Trident 660"));
        motoModels.put("KTM",
                Arrays.asList("Duke 390", "1290 Super Duke R", "790 Adventure", "450 SX-F", "250 EXC-F",
                        "RC 390", "690 Enduro R", "890 Duke R", "500 EXC-F", "Freeride E-XC"));
        motoModels.put("Indian",
                Arrays.asList("Scout", "Chief", "Chieftain", "Roadmaster", "FTR", "Springfield",
                        "Vintage", "Dark Horse", "Challenger", "Super Chief"));
    }
}
