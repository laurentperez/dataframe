package org.jetbrains.kotlinx.dataframe.documentation

internal interface DocumentationUrls {

    /** https://kotlin.github.io/dataframe */
    interface Url

    interface DataRow {

        /** [See Row Expressions on the documentation website.]({@include [Url]}/datarow.html#row-expressions) */
        interface RowExpressions

        /** [See Row Conditions on the documentation website.]({@include [Url]}/datarow.html#row-conditions) */
        interface RowConditions
    }

    /** [See `NaN` and `NA` on the documentation website.]({@include [Url]}/nanAndNa.html) */
    interface NanAndNa {

        /** [See `NaN` on the documentation website.]({@include [Url]}/nanAndNa.html#nan) */
        interface NaN

        /** [See `NA` on the documentation website.]({@include [Url]}/nanAndNa.html#na) */
        interface NA
    }

    /** [See `update` on the documentation website.]({@include [Url]}/update.html) */
    interface Update

    /** [See `fill` on the documentation website.]({@include [Url]}/fill.html) */
    interface Fill {

        /** [See `fillNulls` on the documentation website.]({@include [Url]}/fill.html#fillnulls) */
        interface FillNulls

        /** [See `fillNaNs` on the documentation website.]({@include [Url]}/fill.html#fillnans) */
        interface FillNaNs

        /** [See `fillNA` on the documentation website.]({@include [Url]}/fill.html#fillna) */
        interface FillNA
    }

    /** [See `drop` on the documentation website.]({@include [Url]}/drop.html) */
    interface Drop {

        /** [See `dropNulls` on the documentation website.]({@include [Url]}/drop.html#dropnulls) */
        interface DropNulls

        /** [See `dropNaNs` on the documentation website.]({@include [Url]}/drop.html#dropnans) */
        interface DropNaNs

        /** [See `dropNA` on the documentation website.]({@include [Url]}/drop.html#dropna) */
        interface DropNA
    }

    /** [See Access APIs on the documentation website.]({@include [Url]}/apilevels.html) */
    interface AccessApis {

        /** [See String API on the documentation website.]({@include [Url]}/stringapi.html) */
        interface StringApi

        /** [See Column Accessors API on the documentation website.]({@include [Url]}/columnaccessorsapi.html) */
        interface ColumnAccessorsApi

        /** [See KProperties API on the documentation website.]({@include [Url]}/kpropertiesapi.html) */
        interface KPropertiesApi

        /** [See Extension Properties API on the documentation website.]({@include [Url]}/extensionpropertiesapi.html) */
        interface ExtensionPropertiesApi
    }

    /** [See Column Selectors on the documentation website.]({@include [Url]}/columnselectors.html) */
    interface ColumnSelectors
}
