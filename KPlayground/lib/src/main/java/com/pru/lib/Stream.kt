package com.pru.lib

data class R(
    val id: Int
)

data class CL(
    val list: List<R>
)

data class ML(
    val list: List<CL>
)

fun main() {
    val processedData = listOf(R(id = 1), R(id = 2), R(id = 1), R(id = 2), R(id = 3))
    val uIds = processedData.map { it.id }.toSet().toList()
    val tempMap: MutableMap<Int, MutableList<R>> = mutableMapOf()
    for (id in uIds) {
        for (gr in processedData) {
            if (id == gr.id) {
                if (tempMap.containsKey(id)) {
                    val old = tempMap[id]!!
                    old.add(gr)
                    tempMap[id] = old
                } else {
                    val list = mutableListOf(gr)
                    tempMap[id] = list
                }
            }
        }
    }
    println(tempMap)
    val cls = mutableListOf<CL>()
    for (m in tempMap) {
        val cl = CL(m.value)
        cls.add(cl)
    }
    val m = ML(cls)
    println(m)
}