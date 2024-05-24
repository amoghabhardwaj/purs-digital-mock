import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.pursdigital.LocationResponse
import com.example.pursdigital.NetworkService

// ViewModel to manage UI-related data in a lifecycle-conscious way
class MainViewModel : ViewModel() {

    // Backing property for location data, used to store the fetched data
    private val _locationData = mutableStateOf<LocationResponse?>(null)

    // Public immutable state of location data, observed by the UI
    val locationData: State<LocationResponse?> = _locationData

    // Initializer block to fetch location data when ViewModel is created
    init {
        fetchLocationData()
    }

    // Function to fetch location data from the network
    private fun fetchLocationData() {
        // Launch a coroutine in the ViewModel's scope
        viewModelScope.launch {
            try {
                // Fetch data from the network and update the state
                _locationData.value = NetworkService.api.getLocationData()
            } catch (e: Exception) {
                // Print stack trace in case of an error
                e.printStackTrace()
            }
        }
    }
}
