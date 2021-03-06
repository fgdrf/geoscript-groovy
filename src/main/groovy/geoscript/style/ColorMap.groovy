package geoscript.style

import geoscript.filter.Color
import geoscript.filter.Expression
import geoscript.layer.Raster
import org.geotools.styling.Rule
import org.geotools.styling.RasterSymbolizer
import org.geotools.styling.Symbolizer as GtSymbolizer

/**
 * A ColorMap is a Symbolizer used to style Rasters.
 * @author Jared Erickson
 */
class ColorMap extends geoscript.style.RasterSymbolizer {

    /**
     * The ColorMap type (intervals, values, ramp)
     */
    String type = "ramp"

    /**
     * A List of ColorMap values
     */
    List<Map> values

    /**
     * Whether to use extended colors or not
     */
    boolean extended = false

    /**
     * Create a new ColorMap
     * @param values A List of ColorMap values
     * @param type The type
     * @param extended Whether to use extended colors
     */
    ColorMap(List values, String type = "ramp", boolean extended = false) {
        super()
        this.values = values
        this.type = type
        this.extended = extended
    }

    /**
     * Create a new ColorMap.
     * @param map A Map of named parameters.
     */
    ColorMap(Map map) {
        super()
        map.each{k,v->
            if(this.hasProperty(k as String)){
                this."$k" = v
            }
        }
    }

    /**
     * Create a ColorMap for a Raster with a palette of colors (from Color Brewer) with the given
     * number of categories
     * @param raster The Raster
     * @param palette The color palette name (from Color Brewer)
     * @param numberOfCategories The number of categories
     * @param band The band to use for min and max values
     * @param type The type of interpolation
     * @param extended Whether to use extended colors
     */
    ColorMap (Raster raster, String palette, int numberOfCategories, int band = 0, String type = "ramp", boolean extended = false) {
        Map extrema = raster.extrema
        double min = extrema.min[band]
        double max = extrema.max[band]
        double spread = (max - min) / (numberOfCategories - 1)
        List colors = Color.getPaletteColors(palette, numberOfCategories)
        double quantity = min
        List values = (0..<numberOfCategories).collect{i ->
            def value = [quantity: quantity, color: colors[i]]
            quantity += spread
            value
        }
        this.values = values
        this.type = type
        this.extended = extended
    }

    /**
     * Prepare the GeoTools Rule by applying this Symbolizer
     * @param rule The GeoTools Rule
     */
    @Override
    protected void prepare(Rule rule) {
        super.prepare(rule)
        getGeoToolsSymbolizers(rule, RasterSymbolizer).each{s ->
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
        sym.colorMap = generateColorMap(values, type, extended)
    }

    /**
     * Generate a GeoTool's ColorMap from A List of Maps with color, opacity, quantity, and label keys
     * @param values A List of Maps
     * @return A GeoTool's ColorMap
     */
    protected org.geotools.styling.ColorMap generateColorMap(List<Map> values, String type, boolean extended) {
        def colorMap = styleFactory.createColorMap();
        colorMap.extendedColors = extended
        if (type.equalsIgnoreCase("ramp")) {
            colorMap.type = org.geotools.styling.ColorMap.TYPE_RAMP
        } else if (type.equalsIgnoreCase("intervals")) {
            colorMap.type = org.geotools.styling.ColorMap.TYPE_INTERVALS
        } else if (type.equalsIgnoreCase("values")) {
            colorMap.type = org.geotools.styling.ColorMap.TYPE_VALUES
        }
        values.each {value ->
            def entry = styleFactory.createColorMapEntry()
            if (value.containsKey("color")) entry.color = new Expression(value.color).expr
            entry.opacity = new Expression(value.containsKey("opacity") ? value.opacity : 1.0).expr
            if (value.containsKey("quantity")) entry.quantity = new Expression(value.quantity).expr
            if (value.containsKey("label")) entry.label = new Expression(value.label).expr
            colorMap.addColorMapEntry(entry)
        }
        colorMap
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        buildString("ColorMap", ['values': values, 'type': type, 'extended': extended])
    }
}
