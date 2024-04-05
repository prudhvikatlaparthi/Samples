package com.pru.printlib.data.printable

import com.pru.printlib.data.printer.Printer


interface Printable {
    fun getPrintableByteArray(printer: Printer): List<ByteArray>
}
