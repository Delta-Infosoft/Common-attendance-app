package com.i.common.attendance.ui.home.ledgerreport.viewmodel

import com.i.common.attendance.network.response.LedgerPdfData

sealed class LedgerPdfUiState {

    object Idle : LedgerPdfUiState()

    object Loading : LedgerPdfUiState()

    data class Success(
        val data: List<LedgerPdfData>
    ) : LedgerPdfUiState()

    data class Empty(
        val message: String
    ) : LedgerPdfUiState()

    data class ApiError(
        val message: String
    ) : LedgerPdfUiState()

    data class NetworkError(
        val message: String
    ) : LedgerPdfUiState()
}
