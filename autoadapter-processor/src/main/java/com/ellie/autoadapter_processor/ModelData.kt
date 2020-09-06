package com.ellie.autoadapter_processor

data class ModelData(
    val packageName: String,
    val modelName: String,
    val layoutId: Int,
    val viewBindingData: List<ViewBindingData>
)