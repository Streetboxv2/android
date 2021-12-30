package com.zeepos.utilities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.orhanobut.hawk.Hawk
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun ImageView.loadImageUrl(url: String, context: Context) {
    Glide.with(context)
        .load(url)
        .error(R.drawable.no_image)
        .placeholder(R.drawable.no_image)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun showView(view: View) {
    if (view !== null) {
        view.visibility = View.VISIBLE
    }
}


fun hideView(view: View) {
    if (view !== null) {
        view.visibility = View.GONE
    }
}

fun hideKeyboard(view: View, context: Context) {
    val inputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.hideKeyboardNew(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


fun showToastExt(message: String?, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun intentPage(context: Context, classTarget: Class<*>) {
    val intent = Intent(context, classTarget)
    context.startActivity(intent)
}


fun intentPageData(context: Context, classTarget: Class<*>): Intent {
    return Intent(context, classTarget)
}

fun intentPageFlags(context: Context?, classTarget: Class<*>) {
    val intent = Intent(context, classTarget)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context?.startActivity(intent)
}

fun setTextHTML(html: String): Spanned {
    val result: Spanned =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    return result
}

fun initHawk(context: Context) {
    Hawk.init(context)
        .build()
}

fun setRvAdapterVertikal(
    recyclerView: RecyclerView,
    adapterGroupieViewHolder: GroupAdapter<GroupieViewHolder>
) {
    recyclerView.apply {
        adapter = adapterGroupieViewHolder
        layoutManager = LinearLayoutManager(context)
        hasFixedSize()
    }
}

fun setRvAdapterHorizontal(
    recyclerView: RecyclerView,
    adapterGroupieViewHolder: GroupAdapter<GroupieViewHolder>
) {
    recyclerView.apply {
        adapter = adapterGroupieViewHolder
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        hasFixedSize()
    }
}

fun TextView.ConvertDateCreateAt(createAt: String) {
    val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val output = SimpleDateFormat("EEEE, dd-MM-yyyy, HH:mm")

    var d: Date? = null
    try {
        d = input.parse(createAt)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    val formatted: String = output.format(d)
    this.text = formatted
}

fun ConvertDateTimeString(createAt: String): String {
    val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val output = SimpleDateFormat("HH:mm:ss")

    var d: Date? = null
    try {
        d = input.parse(createAt)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return output.format(d)
}