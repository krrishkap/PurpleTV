package tv.purple.monolith.core.util

import android.os.Build
import tv.purple.monolith.core.LoggerWithTag
import java.lang.reflect.Field
import java.lang.reflect.Modifier

object DebugUtils {
    data class DebugConfig(
        val maxDepth: Int = 3,
        val maxCollectionSize: Int = 100,
        val showPrivateFields: Boolean = true,
        val respectCustomToString: Boolean = true
    )

    private val logger = LoggerWithTag("::DebugObject::")
    private val fieldCache = mutableMapOf<Class<*>, List<Field>>()

    @JvmStatic
    @JvmOverloads
    fun debugObject(obj: Any?, config: DebugConfig = DebugConfig()) {
        debugObjectInternal(obj, 3, mutableSetOf(), config)
    }

    @JvmStatic
    fun debugObjectSimple(obj: Any?) {
        debugObject(obj, DebugConfig(maxDepth = 1, maxCollectionSize = 10))
    }

    @JvmStatic
    fun debugObjectDeep(obj: Any?) {
        debugObject(obj, DebugConfig(maxDepth = 5, maxCollectionSize = 50))
    }

    private fun debugObjectInternal(
        obj: Any?,
        depth: Int,
        visited: MutableSet<Int>,
        config: DebugConfig
    ) {
        if (obj == null) {
            logger.debug("${getIndent(depth)}null")
            return
        }

        val objectHash = System.identityHashCode(obj)
        if (visited.contains(objectHash)) {
            logger.debug("${getIndent(depth)}[Circular reference: ${obj.javaClass.simpleName}@${objectHash.toString(16)}]")
            return
        }

        if (depth > config.maxDepth) {
            logger.debug("${getIndent(depth)}[Max depth reached: ${obj.javaClass.simpleName}]")
            return
        }

        visited.add(objectHash)

        try {
            val clazz = obj.javaClass
            logger.debug("${getIndent(depth)}=== ${clazz.simpleName} ===")

            if (isPrimitiveOrWrapper(clazz) || obj is String) {
                logger.debug("${getIndent(depth)}Value: $obj")
                return
            }

            if (config.respectCustomToString && hasCustomToString(clazz)) {
                logger.debug("${getIndent(depth)}Custom toString(): $obj")
                return
            }

            if (obj is Enum<*>) {
                logger.debug("${getIndent(depth)}Enum value: ${obj.name} (ordinal: ${obj.ordinal})")
                return
            }

            if (obj is Collection<*>) {
                handleCollection(obj, depth, visited, config)
                return
            }

            if (clazz.isArray) {
                handleArray(obj, depth, visited, config)
                return
            }

            if (obj is Map<*, *>) {
                handleMap(obj, depth, visited, config)
                return
            }

            if (isParcelable(obj)) {
                logger.debug("${getIndent(depth)}[Parcelable object]")
            }

            handleRegularObject(obj, clazz, depth, visited, config)

        } catch (e: Exception) {
            logger.debug("${getIndent(depth)}Error debugging object: ${e.message}")
        } finally {
            visited.remove(objectHash)
        }
    }

    private fun handleCollection(
        obj: Collection<*>,
        depth: Int,
        visited: MutableSet<Int>,
        config: DebugConfig
    ) {
        logger.debug("${getIndent(depth)}Collection (${obj.javaClass.simpleName}) size: ${obj.size}")
        obj.take(config.maxCollectionSize).forEachIndexed { index, item ->
            logger.debug("${getIndent(depth)}[$index]:")
            debugObjectInternal(item, depth + 1, visited, config)
        }
        if (obj.size > config.maxCollectionSize) {
            logger.debug("${getIndent(depth)}... и еще ${obj.size - config.maxCollectionSize} элементов")
        }
    }

    private fun handleArray(
        obj: Any,
        depth: Int,
        visited: MutableSet<Int>,
        config: DebugConfig
    ) {
        val arrayLength = java.lang.reflect.Array.getLength(obj)
        logger.debug("${getIndent(depth)}Array (${obj.javaClass.componentType.simpleName}[]) length: $arrayLength")
        for (i in 0 until minOf(arrayLength, config.maxCollectionSize)) {
            logger.debug("${getIndent(depth)}[$i]:")
            val item = java.lang.reflect.Array.get(obj, i)
            debugObjectInternal(item, depth + 1, visited, config)
        }
        if (arrayLength > config.maxCollectionSize) {
            logger.debug("${getIndent(depth)}... и еще ${arrayLength - config.maxCollectionSize} элементов")
        }
    }

    private fun handleMap(
        obj: Map<*, *>,
        depth: Int,
        visited: MutableSet<Int>,
        config: DebugConfig
    ) {
        logger.debug("${getIndent(depth)}Map (${obj.javaClass.simpleName}) size: ${obj.size}")
        obj.entries.take(config.maxCollectionSize).forEach { (key, value) ->
            logger.debug("${getIndent(depth)}Key:")
            debugObjectInternal(key, depth + 1, visited, config)
            logger.debug("${getIndent(depth)}Value:")
            debugObjectInternal(value, depth + 1, visited, config)
        }
        if (obj.size > config.maxCollectionSize) {
            logger.debug("${getIndent(depth)}... и еще ${obj.size - config.maxCollectionSize} пар")
        }
    }

    private fun handleRegularObject(
        obj: Any,
        clazz: Class<*>,
        depth: Int,
        visited: MutableSet<Int>,
        config: DebugConfig
    ) {
        val fields = getAllFields(clazz, config)

        if (fields.isEmpty()) {
            logger.debug("${getIndent(depth)}Нет доступных полей")
            return
        }

        fields.forEach { field ->
            handleField(obj, field, depth, visited, config)
        }
    }

    private fun handleField(
        obj: Any,
        field: Field,
        depth: Int,
        visited: MutableSet<Int>,
        config: DebugConfig
    ) {
        try {
            val wasAccessible = field.isAccessible
            field.isAccessible = true

            val fieldValue = field.get(obj)
            val modifiersStr = getModifiersString(field)

            logger.debug("${getIndent(depth)}$modifiersStr ${field.type.simpleName} ${field.name}:")

            if (fieldValue != null && !isPrimitiveOrWrapper(fieldValue.javaClass) && fieldValue !is String) {
                debugObjectInternal(fieldValue, depth + 1, visited, config)
            } else {
                logger.debug("${getIndent(depth + 1)}$fieldValue")
            }

            field.isAccessible = wasAccessible

        } catch (e: IllegalAccessException) {
            logger.debug("${getIndent(depth)}Не удалось получить доступ к полю ${field.name}: ${e.message}")
        } catch (e: SecurityException) {
            logger.debug("${getIndent(depth)}Нет разрешения на доступ к полю ${field.name}: ${e.message}")
        } catch (e: Exception) {
            logger.debug("${getIndent(depth)}Ошибка при обработке поля ${field.name}: ${e.message}")
        }
    }

    private fun getAllFields(clazz: Class<*>, config: DebugConfig): List<Field> {
        return fieldCache.getOrPut(clazz) {
            val fields = mutableListOf<Field>()
            var currentClass: Class<*>? = clazz

            while (currentClass != null && currentClass != Any::class.java) {
                try {
                    fields.addAll(currentClass.declaredFields)
                    currentClass = currentClass.superclass
                } catch (e: SecurityException) {
                    logger.debug("Нет доступа к полям класса ${currentClass.simpleName}: ${e.message}")
                    break
                }
            }

            fields.filter { field ->
                val shouldInclude = (!field.isSynthetic || config.showPrivateFields) &&
                        field.name != "Companion" &&
                        !field.name.contains("$")

                // Дополнительная фильтрация для приватных полей
                if (!config.showPrivateFields && Modifier.isPrivate(field.modifiers)) {
                    return@filter false
                }

                shouldInclude
            }
        }
    }

    private fun isPrimitiveOrWrapper(clazz: Class<*>): Boolean {
        return clazz.isPrimitive ||
                clazz == java.lang.Boolean::class.javaObjectType ||
                clazz == java.lang.Byte::class.javaObjectType ||
                clazz == java.lang.Character::class.javaObjectType ||
                clazz == java.lang.Short::class.javaObjectType ||
                clazz == java.lang.Integer::class.javaObjectType ||
                clazz == java.lang.Long::class.javaObjectType ||
                clazz == java.lang.Float::class.javaObjectType ||
                clazz == java.lang.Double::class.javaObjectType ||
                clazz == Boolean::class.javaPrimitiveType ||
                clazz == Byte::class.javaPrimitiveType ||
                clazz == Char::class.javaPrimitiveType ||
                clazz == Short::class.javaPrimitiveType ||
                clazz == Int::class.javaPrimitiveType ||
                clazz == Long::class.javaPrimitiveType ||
                clazz == Float::class.javaPrimitiveType ||
                clazz == Double::class.javaPrimitiveType
    }

    private fun hasCustomToString(clazz: Class<*>): Boolean {
        return try {
            val toStringMethod = clazz.getMethod("toString")
            toStringMethod.declaringClass != Any::class.java
        } catch (e: Exception) {
            false
        }
    }

    private fun isParcelable(obj: Any): Boolean {
        return try {
            Class.forName("android.os.Parcelable").isInstance(obj)
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    private fun getModifiersString(field: Field): String {
        val modifiers = mutableListOf<String>()

        if (Modifier.isPublic(field.modifiers)) modifiers.add("public")
        if (Modifier.isPrivate(field.modifiers)) modifiers.add("private")
        if (Modifier.isProtected(field.modifiers)) modifiers.add("protected")
        if (Modifier.isStatic(field.modifiers)) modifiers.add("static")
        if (Modifier.isFinal(field.modifiers)) modifiers.add("final")
        if (Modifier.isTransient(field.modifiers)) modifiers.add("transient")
        if (Modifier.isVolatile(field.modifiers)) modifiers.add("volatile")

        return if (modifiers.isNotEmpty()) "[${modifiers.joinToString(" ")}]" else ""
    }

    private fun getIndent(depth: Int): String {
        return "  ".repeat(depth)
    }
}