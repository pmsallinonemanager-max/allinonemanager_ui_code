package com.allinonemanager.data.remote

import com.allinonemanager.ui.cc.screens.dto.AddCreditCardRequest
import com.allinonemanager.ui.cc.screens.dto.AddDueDetailsRequest
import com.allinonemanager.ui.cc.screens.dto.CreditCard
import com.allinonemanager.ui.cc.screens.dto.DueDetail
import com.allinonemanager.ui.cc.screens.dto.UpdateCreditCardRequest
import com.allinonemanager.ui.cc.screens.dto.UpdateDueDetailsRequest
import com.allinonemanager.ui.cc.screens.dto.UpdateDueStatusRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CreditCardApi {

    @GET("credit-cards/{userId}")
    suspend fun getCreditCards(@Path("userId") userId: Int): List<CreditCard>

    @GET("credit-cards/due/user/{userId}")
    suspend fun getDuesByUser(@Path("userId") userId: Int): List<DueDetail>

    @GET("credit-cards/due/{ccId}")
    suspend fun getDuesByCard(@Path("ccId") ccId: Int): List<DueDetail>

    // Both add calls return HTTP 200 with a plain-text message, even when they fail
    // ("User not found." / "Credit card not found."), so the message decides the outcome
    @POST("credit-cards/add")
    suspend fun addCreditCard(@Body request: AddCreditCardRequest): Response<ResponseBody>

    @POST("credit-cards/due/add")
    suspend fun addDueDetails(@Body request: AddDueDetailsRequest): Response<ResponseBody>

    // Plain-text message like the add calls
    @PUT("credit-cards/update")
    suspend fun updateCreditCard(@Body request: UpdateCreditCardRequest): Response<ResponseBody>

    @PUT("credit-cards/due/update")
    suspend fun updateDueDetails(@Body request: UpdateDueDetailsRequest): Response<ResponseBody>

    @PUT("credit-cards/due/status")
    suspend fun updateDueStatus(@Body request: UpdateDueStatusRequest): Response<ResponseBody>
}
