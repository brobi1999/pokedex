package hu.bme.aut.pokedex.ui.login

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.pokedex.model.PokeType
import hu.bme.aut.pokedex.repo.FirebaseRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel@Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _error = MutableLiveData<Exception>(null)
    val error: LiveData<Exception>
        get() = _error

    fun register(name: String, favTypes: ArrayList<Int>, email: String, pass: String){
        Log.d("asd", "im called")
        val convFavTypes = convertFavTypeFromIntToPokeType(favTypes)
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.register(name, email, pass)
                repository.addUserToFirestore(name, email, convFavTypes)
            }
            catch (e: Exception){
                e.printStackTrace()
                _error.value = e
            }
            finally {
                _isLoading.value = false
            }

        }

    }

    fun login(email: String, pass: String){
        viewModelScope.launch {
            try {
                repository.login(email, pass)
            }
            catch (e: Exception){
                e.printStackTrace()
            }
            finally {

            }

        }
    }

    private fun convertFavTypeFromIntToPokeType(favTypes: ArrayList<Int>) : ArrayList<PokeType>{
        val enumFavTypes = arrayListOf<PokeType>()
        for(favType in favTypes){
            enumFavTypes.add(PokeType.values()[favType])
        }
        return enumFavTypes
    }




}
