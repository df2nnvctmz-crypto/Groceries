import org.json.JSONObject

fun main() {
    val json = JSONObject("""{"micros": {"vitamin_a_ug": 0}}""")
    val micros = json.getJSONObject("micros")
    println(micros.getDouble("vitamin_a_ug"))
}
