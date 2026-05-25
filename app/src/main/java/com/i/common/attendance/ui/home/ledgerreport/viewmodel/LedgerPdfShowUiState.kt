package com.i.common.attendance.ui.home.ledgerreport.viewmodel

import com.i.common.attendance.network.response.LedgerPdfDataShow

sealed class LedgerPdfShowUiState {

    object Idle : LedgerPdfShowUiState()

    object Loading : LedgerPdfShowUiState()

    data class Success(
        val list: List<LedgerPdfDataShow>,
        val pdfUrl: String
    ) : LedgerPdfShowUiState()

    data class ApiError(
        val message: String
    ) : LedgerPdfShowUiState()

    data class NetworkError(
        val message: String
    ) : LedgerPdfShowUiState()
}