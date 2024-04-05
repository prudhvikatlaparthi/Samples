package com.pru.geo

import com.google.gson.Gson
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
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable
import java.util.function.Consumer
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


object Main {
    private fun toFeature(
        locations: List<PolygonPoint>, simpleFeatureType: SimpleFeatureType,
        geometryFactory: GeometryFactory
    ): SimpleFeature {
        val coordinates = arrayOfNulls<Coordinate>(locations.size)
        for ((i, location) in locations.withIndex()) {
            val coordinate = Coordinate(location.x, location.y, 0.0)
            coordinates[i] = coordinate
        }
        val polygon = geometryFactory.createPolygon(coordinates)
        println(polygon.toString())
        val featureBuilder = SimpleFeatureBuilder(simpleFeatureType)
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

        /*locations.add(PolygonPoint(16.74639115140195, 81.68919011950491))
        locations.add(PolygonPoint(16.747630748067472, 81.68983519077301))
        locations.add(PolygonPoint(16.74869536284878, 81.69042326509953))
        locations.add(PolygonPoint(16.747833012491814, 81.69183243066074))
        locations.add(PolygonPoint(16.74729845604674, 81.69169060885906))
        locations.add(PolygonPoint(16.746305429189693, 81.69248487800361))
        locations.add(PolygonPoint(16.74417552482844, 81.69247649610043))
        locations.add(PolygonPoint(16.74285531890929, 81.68976176530123))
        locations.add(PolygonPoint(16.743084557655898, 81.68502397835255))
        locations.add(PolygonPoint(16.74777522267821, 81.68424345552921))
        locations.add(PolygonPoint(16.74639115140195, 81.68919011950491))*/



            locations.add(PolygonPoint(462201.62459999975, 7141476.3589))
            locations.add(PolygonPoint(462216.1370000001, 7141473.017))
            locations.add(PolygonPoint(462223.06919999979, 7141502.4133))
            locations.add(PolygonPoint(462208.28029999975, 7141505.6907))
            locations.add(PolygonPoint(462201.62459999975, 7141476.3589))
    }

    @Throws(IOException::class, SchemaException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        prepareShapeFile()
    }


    fun prepareShapeFile() {
        try {
            val locations: MutableList<PolygonPoint> = ArrayList()
            initData(locations)

            // create simple feature builder for the locations
            val builder = SimpleFeatureTypeBuilder()
            builder.name = "polygonFeature"
            builder.crs = DefaultGeographicCRS.WGS84
            builder.add("the_geom", Polygon::class.java)
            val simpleFeatureType = builder.buildFeatureType()
            val collection = DefaultFeatureCollection()
            val geometryFactory = JTSFactoryFinder.getGeometryFactory(null)
            val feature = toFeature(locations, simpleFeatureType, geometryFactory)
            val json = Gson().toJson(feature.defaultGeometry)
            println("---------------------")
            println(json)
            collection.add(feature)
//            collection.forEach(Consumer { name: SimpleFeature? -> println(name) })
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
            var tempDir = File("temp")
            if (tempDir.exists()) {
                tempDir.delete()
                tempDir = File("temp")
            }
            tempDir.mkdir()
            val shapeFile = File(File("temp/shape-").absolutePath + "file.shp")
            val params: MutableMap<String, Serializable?> = HashMap()
            params["url"] = shapeFile.toURI().toURL()
            params["create spatial index"] = true
            val dataStoreFactory = ShapefileDataStoreFactory()
            val dataStore = dataStoreFactory.createNewDataStore(params) as ShapefileDataStore
            dataStore.createSchema(simpleFeatureType)
            val transaction: Transaction = DefaultTransaction("create")
            val typeName = dataStore.typeNames[0]
            val featureSource: SimpleFeatureSource = dataStore.getFeatureSource(typeName)
            if (featureSource is SimpleFeatureStore) {
                featureSource.transaction = transaction
                try {
                    featureSource.addFeatures(collection)
                    transaction.commit()
                } catch (problem: Exception) {
                    transaction.rollback()
                } finally {
                    transaction.close()
                }
            }

            formatZipFile(tempDir)
        } catch (e: NoClassDefFoundError) {
            e.printStackTrace()
        }
    }

    private fun formatZipFile(temp: File) {
        var shapeDir = File("shape")
        if (shapeDir.exists()) {
            shapeDir.delete()
            shapeDir = File("shape")
        }
        shapeDir.mkdir()
        zip(files = temp.listFiles()?.map { it.absolutePath } ?: listOf(),
            zipFile = File(File("shape/shape-").absolutePath + "file.zip").absolutePath)
        deleteRecursive(temp)
    }

    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()!!) deleteRecursive(
            child
        )
        fileOrDirectory.delete()
    }

    @Throws(IOException::class)
    fun zip(files: List<String>, zipFile: String) {
        val bufferSize = 6 * 1024
        val zipOut = ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile)))
        zipOut.use { out ->
            val data = ByteArray(bufferSize)
            for (i in files.indices) {
                val fi = FileInputStream(files[i])
                val bufInStream = BufferedInputStream(fi, bufferSize)
                bufInStream.use { origin ->
                    val entry = ZipEntry(files[i].substring(files[i].lastIndexOf("\\") + 1))
                    out.putNextEntry(entry)
                    var count: Int
                    while (origin.read(data, 0, bufferSize).also { count = it } != -1) {
                        out.write(data, 0, count)
                    }
                }
            }
        }
    }

    internal data class PolygonPoint(var x: Double, var y: Double)
}