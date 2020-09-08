package com.ellie.autoadapter_processor

data class ItemData(
    val packageName: String,
    val itemName: String,
    val layoutId: Int,
    val viewBindingDataList: List<ViewBindingData>
)