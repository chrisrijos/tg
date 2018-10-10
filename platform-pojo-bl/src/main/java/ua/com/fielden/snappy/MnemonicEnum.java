package ua.com.fielden.snappy;

/**
 * Represents mnemonics for atomic date range widths (days, weeks, .., quarters, .., years), and some number mnemonics.
 *
 * @author Jhou
 *
 */
public enum MnemonicEnum {
    /////////////////////////////////////////
    /////// USED BY ENTITY CENTRES //////////
    /////////////////////////////////////////
    DAY,
    WEEK,
    MONTH,
    YEAR, // Date type mnemonics
    QRT1,
    QRT2,
    QRT3,
    QRT4, // Year quarters
    /** Financial year for Australia, Egypt, New Zealand (government), Pakistan, Sweden (part of corporations) -- [1 July Curr Year; 1 July Next Year) */
    OZ_FIN_YEAR, //

    ///////////////////////////////////////////
    /////// NOT USED BY ENTITY CENTRES ////////
    ///////////////////////////////////////////
    PERCENTAGE, // Number type mnemonics
    /** Day without right limit */
    DAY_AND_BEFORE,
    /** Day without left limit */
    DAY_AND_AFTER
}
