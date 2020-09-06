package com.ellie.autoadapter_annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class ViewBinding (val viewId: Int)