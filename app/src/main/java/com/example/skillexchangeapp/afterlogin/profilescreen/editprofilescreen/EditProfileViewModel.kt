import android.graphics.Bitmap
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import repository.ProfileImageRepository

class EditProfileViewModel : ViewModel() {

    private val _firstName = MutableLiveData<String>()
    val firstName: LiveData<String> get() = _firstName

    private val _lastName = MutableLiveData<String>()
    val lastName: LiveData<String> get() = _lastName

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> get() = _description

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> get() = _location

    private val _userProfileImage = MutableLiveData<String?>()
    val userProfileImage: LiveData<String?> get() = _userProfileImage

    private val _decodedProfileImage = MutableLiveData<Bitmap?>()
    val decodedProfileImage: LiveData<Bitmap?> get() = _decodedProfileImage

    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val profileImageRepository = ProfileImageRepository(firestore)

    fun loadUserProfile() {
        if (userId == null) return

        viewModelScope.launch {
            try {
                val doc = firestore.collection("users").document(userId).get().await()
                if (!doc.exists()) return@launch

                _firstName.value = doc.getString("firstName").orEmpty()
                _lastName.value = doc.getString("lastName").orEmpty()
                _description.value = doc.getString("description").orEmpty()
                _userProfileImage.value = doc.getString("profilePhoto")
                _location.value = doc.getString("location").orEmpty()
            } catch (e: Exception) {
                // Handle error (log it or post to an error LiveData)
            }
        }
    }

    fun saveUserProfile(firstName: String, lastName: String, description: String, location: String?) {
        if (userId == null) return

        val updates = mutableMapOf<String, Any>(
            "firstName" to firstName,
            "lastName" to lastName,
            "description" to description
        )
        if (!location.isNullOrBlank()) {
            updates["location"] = location
        }

        viewModelScope.launch {
            try {
                firestore.collection("users").document(userId).update(updates).await()
                _firstName.value = firstName
                _lastName.value = lastName
                _description.value = description
                location?.let { _location.value = it }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun uploadProfilePhoto(imageString: String?) {
        if (userId == null || imageString.isNullOrEmpty()) return

        viewModelScope.launch {
            val success = profileImageRepository.uploadProfileImage(imageString)
            if (success) {
                // Ενημερώνουμε άμεσα το LiveData για να δει το UI την αλλαγή
                _userProfileImage.postValue(imageString)
                // Προαιρετικό: φορτώνουμε το προφίλ πάλι, αν θέλουμε να πάρουμε και άλλες ενημερώσεις
                loadUserProfile()
            } else {
                // Handle failure (πχ log, error message κτλ)
            }
        }
    }
}
