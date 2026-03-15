package com.example.clock.alarm

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ringtone(val title: String, val uri: Uri) : Parcelable

