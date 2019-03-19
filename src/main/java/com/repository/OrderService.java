package com.repository;

import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderService {
    public static final Map<String, List<String>> orderItems = new HashMap() {{
        put("Household and Pets", Arrays.asList(
                "HydroSilk",
                "Tide",
                "MilkBone",//pet food
                "Hefty",//disposable plastic cups
                "Cascade",//dishwasher detergent
                "Ziploc",//sandwich bags
                "AirHeads"));
        put("Food", Arrays.asList(
                "Milano",//cookie
                "Puffs",//round cookie
                "GoldFish",//crackers
                "Skippy"//peanut butter
        ));
        put("Beverages", Arrays.asList(
                "RedBull",
                "CocaCola",
                "Folgers",//colombian coffee
                "JackDaniels"
        ));
        put("Beauty and Grooming", Arrays.asList(
                "Probiotic",//acidophilus tables welness
                "SlimFast snack bites",//advanced nutrition snack welness
                "Now apple cider",//vinegar
                "FlintStones",//children supplement
                "SlimFast smoothie"
        ));

        put("Wellness and Healthcare", Arrays.asList(
                "Qtips",//cotton swaps grooming
                "Dove",//soap
                "Pantene",
                "Dove fresh",//deodorant
                "Venus",//razors
                "Glide",//floss
                "Crest"//toothpaste
        ));
    }};

    public static final String[] orderCategories = {"Food", "Beverages", "Household and Pets", "Beauty and Grooming", "Wellness and Healthcare"};
}
