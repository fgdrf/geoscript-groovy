package geoscript.layer
/**
 * A Format that can read and write ArcGrids
 * @author Jared Erickson
 */
class ArcGrid extends Format {

    /**
     * Create a new ArcGrid
     */
    ArcGrid() {
        super(new org.geotools.gce.arcgrid.ArcGridFormat())
    }
}
