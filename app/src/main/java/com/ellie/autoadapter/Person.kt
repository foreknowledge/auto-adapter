package com.ellie.autoadapter

import com.ellie.autoadapter_annotations.AdapterItem
import com.ellie.autoadapter_annotations.ViewBinding

@AdapterItem(R.layout.layout_person)
data class Person (
    @ViewBinding(R.id.tv_name) val name: String,
    @ViewBinding(R.id.tv_address) val address: String
)