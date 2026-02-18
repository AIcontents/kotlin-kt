data class User(val id: Int, val name: String, val age: Int)
data class Product(val id: Int, val name: String, val price: Int)
data class Purchase(val userId: Int, val productId: Int, val quantity: Int)

/* -------------------- 3.1 -------------------- */
// filter
fun usersOlderThan(users: List<User>, minAge: Int): List<User> =
    users.filter { it.age > minAge }

/* -------------------- 3.2 -------------------- */
// sortedByDescending + take
fun top3ExpensiveProducts(products: List<Product>): List<Product> =
    products.sortedByDescending { it.price }.take(3)

/* -------------------- 3.3 -------------------- */
// any + работа с Map/группировкой
fun didUserBuyProduct(purchases: List<Purchase>, userId: Int, productId: Int): Boolean {
    // Идея: сгруппировать покупки по userId и проверить any по productId
    val byUser: Map<Int, List<Purchase>> = purchases.groupBy { it.userId }
    return byUser[userId]?.any { it.productId == productId } ?: false
}

/* -------------------- 3.4 -------------------- */
// Сгруппировать покупки по пользователю
fun groupPurchasesByUser(purchases: List<Purchase>): Map<Int, List<Purchase>> =
    purchases.groupBy { it.userId }

/* -------------------- 3.5 -------------------- */
// Сгруппировать покупки по товару
fun groupPurchasesByProduct(purchases: List<Purchase>): Map<Int, List<Purchase>> =
    purchases.groupBy { it.productId }

/* -------------------- 3.6 -------------------- */
// Общая сумма трат каждого пользователя
fun totalSpentPerUser(
    users: List<User>,
    products: List<Product>,
    purchases: List<Purchase>
): Map<Int, Int> {
    // Подсказка:
    // 1) priceById = products.associate { it.id to it.price }
    // 2) сгруппировать покупки по userId
    // 3) для каждой группы посчитать sumOf(priceById[productId] * quantity)

    val priceById: Map<Int, Int> = products.associate { it.id to it.price }

    val purchasesByUser: Map<Int, List<Purchase>> = purchases.groupBy { it.userId }

    return purchasesByUser.mapValues { (_, userPurchases) ->
        userPurchases.sumOf { p ->
            val price = priceById[p.productId] ?: 0
            price * p.quantity
        }
    }.also { spentMap ->
        // Если хочешь, чтобы в Map были даже пользователи без покупок:
        // (не обязательно, но часто удобно)
        users.forEach { u -> spentMap[u.id] ?: Unit }
    }
}

/* -------------------- 3.7 -------------------- */
// Топ-1 покупатель по сумме трат
fun topBuyerBySpent(
    users: List<User>,
    spentByUserId: Map<Int, Int>
): User? {
    // Подсказка: найти max по value, потом user по id
    val topEntry = spentByUserId.maxByOrNull { it.value } ?: return null
    return users.firstOrNull { it.id == topEntry.key }
}

/* -------------------- 4. Generics (Вариант А) -------------------- */
// generic-функция findMax
fun <T, R : Comparable<R>> findMax(items: List<T>, selector: (T) -> R): T? {
    if (items.isEmpty()) return null
    var best = items[0]
    var bestKey = selector(best)

    for (i in 1 until items.size) {
        val candidate = items[i]
        val key = selector(candidate)
        if (key > bestKey) {
            best = candidate
            bestKey = key
        }
    }
    return best
}

/* -------------------- 5. Финальный отчёт -------------------- */
fun main() {
    // Дополни списки до:
    // users >= 5, products >= 6, purchases >= 10

    val users = listOf(
        User(1, "Ivan", 18),
        User(2, "Anna", 25),
        // TODO: добавь ещё минимум 3 пользователя
    )

    val products = listOf(
        Product(1, "Mouse", 1200),
        Product(2, "Keyboard", 3500),
        // TODO: добавь ещё минимум 4 товара
    )

    val purchases = listOf(
        Purchase(userId = 1, productId = 1, quantity = 2),
        Purchase(userId = 2, productId = 2, quantity = 1),
        // TODO: добавь ещё минимум 8 покупок
    )

    val ageThreshold = 21
    val checkUserId = 1
    val checkProductId = 2

    println("=== Все пользователи ===")
    users.forEach { println(it) }
    println("\n=== Пользователи старше $ageThreshold ===")
    usersOlderThan(users, ageThreshold).forEach { println(it) }

    println("\n=== Топ-3 дорогих товара ===")
    top3ExpensiveProducts(products).forEach { println(it) }

    println("\n=== Проверка: покупал ли user=$checkUserId товар=$checkProductId ===")
    println(didUserBuyProduct(purchases, checkUserId, checkProductId))

    val spentByUserId = totalSpentPerUser(users, products, purchases)

    println("\n=== Сумма трат каждого пользователя (userId -> сумма) ===")
    spentByUserId.forEach { (id, sum) -> println("$id -> $sum") }

    val topBuyer = topBuyerBySpent(users, spentByUserId)
    println("\n=== Топ-покупатель ===")
    println(topBuyer ?: "Нет данных")

    // Произвольное использование generic findMax:
    val mostExpensive = findMax(products) { it.price }
    println("\n=== Generic findMax: самый дорогой товар ===")
    println(mostExpensive ?: "Нет товаров")
}
