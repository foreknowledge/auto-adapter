package com.ellie.autoadapter

import com.ellie.autoadapter_annotations.AdapterModel
import com.ellie.autoadapter_annotations.ViewBinding

@AdapterModel(R.layout.layout_person)
data class Person (
    @ViewBinding(R.id.tv_name) val name: String,
    @ViewBinding(R.id.tv_address) val address: String
)