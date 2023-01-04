package com.Oleg.lec17.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.Oleg.lec17.models.Genre
import com.Oleg.lec17.models.Movie
import com.Oleg.lec17.models.MovieGenreCrossRef
import com.Oleg.lec17.models.MoviesWithGenres

@Dao
interface MovieDao {

    //Movie
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovies(movies: List<Movie>)

    @Transaction
    @Query("SELECT * FROM Movie")
    fun getMoviesWithGenres():LiveData<List<MoviesWithGenres>>

    //Genres:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(genre: Genre)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGenres(genres: List<Genre>)


    //MovieGenreCrossRef:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(movieGenreCrossRef: MovieGenreCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovieGenreCrossRefs(movieGenreCrossRef: List<MovieGenreCrossRef>)

    @Query("SELECT * FROM Movie ORDER BY page")
    fun getMoviesPage():PagingSource<Int,Movie>

    @Query("DELETE FROM Movie")
    suspend fun clearAllMovies()
}