package org.jetbrains.kotlinx.dataframe.io

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.tryParse
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.parse
import org.jetbrains.kotlinx.dataframe.values
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringReader
import java.math.BigDecimal
import java.net.URL
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import java.util.zip.GZIPInputStream
import kotlin.collections.ArrayList
import kotlin.reflect.KClass

public enum class CSVType(public val format: CSVFormat) {
    DEFAULT(CSVFormat.DEFAULT.withAllowMissingColumnNames().withIgnoreSurroundingSpaces()),
    TDF(CSVFormat.TDF.withAllowMissingColumnNames())
}

private val defaultCharset = Charsets.UTF_8

private val defaultLocale = Locale.getDefault()

private val setOfNullStrings = setOf("NA", "N/A", "null")

internal fun isCompressed(fileOrUrl: String) = listOf("gz", "zip").contains(fileOrUrl.split(".").last())

internal fun isCompressed(file: File) = listOf("gz", "zip").contains(file.extension)

internal fun isCompressed(url: URL) = isCompressed(url.path)

public fun DataFrame.Companion.readDelimStr(
    text: String,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null
): DataFrame<*> = readDelim(StringReader(text), CSVType.DEFAULT.format.withHeader(), setOfNullStrings, colTypes, skipLines, readLines)

public fun DataFrame.Companion.read(
    fileOrUrl: String,
    delimiter: Char,
    headers: List<String> = listOf(),
    nullStrings: Set<String> = setOfNullStrings,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8
): DataFrame<*> =
    catchHttpResponse(asURL(fileOrUrl)) {
        readDelim(
            it, delimiter,
            headers, nullStrings, isCompressed(fileOrUrl),
            getCSVType(fileOrUrl), colTypes,
            skipLines, readLines,
            duplicate, charset
        )
    }

public fun DataFrame.Companion.readCSV(
    fileOrUrl: String,
    delimiter: Char = ',',
    headers: List<String> = listOf(),
    nullStrings: Set<String> = setOfNullStrings,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
    parserOptions: ParserOptions? = null,
): DataFrame<*> =
    catchHttpResponse(asURL(fileOrUrl)) {
        readDelim(
            it, delimiter,
            headers, nullStrings, isCompressed(fileOrUrl),
            CSVType.DEFAULT, colTypes,
            skipLines, readLines,
            duplicate, charset,
            parserOptions
        )
    }

public fun DataFrame.Companion.readCSV(
    file: File,
    delimiter: Char = ',',
    headers: List<String> = listOf(),
    nullStrings: Set<String> = setOfNullStrings,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
    parserOptions: ParserOptions? = null
): DataFrame<*> =
    readDelim(
        FileInputStream(file), delimiter,
        headers, nullStrings, isCompressed(file),
        CSVType.DEFAULT, colTypes,
        skipLines, readLines,
        duplicate, charset,
        parserOptions
    )

public fun DataFrame.Companion.readCSV(
    url: URL,
    delimiter: Char = ',',
    headers: List<String> = listOf(),
    nullStrings: Set<String> = setOfNullStrings,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
    parserOptions: ParserOptions? = null
): DataFrame<*> =
    readDelim(
        url.openStream(), delimiter,
        headers, nullStrings, isCompressed(url),
        CSVType.DEFAULT, colTypes,
        skipLines, readLines,
        duplicate, charset,
        parserOptions
    )

public fun DataFrame.Companion.readTSV(
    fileOrUrl: String,
    delimiter: Char = '\t',
    headers: List<String> = listOf(),
    nullStrings: Set<String> = setOfNullStrings,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
    parserOptions: ParserOptions? = null
): DataFrame<*> =
    catchHttpResponse(asURL(fileOrUrl)) {
        readDelim(
            it, delimiter,
            headers, nullStrings, isCompressed(fileOrUrl),
            CSVType.TDF, colTypes,
            skipLines, readLines,
            duplicate, charset,
            parserOptions
        )
    }

public fun DataFrame.Companion.readTSV(
    file: File,
    delimiter: Char = '\t',
    headers: List<String> = listOf(),
    nullStrings: Set<String> = setOfNullStrings,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8
): DataFrame<*> =
    readDelim(
        FileInputStream(file), delimiter,
        headers, nullStrings, isCompressed(file),
        CSVType.TDF, colTypes,
        skipLines, readLines,
        duplicate, charset
    )

private fun getCSVType(path: String): CSVType =
    when (path.substringAfterLast('.').toLowerCase()) {
        "csv" -> CSVType.DEFAULT
        "tdf" -> CSVType.TDF
        else -> throw IOException("Unknown file format")
    }

private fun asStream(fileOrUrl: String) = (
    if (isURL(fileOrUrl)) {
        URL(fileOrUrl).toURI()
    } else {
        File(fileOrUrl).toURI()
    }
    ).toURL().openStream()

private fun asURL(fileOrUrl: String): URL = (
    if (isURL(fileOrUrl)) {
        URL(fileOrUrl).toURI()
    } else {
        File(fileOrUrl).toURI()
    }
    ).toURL()

private fun getFormat(type: CSVType, delimiter: Char, headers: List<String>, duplicate: Boolean): CSVFormat =
    type.format.withDelimiter(delimiter).withHeader(*headers.toTypedArray()).withAllowDuplicateHeaderNames(duplicate)

public fun DataFrame.Companion.readDelim(
    inStream: InputStream,
    delimiter: Char = ',',
    headers: List<String> = listOf(),
    nullStrings: Set<String> = setOfNullStrings,
    isCompressed: Boolean = false,
    csvType: CSVType,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = defaultCharset,
    parserOptions: ParserOptions? = null,
): AnyFrame =
    if (isCompressed) {
        InputStreamReader(GZIPInputStream(inStream), charset)
    } else {
        BufferedReader(InputStreamReader(inStream, charset))
    }.run {
        readDelim(this, getFormat(csvType, delimiter, headers, duplicate), nullStrings, colTypes, skipLines, readLines, parserOptions)
    }

internal fun isURL(fileOrUrl: String): Boolean = listOf("http:", "https:", "ftp:").any { fileOrUrl.startsWith(it) }

public enum class ColType {
    Int,
    Long,
    Double,
    Boolean,
    BigDecimal,
    LocalDate,
    LocalTime,
    LocalDateTime,
    String,
}

public fun ColType.toType(): KClass<out Any> = when (this) {
    ColType.Int -> Int::class
    ColType.Long -> Long::class
    ColType.Double -> Double::class
    ColType.Boolean -> Boolean::class
    ColType.BigDecimal -> BigDecimal::class
    ColType.LocalDate -> LocalDate::class
    ColType.LocalTime -> LocalTime::class
    ColType.LocalDateTime -> LocalDateTime::class
    ColType.String -> String::class
}

public fun DataFrame.Companion.readDelim(
    reader: Reader,
    format: CSVFormat = CSVFormat.DEFAULT.withHeader(),
    nullStrings: Set<String> = setOfNullStrings,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    parserOptions: ParserOptions? = null
): AnyFrame {
    var reader = reader
    if (skipLines > 0) {
        reader = BufferedReader(reader)
        repeat(skipLines) { reader.readLine() }
    }

    format.parse(reader).use { csvParser ->
        val records = if (readLines == null) {
            csvParser.records
        } else {
            require(readLines >= 0) { "`readLines` must not be negative" }
            val records = ArrayList<CSVRecord>(readLines)
            val iter = csvParser.iterator()
            var count = readLines ?: 0
            while (iter.hasNext() && 0 < count--) {
                records.add(iter.next())
            }
            records
        }

        val columnNames = csvParser.headerNames.takeIf { it.isNotEmpty() }
            ?: (1..records[0].count()).map { index -> "X$index" }

        val generator = ColumnNameGenerator()
        val uniqueNames = columnNames.map { generator.addUnique(it) }

        val cols = uniqueNames.mapIndexed { colIndex, colName ->
            val defaultColType = colTypes[".default"]
            val colType = colTypes[colName] ?: defaultColType
            var hasNulls = false
            val values = records.map { it[colIndex]?.emptyAsNull(nullStrings).also { if (it == null) hasNulls = true } }
            val column = column(colName, values, hasNulls)
            when (colType) {
                null -> column.tryParse(parserOptions)
                ColType.String -> column
                else -> {
                    val parser = Parsers[colType.toType()]!!
                    column.parse(parser, parserOptions)
                }
            }
        }
        return cols.toDataFrame()
    }
}

internal fun String.emptyAsNull(nullStrings: Set<String>): String? =
    when {
        this.isEmpty() -> null
        nullStrings.contains(this) -> null
        else -> this
    }

public fun AnyFrame.writeCSV(file: File, format: CSVFormat = CSVFormat.DEFAULT.withHeader()): Unit =
    writeCSV(FileWriter(file), format)

public fun AnyFrame.writeCSV(path: String, format: CSVFormat = CSVFormat.DEFAULT.withHeader()): Unit =
    writeCSV(FileWriter(path), format)

public fun AnyFrame.writeCSV(writer: Appendable, format: CSVFormat = CSVFormat.DEFAULT.withHeader()): Unit =
    format.print(writer).use { printer ->
        printer.printRecord(columnNames())
        forEach {
            printer.printRecord(it.values)
        }
    }
