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

    private val _isRegLoading = MutableLiveData(false)
    val isRegLoading: LiveData<Boolean>
        get() = _isRegLoading

    private val _regError = MutableLiveData<Exception?>(null)
    val regError: LiveData<Exception?>
        get() = _regError

    private val _isLoginLoading = MutableLiveData(false)
    val isLoginLoading: LiveData<Boolean>
        get() = _isLoginLoading

    private val _loginError = MutableLiveData<Exception?>(null)
    val loginError: LiveData<Exception?>
        get() = _loginError

    private val _success = MutableLiveData(false)
    val success: LiveData<Boolean>
        get() = _success

    fun register(name: String, favTypes: ArrayList<Int>, email: String, pass: String){
        Log.d("asd", "im called")
        val convFavTypes = convertFavTypeFromIntToPokeType(favTypes)
        viewModelScope.launch {
            try {
                _isRegLoading.value = true
                repository.register(name, email, pass)
                repository.addUserToFirestore(name, email, convFavTypes)
                _success.value = true
            }
            catch (e: Exception){
                e.printStackTrace()
                _regError.value = e
            }
            finally {
                _isRegLoading.value = false
            }

        }

    }

    fun login(email: String, pass: String){
        viewModelScope.launch {
            try {
                _isLoginLoading.value = true
                repository.login(email, pass)
                _success.value = true
            }
            catch (e: Exception){
                _loginError.value = e
                e.printStackTrace()
            }
            finally {
                _isLoginLoading.value = false
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

    fun successReceived() {
        _success.postValue(false)
    }

    fun errorReceived(){
        _loginError.postValue(null)
        _regError.postValue(null)
    }

}
