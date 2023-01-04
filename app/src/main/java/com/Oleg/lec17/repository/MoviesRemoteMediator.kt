package com.Oleg.lec17.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.Oleg.lec17.database.AppDatabase
import com.Oleg.lec17.models.Movie
import com.Oleg.lec17.models.RemoteKeys
import com.Oleg.lec17.services.TMDBService
import okio.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class MoviesRemoteMediator(
    private val moviesApiService:TMDBService,
    private val movieDatabase:AppDatabase
    ) :RemoteMediator<Int,Movie>(){
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Movie>): MediatorResult {
       val page:Int = when(loadType){
           LoadType.APPEND->{
               val remoteKeys = getRemoteKeysForLastItem(state)
               val nextKey = remoteKeys?.nextKey
               //if it's the last page(get out of method and tell the library)
               nextKey?:return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
           }
           LoadType.PREPEND->{
               val remoteKeys = getRemoteKeysForFirstItem(state)
               val prevKey = remoteKeys?.prevKey
               //if it's the last page(get out of method and tell the library)
               prevKey?:return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
           }
           LoadType.REFRESH->{
               val remoteKeys = getRemoteKeysClosestToCurrentItem(state)
               remoteKeys?.nextKey?.minus(1)?:1
           }
       }
        try {
            //go to api and fetch the movies:
            val apiResponse = moviesApiService.popularMovies(page)
            val movies = apiResponse.movies

            val endOfPagination = movies.isEmpty()

            //save to db: all or nothing:
            //3 operations against the db:
            //if one operation throws an exeption:
            //all 3 operations are cenceled (Role back)
            movieDatabase.withTransaction {
                if(loadType == LoadType.REFRESH){
                    movieDatabase.remoteKeysDao().clearRemoteKeys()
                    movieDatabase.movieDao().clearMovies()
                }
            }

        }catch (error:IOException){
            return MediatorResult.Error(error)
        }catch (error:HttpException){
            return MediatorResult.Error(error)
        }
    }

    //helper methods for prepend, append, refresh

    //append:
    private suspend fun getRemoteKeysForLastItem(state: PagingState<Int, Movie>): RemoteKeys? {
        val lastPage = state.pages.lastOrNull(){
            it.data.isNotEmpty()
        }

        val lastKey = lastPage?.data?.lastOrNull()?.let {
            movie -> movieDatabase.remoteKeysDao().getRemoteKeysByMovieId(movie.movieId)
        }
        return lastKey
    }
    //preappend:
    private suspend fun getRemoteKeysForFirstItem(state: PagingState<Int, Movie>): RemoteKeys? {
        val firstPage = state.pages.firstOrNull(){
            it.data.isNotEmpty()
        }

        val firstKey = firstPage?.data?.firstOrNull()?.let {
                movie -> movieDatabase.remoteKeysDao().getRemoteKeysByMovieId(movie.movieId)
        }
        return firstKey
    }
    ///---------------------------------
    private suspend fun getRemoteKeysForFirstItem2(state: PagingState<Int, Movie>): RemoteKeys? {
        return state.pages.firstOrNull() {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { movie ->
            movieDatabase.remoteKeysDao().getRemoteKeysByMovieId(movie.movieId)
        }
    }
//----------------------------------------
    //Refresh:
    private suspend fun getRemoteKeysClosestToCurrentItem(state: PagingState<Int, Movie>): RemoteKeys? {
        //get the page from the library:
        val position = state.anchorPosition ?: return null//Int of the page

        val closestMovieToPosition = state.closestItemToPosition(position) ?: return null

        return movieDatabase.remoteKeysDao().getRemoteKeysByMovieId(closestMovieToPosition.movieId)
    }
}