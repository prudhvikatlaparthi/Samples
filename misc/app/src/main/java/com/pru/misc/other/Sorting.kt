fun main() {
    val people = mutableListOf<Int>(
        5, 8, 6, 4, 2, 8,1
    )
//    selectionSort(people = people)
//    bubbleSort(people = people)
//    insertionSort(people = people)
    mergeSort(people, 0, people.size - 1)
    println(people)
}

private fun selectionSort(people: MutableList<Int>) {
    println(people)
    var min: Int
    for (i in 0 until people.size) {
        min = i
        for (j in i + 1 until people.size) {
            if (people[min] > people[j]) {
                val temp = people[j]
                people[j] = people[min]
                people[min] = temp
            }
        }
    }
    println(people)
}

private fun bubbleSort(people: MutableList<Int>) {
    println(people)
    for (i in 0 until people.size) {
        for (j in 0 until people.size - 1) {
            if (people[j] > people[j + 1]) {
                val temp = people[j + 1]
                people[j + 1] = people[j]
                people[j] = temp
            }
        }
    }
    println(people)
}

private fun insertionSort(people: MutableList<Int>) {
    println(people)
    for (i in 1 until people.size) {
        val key = people[i]
        var j = i
        while (j > 0 && people[j - 1] > key) {
            people[j] = people[j - 1]
            j -= 1
        }
        people[j] = key
    }
    println(people)
    // average case O(n^2)
}