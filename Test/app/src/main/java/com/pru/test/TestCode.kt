package com.pru.test

fun main() {
    val res = handle("id,name,age$1,Jack,34$3,NULL,45$4,NULL,NULL")
//    val res = handle("id,name,age$1,Jack,34$3,NULL,45$4,NULL,NULL")
//    val res = handle("id,name,age$1,Jack,34$3,NULL,45$4,hgg,nULL")
    println(res)
}

fun handle(value: String): String {
    println(value)
    println("------------------------------")
    var res = ""
    val rows = value.split("$")
    for (i in rows.indices) {
        val items = rows[i].split(",")
        if (items.contains("NULL")) {
            continue
        } else {
            res = res.plus(items.joinToString(",")).plus("$")
        }
    }
    return res.dropLast(1)
}

data class Student(val name: String, val subscribedCourses: List<Course>)

data class Course(val id: Int, val name: String, val isPaid: Boolean) : Comparable<Int> {
    override fun compareTo(other: Int): Int {
        return id.compareTo(other)
    }
}

fun get(courseCount: Int): Map<Course, Int> {
    val repository = List(20) {
        Student(name = "S$it", subscribedCourses = List((0..10).random()) {
            Course(id = it, name = "C$it", isPaid = true)
        })
    }
    val map = HashMap<Course, Int>()
    /*repository.get()*/
    repository.forEach { s ->
        s.subscribedCourses.forEach { c ->
            if (c.isPaid) {
                if (map.containsKey(c)) {
                    val o = map[c]!!
                    map[c] = o + 1
                } else {
                    map[c] = 1
                }
            }
        }
    }

    if (map.size > courseCount) {
        return map.toList().sortedByDescending { (_, value) -> value }
            .dropLast(map.size - courseCount).toMap()
    }
    return map.toList().sortedByDescending { (_, value) -> value }.toMap()
}