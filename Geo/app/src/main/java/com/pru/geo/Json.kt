package com.pru.geo

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.geotools.data.FileDataStoreFinder
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.opengis.feature.simple.SimpleFeature
import java.io.File


object Json {

    @JvmStatic
    fun main(args: Array<String>){
        val shapeDir = File("temp/shape-file.shp")
        val store = FileDataStoreFinder.getDataStore(shapeDir)
        val featureSource = store.featureSource
        val collection = featureSource.features
        val simpleF = collection.features()
        simpleF.use { itr1 ->
            while (itr1.hasNext()) {
                val f: SimpleFeature = itr1.next()
                val g = f.defaultGeometryProperty.value
                println(GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(g))
            }
        }
    }
}