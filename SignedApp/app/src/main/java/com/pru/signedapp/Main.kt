package com.pru.signedapp

import org.geotools.data.DefaultTransaction
import org.geotools.data.Transaction
import org.geotools.data.shapefile.ShapefileDataStore
import org.geotools.data.shapefile.ShapefileDataStoreFactory
import org.geotools.data.simple.SimpleFeatureSource
import org.geotools.data.simple.SimpleFeatureStore
import org.geotools.feature.DefaultFeatureCollection
import org.geotools.feature.SchemaException
import org.geotools.feature.simple.SimpleFeatureBuilder
import org.geotools.feature.simple.SimpleFeatureTypeBuilder
import org.geotools.geometry.jts.JTSFactoryFinder
import org.geotools.referencing.crs.DefaultGeographicCRS
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon
import org.opengis.feature.simple.SimpleFeature
import org.opengis.feature.simple.SimpleFeatureType
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.lang.Boolean
import java.util.function.Consumer
import kotlin.Array
import kotlin.Double
import kotlin.Exception
import kotlin.String
import kotlin.Throws
import kotlin.arrayOfNulls


object Main {
    private fun toFeature(
        locations: List<PolygonPoint>, POLYGON: SimpleFeatureType,
        geometryFactory: GeometryFactory
    ): SimpleFeature {
        val coords = arrayOfNulls<Coordinate>(locations.size)
        var i = 0
        for (location in locations) {
            val coord = Coordinate(location.x, location.y, 0.0)
            coords[i] = coord
            i++
        }
        val polygon = geometryFactory.createPolygon(coords)
        println(polygon.toString())
        /**
         * Degub print in this point looks like this: POLYGON ((60.15396170672204
         * 24.665516804291176, 60.1548304906673 24.67437097771886, 60.156868902093464
         * 24.684907435753292, 60.16053593400762 24.681227944085034, 60.16351621152651
         * 24.677017850437107, 60.1641222365678 24.670587380672547, 60.163303711361145
         * 24.664257803508647, 60.16193970279525 24.6590194867117, 60.158682674758644
         * 24.65963863846621, 60.15396170672204 24.665516804291176)) So looks okay so
         * far...
         */
        val featureBuilder = SimpleFeatureBuilder(POLYGON)
        featureBuilder.add(polygon)
        return featureBuilder.buildFeature(null)
    }

    private fun initData(locations: MutableList<PolygonPoint>) {
        /*locations.add(PolygonPoint(60.1539617067220433, 24.6655168042911761))
        locations.add(PolygonPoint(60.1548304906673010, 24.6743709777188585))
        locations.add(PolygonPoint(60.1568689020934642, 24.6849074357532920))
        locations.add(PolygonPoint(60.1605359340076191, 24.6812279440850340))
        locations.add(PolygonPoint(60.1635162115265132, 24.6770178504371067))
        locations.add(PolygonPoint(60.1641222365677990, 24.6705873806725471))
        locations.add(PolygonPoint(60.1633037113611451, 24.6642578035086473))
        locations.add(PolygonPoint(60.1619397027952516, 24.6590194867116992))
        locations.add(PolygonPoint(60.1586826747586443, 24.6596386384662090))
        locations.add(PolygonPoint(60.1539617067220433, 24.6655168042911761))*/

        locations.add(PolygonPoint(16.74639115140195, 81.68919011950491))
        locations.add(PolygonPoint(16.747630748067472, 81.68983519077301))
        locations.add(PolygonPoint(16.74869536284878, 81.69042326509953))
        locations.add(PolygonPoint(16.747833012491814, 81.69183243066074))
        locations.add(PolygonPoint(16.74729845604674, 81.69169060885906))
        locations.add(PolygonPoint(16.746305429189693, 81.69248487800361))
        locations.add(PolygonPoint(16.74417552482844, 81.69247649610043))
        locations.add(PolygonPoint(16.74285531890929, 81.68976176530123))
        locations.add(PolygonPoint(16.743084557655898, 81.68502397835255))
        locations.add(PolygonPoint(16.74777522267821, 81.68424345552921))
        locations.add(PolygonPoint(16.74639115140195, 81.68919011950491))
    }

    @Throws(IOException::class, SchemaException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val locations: MutableList<PolygonPoint> = ArrayList()
        initData(locations)

        // create simple feature builder for the locations
        val builder = SimpleFeatureTypeBuilder()
        builder.name = "polygonFeature"
        builder.crs = DefaultGeographicCRS.WGS84
//        builder.add(Polygon.TYPENAME_POLYGON, Polygon::class.java)
        builder.add("the_geom", Polygon::class.java)
        val POLYGON = builder.buildFeatureType()
        val collection = DefaultFeatureCollection()
        val geometryFactory = JTSFactoryFinder.getGeometryFactory(null)
        val feature = toFeature(locations, POLYGON, geometryFactory)
        collection.add(feature)
        collection.forEach(Consumer { name: SimpleFeature? -> println(name) })
        /**
         * Degub print in this point looks like this:
         * SimpleFeatureImpl:polygonFeature=[SimpleFeatureImpl.Attribute:
         * Polygon<Polygon id=fid-77f7c041_174f9627c85_-8000>=POLYGON
         * ((60.15396170672204 24.665516804291176, 60.1548304906673 24.67437097771886,
         * 60.156868902093464 24.684907435753292, 60.16053593400762 24.681227944085034,
         * 60.16351621152651 24.677017850437107, 60.1641222365678 24.670587380672547,
         * 60.163303711361145 24.664257803508647, 60.16193970279525 24.6590194867117,
         * 60.158682674758644 24.65963863846621, 60.15396170672204 24.665516804291176))]
         * So looks okay still...
        </Polygon> */
        val shapeFile = File(File("2020-").absolutePath + "shapefile.shp")
        val params: MutableMap<String, Serializable?> = HashMap()
        params["url"] = shapeFile.toURI().toURL()
        params["create spatial index"] = Boolean.TRUE
        val dataStoreFactory = ShapefileDataStoreFactory()
        val dataStore = dataStoreFactory.createNewDataStore(params) as ShapefileDataStore
        dataStore.createSchema(POLYGON)
        val transaction: Transaction = DefaultTransaction("create")
        val typeName = dataStore.typeNames[0]
        val featureSource: SimpleFeatureSource = dataStore.getFeatureSource(typeName)
        if (featureSource is SimpleFeatureStore) {
            val featureStore = featureSource
            featureStore.transaction = transaction
            try {
                featureStore.addFeatures(collection)
                transaction.commit()
            } catch (problem: Exception) {
                transaction.rollback()
            } finally {
                transaction.close()
            }
        }
    }

    internal class PolygonPoint(var x: Double, var y: Double)
}