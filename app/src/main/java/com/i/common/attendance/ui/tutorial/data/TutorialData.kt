package com.i.common.attendance.ui.tutorial.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TutorialData(val image: Int, val title: String, val description: String) : Parcelable
