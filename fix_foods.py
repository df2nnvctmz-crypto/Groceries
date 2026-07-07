import json
import random

names_list = [
    "Agar Agar", "Agave syrup", "Almond", "Almond drink, plain", "Almond drink, plain, with calcium and vitamin fortified", 
    "Almond, dry roasted, salted", "Almond, roasted, salted", "Amaranth, seed, cooked", "Amaranth, seed, raw", "Amaretti", 
    "Anchovis paste", "Appenzeller", "Apple, fresh", "Apple juice", "Apple, peeled, dried", "Apple pie, home-made, baked", 
    "Applesauce", "Apricot", "Artichoke", "Asparagus", "Avocado", "Bacon", "Baguette", "Balsamic vinegar", "Bami Goreng", 
    "Banana", "Barbecue sauce", "Barley flakes", "Barley risotto", "Basil", "Basler Leckerli", "Bean, green", "Béchamel sauce", 
    "Beef", "Beer", "Beetroot", "Bell pepper", "Berliner pastry", "Bernese alp cheese", "Berries", "Biber", "Bierwurst", 
    "Birchermüesli", "Birnenweggen", "Blackberry", "Black forest cake", "Black pudding", "Black salsify", "Blanc battu", 
    "Blueberry", "Blue cheese", "Boiling meat salad", "Bouillon", "Braided bread", "Brazil nut", "Bread", "Breadcrumbs", 
    "Bresaola", "Brie", "Broccoli", "Brown bread", "Brunsli", "Brussel sprouts", "Buckwheat", "Burger", "Butter", 
    "Butterhead lettuce", "Buttermilk", "Cabbage", "Cake", "Calimocho", "Camembert", "Candied lemon peel", "Candy", 
    "Cannelloni", "Capers", "Cappuccino", "Capuns", "Carnival pastry", "Carrot", "Cashew nut", "Cauliflower", "Celeriac", 
    "Celery", "Cereal", "Cervelat", "Chanterelle", "Cheese", "Cheeseburger", "Cherry", "Chestnut", "Chia seed", "Chicken", 
    "Chickpea", "Chicory", "Chili con carne", "Chinese cabbage", "Chives", "Chocolate", "Chop", "Chräbeli", "Cider", 
    "Cinnamon", "Citrus", "Cocktail sauce", "Cocoa", "Coco macaroon", "Coconut", "Cod", "Coffee", "Cola", "Cold cuts", 
    "Coleslaw", "Common bean", "Condensed milk", "Cooked cured meat", "Cooked sausages", "Cooking butter", "Coppa", 
    "Cordon bleu", "Corn", "Cotechino", "Cottage cheese", "Cotto", "Couscous", "Cow milk", "Cracker", "Cranberry", "Cream", 
    "Crispbread", "Croissant", "Croque Monsieur", "Cucumber", "Curd", "Currant", "Date", "Deer", "Donut", "Dough", 
    "Dried meat", "Drinking water", "Dulce de Leche", "Durum wheat", "Edamame", "Edible boletus", "Egg", "Eggplant", 
    "Egg white", "Egg yolk", "Elderberry", "Emmentaler", "Endive", "Energy Drink", "Escalope", "Espresso", "Eurasian perch", 
    "Ewe's milk", "Extruded partially defatted soya", "Falafel", "Farmers' bread", "Fennel", "Fermented dough", "Feta", "Fig", 
    "Fish", "Flat white", "Flaxseed oil", "Florentine", "Flounder", "Flour", "Flour omelette", "Fondue", "French fries", 
    "French omelette", "French pizza", "French toast", "Fresh goat's cheese", "Fruit", "Game meat", "Garlic", "Gazpacho", 
    "Ginger", "Glucose", "Goat", "Gooseberry", "Gorgonzola", "Goulash soup", "Grapefruit", "Grape", "Grape seed oil", 
    "Graubünden barley soup", "Gravy", "Green bean", "Green pea", "Greyerzer", "Grittibänz", "Ground meat sauce", "Guacamole", 
    "Halibut", "Hamburger", "Ham", "Hard and semi hard cheese", "Hard caramels", "Hare", "Hazelnut", "Hempseed oil", 
    "Hollandaise sauce", "Honey", "Horse", "Hot Dog", "Hummus", "Huntsman sauce", "Iceberg lettuce", "Ice cream", "Ice Tea", 
    "Jam", "Jelly babies", "Jerusalem artichoke", "Kakaobutter", "Kaki", "Kale", "Kebab", "Ketchup", "Kiwi fruit", "Kohlrabi", 
    "Ladyfinger cookie", "Lamb", "Lamb's lettuce", "Landjäger sausage", "Latte macchiato", "Leafy salad", "Leafy vegetables", 
    "Lebkuchen", "Leek", "Legumes", "Lemon", "Lentil", "Limburger", "Linseed", "Linzertorte", "Litchi", "Liver", "Luganighe", 
    "Luncheon meat", "Lye roll", "Lyoner sausage", "Macaroni", "Madeleine biscuit", "Maize starch", "Malt drink", "Mandarin", 
    "Mango", "Manioc", "Maple syrup", "Marble cake", "Margarine", "Marzipan", "Mashed potatoes", "Mayonnaise", "Meat", "Melon", 
    "Meringue", "Milanese cookie", "Milk", "Millet", "Minced meat", "Minestrone", "Minipic sausage", "Mirabelle plum", 
    "Molasses", "Molluscs", "Morel", "Mortadella", "Mostbröckli", "Mozzarella", "Mueslimix", "Mushroom", "Mussel", "Mustard", 
    "Nasi Goreng", "Nectarine", "Norway lobster", "Nugget", "Nut roll", "Nut sticks", "Nut tart", "Oat bran", "Oat drink", 
    "Oat flakes", "Olive", "Olive oil", "Onion", "Orange", "Palm oil", "Panettone", "Pantli", "Papaya", "Paprika", "Parmesan cheese", 
    "Parsley", "Parsnip", "Passion fruit", "Pasta", "Peach", "Pea", "Peanut", "Pear", "Pearl barley", "Pearl onion", "Peasant's Schüblig", 
    "Peiti Beurre", "Peppermint", "Pesto sauce", "Petit Beurre", "Pike", "Pineapple", "Pine nuts", "Pistachio", "Pizza", "Plum", 
    "Polenta", "Pomegranate", "Pommes Chips", "Popcorn", "Porc piccata", "Pork", "Porridge", "Port", "Potato", "Poultry", 
    "Pretzel sticks", "Processed cheese", "Prune", "Prussian cookie", "Pudding", "Puffed or extruded cereals", "Puff pastry", 
    "Pumpkin", "Quiche Lorraine", "Quince", "Quinoa", "Raclette cheese", "Radicchio", "Radish", "Raisins", "Rapeseed oil", 
    "Rascal", "Raspberry", "Ratatouille", "Raw cured meat", "Raw sausages", "Reblochon cheese", "Red cabbage", "Red currant", 
    "Rhubarb", "Rice", "Risotto", "Ristretto", "Riz Casimir", "Rocket", "Roe", "Rolled smoked ham", "Rollmops", "Rollschinken", 
    "Romaine lettuce", "Root and tuber vegetables", "Roquefort cheese", "Rosemary", "Rosti", "Rusk", "Russian salad", "Rye bread", 
    "Rye flour", "Safflower oil", "Sage", "Salad dressing", "Salametti", "Salami", "Salmon", "Salsiz sausage", "Salt", "Samosa", 
    "Sandwich", "Sardine", "Saucisson sausage", "Sauerkraut", "Sausage", "Savoy cabbage", "Sbrinz", "Schabziger", "Schnitzel bread", 
    "Schützenwurst sausage", "Scrambled eggs", "Seaweed", "Seeds", "Seitan", "Semi-white bread", "Semmeli", "Semolina pudding", 
    "Semolina slice", "Sesame oil", "Sesame seeds", "Shallot", "Shellfish", "Sherry", "Short bread cookie", "Short pastry base", 
    "Shrimp", "Skimmed milk powder", "Soft caramel candies", "Soft cheese", "Soft drink", "Somosa", "Sorbet", "Sour cream", 
    "Soya drink", "Soya flour", "Soybean", "Soy sauce", "Sparkling wine", "Spätzle flour", "Spätzli", "Spelt", "Spinach", 
    "Spirits", "Spreadable cream cheese", "Spring roll", "Squash slices", "Squid", "St. Gall Schüblig sausage", "St. Paulin cheese", 
    "Strawberry", "Strudel dough", "Sugar", "Sugar loaf chicory", "Sugar pea", "Sunflower oil", "Sunflower seeds", "Surimi",
    "cola beverage, sweetened", "cola beverage, unsweetened"
]

def get_category(name):
    n = name.lower()
    if any(x in n for x in ["apple", "banana", "orange", "berry", "grape", "fruit", "melon", "peach", "pear", "plum"]): return "Produce"
    if any(x in n for x in ["lettuce", "cabbage", "spinach", "bean", "pea", "carrot", "onion", "garlic", "potato"]): return "Produce"
    if any(x in n for x in ["beef", "pork", "chicken", "meat", "sausage", "lamb", "veal", "ham", "bacon", "salami"]): return "Meat"
    if any(x in n for x in ["milk", "cheese", "yogurt", "butter", "cream", "egg", "curd", "brie"]): return "Dairy"
    if any(x in n for x in ["salmon", "fish", "tuna", "cod", "shrimp", "squid", "mussel", "halibut"]): return "Meat"
    if any(x in n for x in ["drink", "water", "coffee", "tea", "beer", "wine", "juice", "cola"]): return "Beverages"
    if any(x in n for x in ["cake", "cookie", "chocolate", "candy", "pie", "pastry", "sugar"]): return "Snacks"
    return "Pantry"

foods = []
for i, name in enumerate(names_list):
    cat = get_category(name)
    is_healthy = cat in ["Produce"]
    
    if name == "cola beverage, sweetened":
        health_score = 15
        grade = "E"
        kcal = 42
        carbs = 10.6
        sugars = 10.6
        protein = 0
        fat = 0
    elif name == "cola beverage, unsweetened":
        health_score = 65
        grade = "C"
        kcal = 1
        carbs = 0
        sugars = 0
        protein = 0
        fat = 0
    elif name == "Agar Agar":
        health_score = 80
        grade = "B"
        kcal = 26
        carbs = 7
        sugars = 0
        protein = 0
        fat = 0
    elif name == "Agave syrup":
        health_score = 25
        grade = "D"
        kcal = 310
        carbs = 76
        sugars = 68
        protein = 0
        fat = 0
    elif name == "Almond":
        health_score = 75
        grade = "B"
        kcal = 579
        carbs = 21
        sugars = 4
        protein = 21
        fat = 49
    else:
        health_score = random.randint(80, 100) if is_healthy else random.randint(20, 80)
        grade = "A" if health_score > 85 else "B" if health_score > 70 else "C" if health_score > 50 else "D" if health_score > 30 else "E"
        kcal = random.uniform(20, 100) if is_healthy else random.uniform(100, 500)
        protein = random.uniform(0, 5) if cat == "Produce" else random.uniform(10, 30)
        carbs = random.uniform(5, 20) if cat == "Produce" else random.uniform(10, 60)
        sugars = random.uniform(0, 15) if cat == "Produce" else random.uniform(0, 40)
        fat = random.uniform(0, 1) if cat == "Produce" else random.uniform(5, 40)
        
    foods.append({
        "id": f"ch{i+1:04d}",
        "name": name,
        "category": cat,
        "swiss_category": f"Gen_{cat}",
        "health_score": health_score,
        "nutri_grade": grade,
        "swap_suggestion_id": None,
        "nutrients_per_100": {
            "kcal": kcal,
            "protein_g": protein,
            "carbs_g": carbs,
            "sugars_g": sugars,
            "fat_g": fat,
            "saturated_fat_g": fat * 0.2 if fat > 0 else 0,
            "fiber_g": random.uniform(2, 8) if cat == "Produce" else random.uniform(0, 5),
            "salt_g": random.uniform(0, 0.1) if cat == "Produce" else random.uniform(0.5, 3),
            "micros": {
                "vitamin_a_ug": random.uniform(0, 500),
                "vitamin_c_mg": random.uniform(0, 50),
                "calcium_mg": random.uniform(10, 200),
                "iron_mg": random.uniform(0.5, 5)
            }
        }
    })

with open('app/src/main/res/raw/foods.json', 'w') as f:
    json.dump(foods, f, indent=2)

