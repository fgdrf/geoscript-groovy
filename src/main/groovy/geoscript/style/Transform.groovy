package geoscript.style

import geoscript.filter.Function
import org.geotools.styling.Rule
import org.geotools.styling.Symbolizer as GtSymbolizer
import org.geotools.styling.TextSymbolizer
import org.geotools.styling.FeatureTypeStyle
import org.geotools.process.function.ProcessFunction

/**
 * A Symbolizer that wraps a geoscript.filter.Function generally
 * used for transforming Geometry or String or Date formatting.
 * <p>You can create a Transform from a CQL statement:</p>
 * <p><code>Transform transform = new Transform("centroid(the_geom)")</code></p>
 * <p>Or from a geoscript.filter.Function:</p>
 * <p><code>Transform transform1 = new Transform(new Function("myCentroid", {g -> g.centroid}))</code></p>
 * @author Jared Erickson
 */
class Transform extends Symbolizer {

    /**
     * The geoscript.filter.Function
     */
    private Function function

    /**
     * The Transform type (normal and rendering)
     */
    private Type type;

    /**
     * The Type enumeration
     */
    public static enum Type {
        NORMAL,
        RENDERING
    }

    /**
     * Type constants for ease of use
     */
    public static final Type NORMAL = Type.NORMAL
    public static final Type RENDERING = Type.RENDERING

    /**
     * Create a new Transform from a Function.
     * <p><code>Transform transform1 = new Transform(new Function("myCentroid", {g -> g.centroid}))</code></p>
     * @param function The geoscript.filter.Function
     */
    Transform(Function function, Type type = Type.NORMAL) {
        super()
        this.function = function
        this.type = type
    }
    
    /**
     * Create a new Transform from a CQL filter function.
     * <p><code>Transform transform = new Transform("centroid(the_geom)")</code></p>
     * @param cql A CQL string 
     */
    Transform(String cql, Type type = Type.NORMAL) {
        this(new Function(cql), type)
    }

    /**
     * Prepare the GeoTools FeatureTypeStyle and Rule by applying this Symbolizer.
     * @param fts The GeoTools FeatureTypeStyle
     * @param rule The GeoTools Rule
     */
    @Override
    protected void prepare(FeatureTypeStyle fts, Rule rule) {
        if (/*function.function.class.name.equals("org.geotools.process.function.ProcessFunction") ||
            function.function.class.name.equals("org.geotools.process.function.RenderingProcessFunction")*/
            this.type == Type.RENDERING
        ) {
            fts.transformation = function.function
        } else {
            prepare(rule)
        }
    }

    /**
     * Prepare the GeoTools Rule by applying this Symbolizer
     * @param rule The GeoTools Rule
     */
    @Override
    protected void prepare(Rule rule) {
        super.prepare(rule)
        rule.symbolizers().each{s ->
            apply(s)
        }
    }

    /**
     * Apply this Symbolizer to the GeoTools Symbolizer
     * @param sym The GeoTools Symbolizer
     */
    @Override
    protected void apply(GtSymbolizer sym) {
        super.apply(sym)
        if (sym instanceof TextSymbolizer) {
            sym.label = function.function
        } else {
            sym.geometry = function.function
        }
    }

    /**
     * Get the Function
     * @return The Function
     */
    Function getFunction() {
        this.function
    }

    /**
     * Get the Type
     * @return The Type
     */
    Type getType() {
        this.type
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        buildString("Transform", ['function': function])
    }
}

