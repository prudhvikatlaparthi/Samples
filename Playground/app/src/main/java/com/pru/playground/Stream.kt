package com.pru.playground

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
    val rows = listOf(R(id = 1), R(id = 2), R(id = 1), R(id = 2), R(id = 3))
    val urids = rows.map { it.id }.toSet().toList()
    val cls: MutableMap<Int, MutableList<R>> = mutableMapOf()
    for (id in urids) {
        for (gr in rows) {
            if (id == gr.id) {
                if (cls.containsKey(id)) {
                    val old = cls[id]!!
                    old.add(gr)
                    cls[id] = old
                } else {
                    val list = mutableListOf(gr)
                    cls[id] = list
                }
            }
        }
    }
    println(cls)
    val acls = mutableListOf<CL>()
    for (m in cls){
        val cl = CL(m.value)
        acls.add(cl)
    }
    val m = ML(acls)
    println(m)
}