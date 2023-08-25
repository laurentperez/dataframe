package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnGroupRef
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnSetRef
import kotlin.reflect.KProperty

public interface UsageTemplateColumnsSelectionDsl {

    /**
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### In the plain DSL:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnSet][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnSetDef]
     *
     *  
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a column group reference:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnGroup][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnGroupDef]
     *
     *  
     *
     *
     *
     *
     *
     *
     */
    public interface UsageTemplate {

        // region parts

        /* Can be set to nothing to disable the plain dsl part */
        public interface PlainDslPart

        /* Can be set to nothing to disable the column set part */
        public interface ColumnSetPart

        /* Can be set to nothing to disable the column group part */
        public interface ColumnGroupPart

        // endregion

        // region Template arguments

        /* What to put in definitions part aside from the default part. */
        public interface DefinitionsArg

        /* What to put in the plain dsl part. Does not need indents. */
        public interface PlainDslFunctionsArg

        /* What to put in the column set part. Needs indents. */
        public interface ColumnSetFunctionsArg

        /* What to put in the column group part. Needs indents. */
        public interface ColumnGroupFunctionsArg

        // endregion

        // region Definitions for at the top of the template

        /** `columnSet: `[ColumnSet][ColumnSet]`<*>` */
        public interface ColumnSetDef

        /**
         * `columnGroup: `[SingleColumn][SingleColumn]`<`[DataRow][DataRow]`<*>> | `[String][String]
         *
         * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
         * `| `[KProperty][KProperty]`<*>` | `[ColumnPath][ColumnPath]
         */
        public interface ColumnGroupDef

        /**
         * `column: `[ColumnAccessor][ColumnAccessor]` | `[String][String]
         *
         * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
         * `| `[KProperty][KProperty]`<*> | `[ColumnPath][ColumnPath]
         */
        public interface ColumnDef

        /** `T: Column type` */
        public interface ColumnTypeDef

        /** `index: `[Int][Int] */
        public interface IndexDef

        /** `indexRange: `[IntRange][IntRange] */
        public interface IndexRangeDef

        /** `condition: `[ColumnFilter][ColumnFilter] */
        public interface ConditionDef

        /** `kind: `[ColumnKind][ColumnKind] */
        public interface ColumnKindDef

        // endregion

        // region References to the definitions

        /** [columnSet][ColumnSetDef] */
        public interface ColumnSetRef

        /** [columnGroup][ColumnGroupDef] */
        public interface ColumnGroupRef

        /** [condition][ConditionDef] */
        public interface ConditionRef

        /** [column][ColumnDef] */
        public interface ColumnRef

        /** [index][IndexDef] */
        public interface IndexRef

        /** [indexRange][IndexRangeDef] */
        public interface IndexRangeRef

        /** [T][ColumnTypeDef] */
        public interface ColumnTypeRef

        /** [kind][ColumnKindDef] */
        public interface ColumnKindRef

        // endregion
    }
}
