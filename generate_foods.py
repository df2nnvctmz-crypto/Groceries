import json
import random

categories = ["Produce", "Dairy", "Meat", "Pantry", "Snacks", "Beverages"]
names = [
    "Apple", "Banana", "Orange", "Strawberry", "Tomato", "Potato", "Onion", "Garlic", "Lettuce", "Spinach",
    "Milk", "Cheese", "Yogurt", "Butter", "Cream", "Eggs", "Cottage Cheese", "Ice Cream", "Sour Cream", "Whey",
    "Beef", "Pork", "Chicken", "Turkey", "Lamb", "Salmon", "Tuna", "Cod", "Shrimp", "Bacon",
    "Rice", "Pasta", "Flour", "Sugar", "Salt", "Olive Oil", "Vinegar", "Bread", "Oats", "Cereal",
    "Chips", "Cookies", "Chocolate", "Candy", "Popcorn", "Crackers", "Pretzels", "Nuts", "Almonds", "Peanuts",
    "Water", "Coffee", "Tea", "Juice", "Soda", "Beer", "Wine", "Milkshake", "Smoothie", "Energy Drink"
]

foods = []
for i in range(1, 101):
    cat = categories[(i-1) // 10] if i <= 60 else random.choice(categories)
    name_base = names[(i-1) % len(names)]
    name = f"{name_base} {i}" if i > 60 else name_base
    
    foods.append({
        "id": f"ch{i:04d}",
        "name": name,
        "category": cat,
        "swiss_category": f"Gen_{cat}",
        "health_score": random.randint(10, 100),
        "nutri_grade": random.choice(["A", "B", "C", "D", "E"]),
        "swap_suggestion_id": None,
        "nutrients_per_100": {
            "kcal": random.uniform(10, 600),
            "protein_g": random.uniform(0, 30),
            "carbs_g": random.uniform(0, 80),
            "sugars_g": random.uniform(0, 50),
            "fat_g": random.uniform(0, 50),
            "saturated_fat_g": random.uniform(0, 20),
            "fiber_g": random.uniform(0, 15),
            "salt_g": random.uniform(0, 5),
            "micros": {
                "vitamin_a_ug": random.uniform(0, 1000),
                "vitamin_c_mg": random.uniform(0, 100),
                "calcium_mg": random.uniform(0, 500),
                "iron_mg": random.uniform(0, 20)
            }
        }
    })

with open('app/src/main/res/raw/foods.json', 'w') as f:
    json.dump(foods, f, indent=2)

