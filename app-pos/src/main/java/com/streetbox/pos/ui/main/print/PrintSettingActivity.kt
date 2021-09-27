package com.streetbox.pos.ui.main.print

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.streetbox.pos.R
import com.streetbox.pos.ui.main.onlineorder.OnlineOrderActivity
import com.zeepos.ui_base.ui.BaseActivity
import kotlinx.android.synthetic.main.printsetting.*

class PrintSettingActivity : BaseActivity<PrintSettingViewEvent, PrintSettingViewModel>() {

    private var doubleBackToExitPressedOnce = false
    private var printing: Printing? = null

    override fun initResourceLayout(): Int {
        return R.layout.printsetting
    }

    override fun init() {
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        initViews()
        initListeners()
        viewModel = ViewModelProvider(this, viewModeFactory).get(PrintSettingViewModel::class.java)

    }

    private fun initViews() {
//        btnPiarUnpair.text =
//            if (Printooth.hasPairedPrinter()) "Un-pair ${Printooth.getPairedPrinter()?.name}" else "Pair with printer"
    }

    fun initListeners() {
        btnPrint.setOnClickListener {
            if (!Printooth.hasPairedPrinter()) startActivityForResult(
                Intent(
                    this,
                    ScanningActivity::class.java
                ),
                ScanningActivity.SCANNING_FOR_PRINTER
            )
            else printSomePrintable()
        }

        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(
                    this@PrintSettingActivity,
                    "Connecting with printer",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(
                    this@PrintSettingActivity,
                    "Order sent to printer",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(
                    this@PrintSettingActivity,
                    "Failed to connect printer",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@PrintSettingActivity, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@PrintSettingActivity, "Message: $message", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }


    private fun printSomePrintable() {
        val printables = getSomePrintables()
        printing?.print(printables)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK)
            printSomePrintable()
        initViews()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {

    }

    private fun getSomePrintables() = ArrayList<Printable>().apply {
        add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build()) // feed lines example in raw mode

        add(
            TextPrintable.Builder()
                .setText(" Hello World : été è à '€' içi Bò Xào Coi Xanh")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Hello World : été è à €")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Hello World")
                .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Hello World")
                .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                .setNewLinesAfter(1)
                .build()
        )


    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onEvent(useCase: PrintSettingViewEvent) {
        TODO("Not yet implemented")
    }

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, PrintSettingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

}

