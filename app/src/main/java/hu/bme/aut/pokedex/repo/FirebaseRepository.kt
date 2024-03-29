package hu.bme.aut.pokedex.repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import hu.bme.aut.pokedex.model.ui.PokeType
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor() {
    private var db = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun register(userName: String, email: String, pass: String){
        firebaseAuth
            .createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val firebaseUser = result.user
                val profileChangeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(userName)
                    .build()
                firebaseUser?.updateProfile(profileChangeRequest)
            }.await()
    }

    suspend fun login(email: String, pass: String){
        firebaseAuth
            .signInWithEmailAndPassword(email, pass)
            .await()
    }

    suspend fun addUserToFirestore(userName: String, email: String, favTypes: ArrayList<PokeType>){
        val userData = hashMapOf(
            "username" to userName,
            "email" to email,
            "fav_types" to favTypes.map { it.name }
        )

        db.collection("Users").document(firebaseAuth.currentUser?.uid!!).set(userData).await()
    }

    suspend fun getFavouritePokeNamesForCurrentUser(): List<String>{
        val list = mutableListOf<String>()
        val favPokeNamesRef = db.collection("Users").document(firebaseAuth.currentUser?.uid!!).collection("fav_poke_names")
        val querySnapshot = favPokeNamesRef.get().await()
        for(docSnapshot in querySnapshot){
            val name = docSnapshot.id
            list.add(name)
        }
        return list
    }

    suspend fun addPokeNameToFavourites(name: String){
        db.collection("Users").document(firebaseAuth.currentUser?.uid!!).collection("fav_poke_names").document(name).set(
            hashMapOf("exists" to true)).await()
    }

    suspend fun removePokeNameFromFavourites(name: String){
        db.collection("Users").document(firebaseAuth.currentUser?.uid!!).collection("fav_poke_names").document(name).delete().await()
    }


}
