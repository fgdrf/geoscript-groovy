package geoscript.layer

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import geoscript.workspace.Directory
import geoscript.workspace.Workspace
import org.junit.Test
import static org.junit.Assert.*

/**
 * The Shapefile UnitTest
 */
class ShapefileTestCase {

    @Test void constructors() {

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)

        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)
        assertEquals file, shp.file

        println("Number of Features: ${shp.count()}")
        println("Bounds: ${shp.bounds()}")
        assertEquals 49, shp.count()
        assertEquals "(-124.73142200000001,24.955967,-66.969849,49.371735,EPSG:4326)", shp.bounds().toString()
    }

    @Test void bounds() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        assertEquals "(-124.73142200000001,24.955967,-66.969849,49.371735,EPSG:4326)", shp.bounds.toString()
        assertEquals "(-109.055199,36.988972000000004,-102.036758,41.00341,EPSG:4326)", shp.bounds("STATE_NAME = 'Colorado'").toString()
    }

    @Test void longFieldNames() {
        File file = File.createTempFile("points",".shp")
        String name = file.name.replaceAll(".shp","")
        Schema schema = new Schema(name, [
            new Field("the_geom","Point","EPSG:4326"),
            new Field("id","int"),
            new Field("areallylongfieldname","String")
        ])
        Directory d = new Directory(file.getParentFile())
        Layer layer = d.create(schema)
        d.close()
        Shapefile shp = new Shapefile(file)
        assertEquals 0, shp.count
        shp.add([
            the_geom: new Point(1,1),
            id: 1,
            areallylongfieldname: "test1"
        ])
        assertEquals 1, shp.count
        List features = shp.features
        assertEquals "test1", features[0]["areallylon"]
        assertEquals "test1", features[0]["areallylongfieldname"]
        features[0]["areallylongfieldname"] = "test2"
        assertEquals "test2", features[0]["areallylongfieldname"]

        Feature f = shp.schema.feature([
                the_geom: new Point(2,2),
                id: 2,
                areallylongfieldname: "test22"
        ])
        shp.add(f)
        assertEquals 2, shp.count
        features = shp.features
        assertEquals "test22", features[1]["areallylon"]
        assertEquals "test22", features[1]["areallylongfieldname"]
    }
}

