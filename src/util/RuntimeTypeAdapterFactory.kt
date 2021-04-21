package de.fhac.ewi.util

import com.google.gson.*
import com.google.gson.internal.Streams
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * Adapts values whose runtime type may differ from their declaration type. This
 * is necessary when a field's type is not the same type that GSON should create
 * when deserializing that field. For example, consider these types:
 * <pre>   {@code
 *   abstract class Shape {
 *     int x;
 *     int y;
 *   }
 *   class Circle extends Shape {
 *     int radius;
 *   }
 *   class Rectangle extends Shape {
 *     int width;
 *     int height;
 *   }
 *   class Diamond extends Shape {
 *     int width;
 *     int height;
 *   }
 *   class Drawing {
 *     Shape bottomShape;
 *     Shape topShape;
 *   }
 * }</pre>
 * <p>Without additional type information, the serialized JSON is ambiguous. Is
 * the bottom shape in this drawing a rectangle or a diamond? <pre>   {@code
 *   {
 *     "bottomShape": {
 *       "width": 10,
 *       "height": 5,
 *       "x": 0,
 *       "y": 0
 *     },
 *     "topShape": {
 *       "radius": 2,
 *       "x": 4,
 *       "y": 1
 *     }
 *   }}</pre>
 * This class addresses this problem by adding type information to the
 * serialized JSON and honoring that type information when the JSON is
 * deserialized: <pre>   {@code
 *   {
 *     "bottomShape": {
 *       "type": "Diamond",
 *       "width": 10,
 *       "height": 5,
 *       "x": 0,
 *       "y": 0
 *     },
 *     "topShape": {
 *       "type": "Circle",
 *       "radius": 2,
 *       "x": 4,
 *       "y": 1
 *     }
 *   }}</pre>
 * Both the type field name ({@code "type"}) and the type labels ({@code
 * "Rectangle"}) are configurable.
 *
 * <h3>Registering Types</h3>
 * Create a {@code RuntimeTypeAdapterFactory} by passing the base type and type field
 * name to the {@link #of} factory method. If you don't supply an explicit type
 * field name, {@code "type"} will be used. <pre>   {@code
 *   RuntimeTypeAdapterFactory<Shape> shapeAdapterFactory
 *       = RuntimeTypeAdapterFactory.of(Shape.class, "type");
 * }</pre>
 * Next register all of your subtypes. Every subtype must be explicitly
 * registered. This protects your application from injection attacks. If you
 * don't supply an explicit type label, the type's simple name will be used.
 * <pre>   {@code
 *   shapeAdapterFactory.registerSubtype(Rectangle.class, "Rectangle");
 *   shapeAdapterFactory.registerSubtype(Circle.class, "Circle");
 *   shapeAdapterFactory.registerSubtype(Diamond.class, "Diamond");
 * }</pre>
 * Finally, register the type adapter factory in your application's GSON builder:
 * <pre>   {@code
 *   Gson gson = new GsonBuilder()
 *       .registerTypeAdapterFactory(shapeAdapterFactory)
 *       .create();
 * }</pre>
 * Like {@code GsonBuilder}, this API supports chaining: <pre>   {@code
 *   RuntimeTypeAdapterFactory<Shape> shapeAdapterFactory = RuntimeTypeAdapterFactory.of(Shape.class)
 *       .registerSubtype(Rectangle.class)
 *       .registerSubtype(Circle.class)
 *       .registerSubtype(Diamond.class);
 * }</pre>
 */
class RuntimeTypeAdapterFactory<T: Any> private constructor(
    private val baseType: KClass<T>,
    private val typeFieldName: String,
    private val maintainType: Boolean
) : TypeAdapterFactory {
    private val labelToSubtype = mutableMapOf<String, KClass<*>>()
    private val subtypeToLabel = mutableMapOf<KClass<*>, String>()

    /**
     * Registers `type` identified by `label`. Labels are case
     * sensitive.
     *
     * @throws IllegalArgumentException if either `type` or `label`
     * have already been registered on this type adapter.
     */
    fun registerSubtype(type: KClass<out T>, label: String = type.simpleName!!): RuntimeTypeAdapterFactory<T> {
        if (subtypeToLabel.containsKey(type) || labelToSubtype.containsKey(label)) {
            throw IllegalArgumentException("types and labels must be unique")
        }
        labelToSubtype[label] = type
        subtypeToLabel[type] = label
        return this
    }

    fun registerSubtypes(vararg types: Pair<KClass<out T>, String>): RuntimeTypeAdapterFactory<T> {
        for ((klass, label) in types) {
            registerSubtype(klass, label)
        }
        return this
    }

    override fun <R: Any> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
        // match on the base type or an abstract subtype.
        val matchingType = subtypeToLabel.keys.find { type.rawType === it.java }
        val isOpen = { t: Class<*> -> t.isInterface || !Modifier.isFinal(t.modifiers) || Modifier.isAbstract(t.modifiers) }
        val isAbstractSubtype = matchingType != null && isOpen(matchingType.java)
        if (type.rawType != baseType.java && !isAbstractSubtype) {
            return null
        }

        val labelToDelegate = mutableMapOf<String, TypeAdapter<*>>()
        val subtypeToDelegate = mutableMapOf<KClass<*>, TypeAdapter<*>>()
        for ((key, value) in labelToSubtype) {
            val delegate = gson.getDelegateAdapter(this, TypeToken.get(value.java))
            labelToDelegate[key] = delegate
            subtypeToDelegate[value] = delegate
        }

        return RuntimeTypeAdapter<R>(
            labelToDelegate,
            subtypeToDelegate
        ).nullSafe()
    }

    inner class RuntimeTypeAdapter<R: Any>(
        val labelToDelegate: Map<String, TypeAdapter<*>>,
        val subtypeToDelegate: Map<KClass<*>, TypeAdapter<*>>
    ) : TypeAdapter<R>() {
        @Throws(IOException::class)
        override fun read(input: JsonReader): R {
            val jsonElement = Streams.parse(input)
            return if (jsonElement.isJsonPrimitive) {
                val jsonString = jsonElement.asString
                // singleton
                val subType = labelToSubtype[jsonString]
                val singleton = try {
                    subType?.objectInstance
                    //                subType?.java?.getDeclaredField("INSTANCE")?.get(null)
                } catch (e: Exception) {
                    null
                } ?: error("String value invalid for non-singleton")

                singleton as R
            } else {
                val jsonObject = jsonElement.asJsonObject
                val labelJsonElement = jsonObject[typeFieldName]
                    ?: throw JsonParseException(
                        "cannot deserialize $baseType without a field named $typeFieldName"
                    )

                val label = labelJsonElement.asString
                val delegate = labelToDelegate[label] as? TypeAdapter<R>
                // registration requires that subtype extends T
                    ?: throw JsonParseException(
                        "cannot deserialize $baseType subtype named $label; did you forget to register a subtype?"
                    )

                val subType = labelToSubtype[label]
                val singleton = try {
                    subType?.objectInstance
//                subType?.java?.getDeclaredField("INSTANCE")?.get(null)
                } catch (e: Exception) {
                    null
                }

                if (singleton != null) {
                    singleton as R
                } else {
                    val valueElement = if (maintainType) {
                        jsonObject
                    } else jsonObject["value"]

                    delegate.fromJsonTree(valueElement)
                }
            }
        }

        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: R) {
            val srcType = value::class
            val label = subtypeToLabel[srcType]
            val delegate = subtypeToDelegate[srcType] as? TypeAdapter<R>
            // registration requires that subtype extends T
                ?: throw JsonParseException(
                    "cannot serialize ${srcType.qualifiedName}; did you forget to register a subtype?"
                )
            val child = delegate.toJsonTree(value)

            if (maintainType) {
                Streams.write(child, out)
                return
            }

            val parent = JsonObject().apply {
                addProperty(typeFieldName, label)
                add("value", child)
            }

            Streams.write(parent, out)
        }
    }

    companion object {

        /**
         * Creates a new runtime type adapter using for `baseType` using `typeFieldName` as the type field name. Type field names are case sensitive.
         * `maintainType` flag decide if the type will be stored in pojo or not.
         */
        fun <T: Any> of(baseType: KClass<T>, typeFieldName: String, maintainType: Boolean): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, typeFieldName, maintainType)
        }

        /**
         * Creates a new runtime type adapter using for `baseType` using `typeFieldName` as the type field name. Type field names are case sensitive.
         */
        fun <T: Any> of(baseType: KClass<T>, typeFieldName: String): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, typeFieldName, false)
        }

        /**
         * Creates a new runtime type adapter for `baseType` using `"type"` as
         * the type field name.
         */
        fun <T: Any> of(baseType: KClass<T>): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, "type", false)
        }

        inline fun <reified T: Any> of() = of(T::class)
    }
}
/**
 * Registers `type` identified by its [simple][Class.getSimpleName]. Labels are case sensitive.
 *
 * @throws IllegalArgumentException if either `type` or its simple name
 * have already been registered on this type adapter.
 */