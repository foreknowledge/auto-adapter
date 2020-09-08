package com.ellie.autoadapter_processor

import com.ellie.autoadapter_annotations.AdapterItem
import com.ellie.autoadapter_annotations.ViewBinding
import com.squareup.kotlinpoet.FileSpec
import java.io.File
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

class AutoAdapterProcessor : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes() = mutableSetOf(AdapterItem::class.java.canonicalName)

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val kaptKotlinGeneratedDir =
            processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: return false

        roundEnv.getElementsAnnotatedWith(AdapterItem::class.java)
            .forEach {
                val itemData = getItemData(it)
                val fileName = "${itemData.itemName}RecyclerAdapter"
                FileSpec.builder(itemData.packageName, fileName)
                    .addType(AdapterCodeBuilder(fileName, itemData).build())
                    .build()
                    .writeTo(File(kaptKotlinGeneratedDir))
            }

        return true
    }

    private fun getItemData(e: Element): ItemData {
        val packageName = processingEnv.elementUtils.getPackageOf(e).toString()
        val itemName = e.simpleName.toString()
        val annotation = e.getAnnotation(AdapterItem::class.java)
        val layoutId = annotation.layoutId
        val viewHolderBindingDataList = e.enclosedElements.mapNotNull {
            it.getAnnotation(ViewBinding::class.java)?.let { viewBinding ->
                val elementName = it.simpleName.toString()  // getXXX$annotation 형태
                val fieldName = elementName.substring(3, elementName.indexOf("$")).toLowerCase(Locale.ROOT)
                ViewBindingData(fieldName, viewBinding.viewId)
            }
        }

        return ItemData(packageName, itemName, layoutId, viewHolderBindingDataList)
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}