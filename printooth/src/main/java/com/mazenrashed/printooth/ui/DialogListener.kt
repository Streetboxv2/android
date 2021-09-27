package com.mazenrashed.printooth.ui

import com.mazenrashed.printooth.data.PairedPrinter

/**
 * Created by Arif S. on 2020-03-08
 */
interface DialogListener {
    fun onDeviceSelected(pairedPrinter: PairedPrinter)
    fun onDismiss()
}