package com.ellie.autoadapter_processor

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class AdapterCodeBuilder (
    private val adapterName: String,
    private val data: ItemData
) {
    private val viewHolderName = "ViewHolder"
    private val viewHolderClassName = ClassName(data.packageName, viewHolderName)
    private val viewHolderQualifiedClassName = ClassName(data.packageName, "$adapterName.$viewHolderName")
    private val modelClassName = ClassName(data.packageName, data.itemName)
    private val itemListClassName = ClassName("kotlin.collections", "List").parameterizedBy(modelClassName)
    private val textViewClassName = ClassName("android.widget", "TextView")

    fun build(): TypeSpec = TypeSpec.classBuilder(adapterName)
        .primaryConstructor(
            FunSpec.constructorBuilder()
            .addParameter("items", itemListClassName)
            .build()
        )
        .superclass(ClassName("androidx.recyclerview.widget.RecyclerView", "Adapter")
            .parameterizedBy(viewHolderQualifiedClassName)
        )
        .addProperty(PropertySpec.builder("items", itemListClassName)
            .addModifiers(KModifier.PRIVATE)
            .initializer("items")
            .build()
        )
        .addBaseMethods()
        .addViewHolderType()
        .build()

    private fun TypeSpec.Builder.addBaseMethods(): TypeSpec.Builder = apply {
        addFunction(FunSpec.builder("getItemCount")
            .addModifiers(KModifier.OVERRIDE)
            .returns(INT)
            .addStatement("return items.size")
            .build())

        addFunction(FunSpec.builder("onCreateViewHolder")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("parent", ClassName("android.view", "ViewGroup"))
            .addParameter("viewType", INT)
            .returns(viewHolderQualifiedClassName)
            .addStatement("val view = " +
                    "android.view.LayoutInflater.from(parent.context).inflate(%L, " +
                    "parent, false)", data.layoutId)
            .addStatement("return $viewHolderName(view)")
            .build()
        )

        addFunction(FunSpec.builder("onBindViewHolder")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("viewHolder", viewHolderQualifiedClassName)
            .addParameter("position", INT)
            .addStatement("viewHolder.bind(items[position])")
            .build()
        )
    }

    private fun TypeSpec.Builder.addViewHolderType(): TypeSpec.Builder = addType(
        TypeSpec.classBuilder(viewHolderClassName)
            .primaryConstructor(FunSpec.constructorBuilder()
                .addParameter("itemView", ClassName("android.view", "View"))
                .build()
            )
            .superclass(ClassName(
                "androidx.recyclerview.widget.RecyclerView",
                "ViewHolder")
            )
            .addSuperclassConstructorParameter("itemView")
            .addBindingMethod()
            .build()
    )

    private fun TypeSpec.Builder.addBindingMethod(): TypeSpec.Builder = addFunction(
        FunSpec.builder("bind")
            .addParameter("item", modelClassName)
            .apply {
                data.viewBindingDataList.forEach {
                    addProperty(PropertySpec.builder("${it.fieldName}View", textViewClassName, KModifier.PRIVATE)
                        .initializer("itemView.findViewById(%L)", it.viewId)
                        .build())

                    addStatement("%LView.text = item.%L", it.fieldName, it.fieldName)
                }
            }
            .build()
    )
}