package com.pru.geoshape

import com.pru.geoshape.model.PolygonPoint
import org.geotools.data.DefaultTransaction
import org.geotools.data.Transaction
import org.geotools.data.shapefile.ShapefileDataStore
import org.geotools.data.shapefile.ShapefileDataStoreFactory
import org.geotools.data.simple.SimpleFeatureSource
import org.geotools.data.simple.SimpleFeatureStore
import org.geotools.feature.DefaultFeatureCollection
import org.geotools.feature.simple.SimpleFeatureBuilder
import org.geotools.feature.simple.SimpleFeatureTypeBuilder
import org.geotools.geometry.jts.JTSFactoryFinder
import org.geotools.referencing.crs.DefaultGeographicCRS
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon
import org.opengis.feature.simple.SimpleFeature
import org.opengis.feature.simple.SimpleFeatureType
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


object Main {
    private fun toFeature(
        locations: List<PolygonPoint>, simpleFeatureType: SimpleFeatureType, geometryFactory: GeometryFactory
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


    fun prepareShapeFile(polyList: List<PolygonPoint>): File {

        // create simple feature builder for the locations
        val builder = SimpleFeatureTypeBuilder()
        builder.name = "polygonFeature"
        builder.crs = DefaultGeographicCRS.WGS84
        builder.add("the_geom", Polygon::class.java)
        val simpleFeatureType = builder.buildFeatureType()
        val collection = DefaultFeatureCollection()
        val geometryFactory = JTSFactoryFinder.getGeometryFactory(null)
        val feature = toFeature(polyList, simpleFeatureType, geometryFactory)
//            val json = Gson().toJson(feature.defaultGeometry)
        println("---------------------")
//            println(""+json)
        collection.add(feature)
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

        return formatZipFile(tempDir)
    }

    private fun formatZipFile(temp: File): File {
        var shapeDir = File("shape")
        if (shapeDir.exists()) {
            shapeDir.delete()
            shapeDir = File("shape")
        }
        shapeDir.mkdir()
        val shapeFile = File(File("shape/shape-").absolutePath + "file${System.currentTimeMillis()}.zip")
        zip(files = temp.listFiles()?.map { it.absolutePath } ?: listOf(), zipFile = shapeFile.absolutePath)
        deleteRecursive(temp)
        return shapeFile
    }

    fun deleteRecursive(fileOrDirectory: File) {
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
}