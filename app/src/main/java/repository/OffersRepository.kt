package repository

import android.util.Log
import com.example.skillexchangeapp.model.Offer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class OffersRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val offersCollection = firestore.collection("offers")

    // Προσθήκη προσφοράς στο Firestore (await)
    suspend fun addOffer(offer: Offer): Boolean {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return false
        val documentRef = offersCollection.document()
        val offerWithUserIdAndId = offer.copy(
            userId = userId,
            offerId = documentRef.id
        )

        return try {
            documentRef.set(offerWithUserIdAndId).await()
            true
        } catch (e: Exception) {
            Log.e("OffersRepository", "Error adding offer", e)
            false
        }
    }

    // Λήψη όλων των προσφορών από το Firestore (await)
    suspend fun getAllOffers(): List<Offer> {
        return try {
            val snapshot = offersCollection.get().await()
            snapshot.mapNotNull { it.toObject(Offer::class.java) }
        } catch (e: Exception) {
            Log.e("OffersRepository", "Error getting all offers", e)
            emptyList()
        }
    }

    // Λήψη προσφορών συγκεκριμένου χρήστη (await)
    suspend fun getOffersByUser(userId: String): List<Offer> {
        return try {
            val snapshot = offersCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.mapNotNull { it.toObject(Offer::class.java) }
        } catch (e: Exception) {
            Log.e("OffersRepository", "Error getting offers by user", e)
            emptyList()
        }
    }

    // Διαγραφή προσφοράς βάσει offerId (await)
    suspend fun deleteOffer(offerId: String): Boolean {
        return try {
            offersCollection.document(offerId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("OffersRepository", "Error deleting offer", e)
            false
        }
    }

    // 🔍 Λήψη προσφορών με βάση λίστα κατηγοριών
    suspend fun getOffersByCategories(categories: List<String>): List<Offer> {
        return try {
            if (categories.isEmpty()) {
                val all = getAllOffers()
                Log.d("OffersRepository", "No category filter, returning all offers: ${all.size}")
                return all
            }

            val allResults = mutableListOf<Offer>()
            val chunks = categories.chunked(10)
            Log.d("OffersRepository", "Chunks to process: $chunks")

            for (chunk in chunks) {
                val snapshot = offersCollection
                    .whereIn("category", chunk)
                    .get()
                    .await()

                val offers = snapshot.mapNotNull { it.toObject(Offer::class.java) }
                Log.d("OffersRepository", "Offers found for chunk $chunk: ${offers.size}")
                allResults.addAll(offers)
            }

            allResults
        } catch (e: Exception) {
            Log.e("OffersRepository", "Error fetching offers by categories", e)
            emptyList()
        }
    }

    // 🔹 Νέο: Λήψη μοναδικών userIds που έχουν offers σε συγκεκριμένες κατηγορίες
    suspend fun getUserIdsByCategories(categories: List<String>): Set<String> {
        return try {
            val offers = getOffersByCategories(categories)
            val userIds = offers.mapNotNull { it.userId }.toSet()
            Log.d("OffersRepository", "Unique userIds for categories $categories: $userIds")
            userIds
        } catch (e: Exception) {
            Log.e("OffersRepository", "Error fetching userIds by categories", e)
            emptySet()
        }
    }
}
