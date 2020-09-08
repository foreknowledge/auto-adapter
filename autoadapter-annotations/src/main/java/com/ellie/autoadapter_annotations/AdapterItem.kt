package com.ellie.autoadapter_annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AdapterItem(val layoutId: Int)