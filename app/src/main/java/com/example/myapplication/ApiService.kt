package com.example.myapplication

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import okhttp3.ResponseBody

data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

data class StudentResult(
    val id: String,
    val subject: String,
    val score: Int,
    val grade: String,
    val semester: String
)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val userId: String)

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<Post>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("student/results")
    suspend fun getResults(): List<StudentResult>

    @GET("student/transcript")
    suspend fun downloadTranscript(): ResponseBody

    companion object {
        private var apiService: ApiService? = null

        fun getInstance(): ApiService {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl("https://jsonplaceholder.typicode.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
            }
            return apiService!!
        }
    }
}
