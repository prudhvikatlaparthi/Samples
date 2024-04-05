fun merge(arr: MutableList<Int>, l: Int, m: Int, r: Int) {

    val n1 = m - l + 1
    val n2 = r - m

    val L = IntArray(n1)
    val R = IntArray(n2)

    for (i in 0 until n1) L[i] = arr[l + i]
    for (j in 0 until n2) R[j] = arr[m + 1 + j]

    var i = 0
    var j = 0


    var k = l
    while (i < n1 && j < n2) {
        if (L[i] <= R[j]) {
            arr[k] = L[i]
            i++
        } else {
            arr[k] = R[j]
            j++
        }
        k++
    }
    while (i < n1) {
        arr[k] = L[i]
        i++
        k++
    }
    while (j < n2) {
        arr[k] = R[j]
        j++
        k++
    }
}


fun mergeSort(arr: MutableList<Int>, l: Int, r: Int) {
    if (l < r) {
        val m = l + (r - l) / 2
        mergeSort(arr, l, m)
        mergeSort(arr, m + 1, r)
        merge(arr, l, m, r)
    }
}