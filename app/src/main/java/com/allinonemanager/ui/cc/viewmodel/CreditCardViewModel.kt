package com.allinonemanager.ui.cc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allinonemanager.data.UserSession
import com.allinonemanager.data.remote.ApiClient
import com.allinonemanager.data.remote.CreditCardApi
import com.allinonemanager.ui.cc.screens.dto.AddCreditCardRequest
import com.allinonemanager.ui.cc.screens.dto.AddDueDetailsRequest
import com.allinonemanager.ui.cc.screens.dto.CreditCard
import com.allinonemanager.ui.cc.screens.dto.DueDetail
import com.allinonemanager.ui.cc.screens.dto.UpdateCardStatusRequest
import com.allinonemanager.ui.cc.screens.dto.UpdateCreditCardRequest
import com.allinonemanager.ui.cc.screens.dto.UpdateDueDetailsRequest
import com.allinonemanager.ui.cc.screens.dto.UpdateDueStatusRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

sealed interface CardsUiState {
    data object Loading : CardsUiState
    data class Loaded(val cards: List<CreditCard>) : CardsUiState
    data class Error(val message: String) : CardsUiState
}

sealed interface DuesUiState {
    data object Loading : DuesUiState
    data class Loaded(val dues: List<DueDetail>) : DuesUiState
    data class Error(val message: String) : DuesUiState
}

sealed interface SaveUiState {
    data object Idle : SaveUiState
    data object Saving : SaveUiState
    data class Success(val message: String) : SaveUiState
    data class Error(val message: String) : SaveUiState
}

class CreditCardViewModel(
    private val creditCardApi: CreditCardApi = ApiClient.creditCardApi
) : ViewModel() {

    private val _cardsState = MutableStateFlow<CardsUiState>(CardsUiState.Loading)
    val cardsState: StateFlow<CardsUiState> = _cardsState.asStateFlow()

    private val _duesState = MutableStateFlow<DuesUiState>(DuesUiState.Loading)
    val duesState: StateFlow<DuesUiState> = _duesState.asStateFlow()

    private val _saveState = MutableStateFlow<SaveUiState>(SaveUiState.Idle)
    val saveState: StateFlow<SaveUiState> = _saveState.asStateFlow()

    fun loadCards() {
        val userId = UserSession.userId ?: run {
            _cardsState.value = CardsUiState.Error(NOT_LOGGED_IN)
            return
        }
        // Keep showing the old list while refreshing, only show loading the first time
        if (_cardsState.value !is CardsUiState.Loaded) _cardsState.value = CardsUiState.Loading

        viewModelScope.launch {
            _cardsState.value = try {
                CardsUiState.Loaded(creditCardApi.getCreditCards(userId))
            } catch (_: IOException) {
                CardsUiState.Error("Unable to reach server. Check your connection.")
            } catch (e: Exception) {
                CardsUiState.Error(e.message ?: "Could not load credit cards")
            }
        }
    }

    // ccId = null loads the dues of every card the logged-in user has
    fun loadDues(ccId: Int? = null) {
        val userId = UserSession.userId ?: run {
            _duesState.value = DuesUiState.Error(NOT_LOGGED_IN)
            return
        }
        if (_duesState.value !is DuesUiState.Loaded) _duesState.value = DuesUiState.Loading

        viewModelScope.launch {
            _duesState.value = try {
                DuesUiState.Loaded(
                    if (ccId != null) creditCardApi.getDuesByCard(ccId) else creditCardApi.getDuesByUser(userId)
                )
            } catch (_: IOException) {
                DuesUiState.Error("Unable to reach server. Check your connection.")
            } catch (e: Exception) {
                DuesUiState.Error(e.message ?: "Could not load due details")
            }
        }
    }

    // The screen does not know the user, so userId is filled in here
    fun addCard(request: AddCreditCardRequest) {
        val userId = UserSession.userId ?: run {
            _saveState.value = SaveUiState.Error(NOT_LOGGED_IN)
            return
        }
        save({ it == CARD_ADDED }) { creditCardApi.addCreditCard(request.copy(userId = userId)) }
    }

    // The form gives an AddCreditCardRequest; ccId comes from the route, userId from the session
    fun updateCard(ccId: Int, request: AddCreditCardRequest) {
        val userId = UserSession.userId ?: run {
            _saveState.value = SaveUiState.Error(NOT_LOGGED_IN)
            return
        }
        val update = with(request) {
            UpdateCreditCardRequest(
                ccId = ccId, userId = userId, ccNumber = ccNumber, ccBank = ccBank, ccName = ccName,
                ccType = ccType, expDate = expDate, cvv = cvv, nameOnCard = nameOnCard,
                statementDate = statementDate, dueDate = dueDate, ccLimit = ccLimit
            )
        }
        // Exact update message is not documented, so any "...success..." reply counts
        save({ it.contains("success", ignoreCase = true) }) { creditCardApi.updateCreditCard(update) }
    }

    fun addDue(request: AddDueDetailsRequest) {
        save({ it == DUE_ADDED }) { creditCardApi.addDueDetails(request) }
    }

    fun updateDue(request: UpdateDueDetailsRequest) {
        save({ it == DUE_UPDATED }) { creditCardApi.updateDueDetails(request) }
    }

    fun setDuePaid(ccDueId: Int, isPaid: Boolean) {
        save({ it == DUE_STATUS_UPDATED }) { creditCardApi.updateDueStatus(UpdateDueStatusRequest(ccDueId, isPaid)) }
    }

    fun deleteDue(ccDueId: Int, ccId: Int) {
        save({ it == DUE_DELETED }) { creditCardApi.deleteDueDetails(ccDueId, ccId) }
    }

    // false = deactivate, true = reactivate
    fun setCardActive(ccId: Int, isActive: Boolean) {
        val userId = UserSession.userId ?: run {
            _saveState.value = SaveUiState.Error(NOT_LOGGED_IN)
            return
        }
        save({ it == CARD_STATUS_UPDATED }) {
            creditCardApi.updateCardStatus(UpdateCardStatusRequest(ccId, userId, isActive))
        }
    }

    fun deleteCard(ccId: Int) {
        val userId = UserSession.userId ?: run {
            _saveState.value = SaveUiState.Error(NOT_LOGGED_IN)
            return
        }
        save({ it == CARD_DELETED }) { creditCardApi.deleteCreditCard(ccId, userId) }
    }

    fun resetSaveState() {
        _saveState.value = SaveUiState.Idle
    }

    private fun save(isSuccess: (message: String) -> Boolean, call: suspend () -> Response<ResponseBody>) {
        if (_saveState.value is SaveUiState.Saving) return
        _saveState.value = SaveUiState.Saving

        viewModelScope.launch {
            _saveState.value = try {
                val response = call()
                val message = response.body()?.string()?.trim().orEmpty()
                when {
                    !response.isSuccessful -> SaveUiState.Error("Save failed (${response.code()})")
                    isSuccess(message) -> SaveUiState.Success(message)
                    else -> SaveUiState.Error(message.ifBlank { "Save failed" })
                }
            } catch (_: IOException) {
                SaveUiState.Error("Unable to reach server. Check your connection.")
            } catch (e: Exception) {
                SaveUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    companion object {
        // Public: the Details screen leaves the page when it sees this
        const val CARD_DELETED = "Credit card deleted successfully."
        private const val CARD_STATUS_UPDATED = "Credit card status updated successfully."
        private const val NOT_LOGGED_IN = "Session expired. Please logout and login again."
        private const val CARD_ADDED = "Credit card added successfully."
        private const val DUE_ADDED = "Due details added successfully."
        private const val DUE_UPDATED = "Due details updated successfully."
        private const val DUE_STATUS_UPDATED = "Due status updated successfully."
        private const val DUE_DELETED = "Due details deleted successfully."
    }
}
