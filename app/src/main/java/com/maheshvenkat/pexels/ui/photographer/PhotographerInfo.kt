package com.maheshvenkat.pexels.ui.photographer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotographerInfo(val name: String, val websiteInfo: String) : Parcelable