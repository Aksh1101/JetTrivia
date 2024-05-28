package com.example.jettrivia.repository

import android.util.Log
import com.example.jettrivia.data.DataOrException
import com.example.jettrivia.model.QuestionItem
import com.example.jettrivia.network.QuestionApi
import retrofit2.http.Tag
import java.util.ArrayList
import javax.inject.Inject


class QuestionRepository @Inject constructor(private val api: QuestionApi) {
    private val dataOrException = DataOrException<java.util.ArrayList<QuestionItem> ,Boolean ,java.lang.Exception >()

    suspend fun getAllQuestions(): DataOrException<ArrayList<QuestionItem>, Boolean, java.lang.Exception> {
        try {
            dataOrException.loading = true
            dataOrException.data = api.getAllQuestion()
            if(dataOrException.data.toString().isNotEmpty()) dataOrException.loading = false

        }catch (exception : Exception){
            dataOrException.e = exception
            Log.d("Exc","getAllQuestions : ${dataOrException.e!!.localizedMessage}")

        }
        return dataOrException
    }

}