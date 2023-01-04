package com.Oleg.lec17.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.Oleg.lec17.MoviesApplication
import com.Oleg.lec17.models.MoviesWithGenres
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    //get the old movies from dao.getMovies:
    //show progress bar
    val movies: LiveData<List<MoviesWithGenres>> = MoviesApplication.repository.getMovies()
    private val _error: MutableLiveData<String> = MutableLiveData()
    private val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val  loading: LiveData<Boolean> = _loading

    //expose the error live data as READ ONLY
    val error: LiveData<String> = _error


    //call the refresh function
    init {
        viewModelScope.launch {
            if (MoviesApplication.networkStatusChecker.hasInternet()) {
                _loading.value = true
                MoviesApplication.repository.refreshMovies()
                _loading.value = false
            } else {
                //show a toast/dialog...
                _error.value = "No Internet Connection"
            }
        }
    }
}