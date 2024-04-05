fun quickSort(arr: MutableList<Int>, low: Int, high: Int) {
    if (low < high) {
        val pi: Int = partition(arr, low, high)
        quickSort(arr, low, pi - 1)
        quickSort(arr, pi + 1, high)
    }
}

fun swap(arr: MutableList<Int>, i: Int, j: Int) {
    val temp = arr[i]
    arr[i] = arr[j]
    arr[j] = temp
}

fun partition(arr: MutableList<Int>, low: Int, high: Int): Int {
    val pivot = arr[high]
    var i = low - 1
    for (j in low until high) {
        if (arr[j] < pivot) {
            i++
            swap(arr, i, j)
        }
    }
    swap(arr, i + 1, high)
    return i + 1
}

fun main() {
    val people = mutableListOf<Int>(
        5, 8, 6, 4, 2, 8, 1
    )
    println(people)
    quickSort(arr = people, 0, people.size - 1)
    println(people)
}