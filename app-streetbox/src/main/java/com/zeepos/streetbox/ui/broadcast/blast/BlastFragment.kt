package com.zeepos.streetbox.ui.broadcast.blast

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chad.library.adapter.base.listener.InstanceFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.messaging.FirebaseMessaging
import com.orhanobut.hawk.Hawk
import com.zeepos.models.master.User
import com.zeepos.models.response.DataRuleBlast
import com.zeepos.streetbox.R
import com.zeepos.streetbox.adapter.AdapterViewPagerBlast
import com.zeepos.streetbox.ui.broadcast.BroadCastViewEvent
import com.zeepos.streetbox.ui.broadcast.BroadCastViewModel
import com.zeepos.streetbox.ui.broadcast.callaccepted.AcceptedCallHistoryFragment
import com.zeepos.streetbox.ui.broadcast.callcustomer.CustomerCallFragment
import com.zeepos.streetbox.ui.broadcast.history.HistoryAllBlastFragment
import com.zeepos.streetbox.utils.showErrorMessageThrowable
import com.zeepos.ui_base.ui.BaseFragment
import com.zeepos.utilities.*
import kotlinx.android.synthetic.main.fragment_blast.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlastFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlastFragment : BaseFragment<BroadCastViewEvent, BroadCastViewModel>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var countDownTimer: CountDownTimer? = null
    private var countDownTimerAutoBlast: CountDownTimer? = null
    private var timeCountInMilliSeconds = 1 * 60000.toLong()
    private var timeCountInMilliSecondsAuto = 1 * 60000.toLong()
    private var timeRunning: Long = 0
    private var timeRunningAuto: Long = 0

    var trackGps: TrackGps? = null
    var latitude = 0.0
    var longitude = 0.0

    var adapterViewPagerBlast: AdapterViewPagerBlast? = null
    private val titles =
        arrayOf("Accepted Call", "Customer Call", "History")
    var timerCountDown: Int = 0
    var lastTimerCountDonw: String = ""
    var lastTimerCountDownAuto: String = ""
    var dataCooldown: Int? = 0
    var dataCooldownAuto: Int? = 0
    var isActive = false
    var isClickAutoBlast = false
    var isClickSwitch = false
    var dataRuleBlast: DataRuleBlast? = null

    val instanceFragment = object : InstanceFragment {
        override fun instanceFragment(position: Int): Fragment {
            return if (position == 0) {
                AcceptedCallHistoryFragment.newInstance("accept", "")
            } else if (position == 1) {
                CustomerCallFragment.newInstance("request", "")
            } else if (position == 2) HistoryAllBlastFragment.newInstance("", "")
            else throw IllegalArgumentException("")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlastFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun initResourceLayout(): Int {
        return R.layout.fragment_blast
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(BroadCastViewModel::class.java)
    }


    fun suscribesNotif(user: User) {
        println("respon Userv he ${viewModel.getUser()?.id}")
        FirebaseMessaging.getInstance().subscribeToTopic("blast_${viewModel.getUser()?.id}")
            .addOnSuccessListener {
            }.addOnFailureListener {
            }
    }

    private fun initCurrentLocation() {
        trackGps = TrackGps(requireContext())
        if (trackGps!!.canGetLocation()) {
            println("canGetLocation")
            latitude = trackGps!!.latitude
            longitude = trackGps!!.longitude

            updateLocation()
        }
    }

    private fun updateLocation() {
        val map = mutableMapOf(
            "latitude" to latitude,
            "longitude" to longitude
        )
        viewModel.callUpdateLoc(map)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        showLoading()
        initial()
    }

    override fun onResume() {
        super.onResume()
        showLoading()
        initial()
    }


    private fun initial() {
        initCurrentLocation()
        setTabViewPager()

        imgRippleLoader.setOnClickListener {
            isClickAutoBlast = false
            Hawk.put("isClickSwitch", isClickAutoBlast)
            hitApiBlastNotif()
        }

        val user = viewModel.getUser()
        user?.let {
            suscribesNotif(it)
        }

        initSwitchAutoBlast()

    }


    override fun onEvent(useCase: BroadCastViewEvent) {
        when (useCase) {
            is BroadCastViewEvent.OnSuccessTimerGetBlast -> {
                val dataItem = useCase.responseRuleBlast.data
                validationTimer(dataItem)
            }
            is BroadCastViewEvent.OnSuccessNotifBlastManual -> {
                dismissLoading()
                val dataItem = useCase.jsonObject

                viewModel.callGetTimerBlast()
            }
            is BroadCastViewEvent.OnSuccessReqAutoBlast -> {
                viewModel.callGetTimerBlast()
            }

            is BroadCastViewEvent.OnFailed -> {
                dismissLoading()
//                println("respon thro blast ${useCase.throwable.message}")

            }

            is BroadCastViewEvent.OnFailedBlastNotif -> {
                dismissLoading()
                val throwable = useCase.throwable
                showToastExt("Mohon segera proses customer call",requireContext())
            }

            is BroadCastViewEvent.OnSuccessUpdateLoc -> {
                viewModel.callGetTimerBlast()
            }
        }
    }


    fun startCountDownAuto(dataItem: DataRuleBlast) {
        dataRuleBlast = dataItem
        lastTimerCountDownAuto = ConvertDateTimeString(dataItem.lastAutoBlast.toString())
        val calendar = Calendar.getInstance()
        val formatTime = SimpleDateFormat("HH:mm:ss")

        val getLastDate = formatTime.parse(lastTimerCountDownAuto)
        calendar.time = getLastDate
        calendar.add(Calendar.MINUTE, dataItem.interval!!)

        //result lastblast + cooldown
        val resultLastBlastTime = formatTime.format(calendar.time)

        val currentTime: String =
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        val lastBlastParse = formatTime.parse(resultLastBlastTime)
        val currentParse = formatTime.parse(currentTime)

        var testTotal = lastBlastParse.time - currentParse.time
        val limit = dataItem.interval!! * 60000.toLong()
        if (testTotal > limit || testTotal < 0) {
            testTotal = limit
        }
        val formatTotal = formatTime.format(testTotal)
        println("respon Time auto $testTotal and $formatTotal and current time $currentParse")

        if (dataItem.isAutoBlast!!) {
            println("respon start")
            timeCountInMilliSecondsAuto = testTotal
            startCountDownTimerAutoBlast()
        } else {
            countDownTimerAutoBlast?.cancel()
            println("respon Pause")
        }

    }


    private fun validationTimer(dataItem: DataRuleBlast?) {
        dismissLoading()
        dataRuleBlast = dataItem
        dataCooldown = dataItem?.cooldown!!.toInt()
        dataCooldownAuto = dataItem.interval
        isActive = dataItem.isActive!!

        val isClickSave = Hawk.get<Boolean>("isClickSwitch")
        if (isClickSave != null) {
            isClickAutoBlast = isClickSave
            switchAutoBlast.isChecked = isClickSave
        }

        switchAutoBlast.isChecked = dataItem.isAutoBlast!!
        isClickSwitch = dataItem.isAutoBlast!!
        isClickAutoBlast = dataItem.isAutoBlast!!

        println("respon Click blast $isClickAutoBlast")

        startCountDownAuto(dataItem)
        startManualBlast(dataItem)

    }

    private fun startManualBlast(dataItem: DataRuleBlast) {
        if (!isActive) {
            showView(tvKetNotData)
//            hideView(rlMultipleLoader)
            if (rlMultipleLoader != null) {
                rlMultipleLoader.visibility = View.INVISIBLE
            }
            hideView(switchAutoBlast)
        } else {
            showView(rlMultipleLoader)
            showView(multipleLoader)
            showView(switchAutoBlast)

            if (dataItem.lastManualBlast?.isNotEmpty()!!) {
                lastTimerCountDonw = ConvertDateTimeString(dataItem.lastManualBlast.toString())
                val calendar = Calendar.getInstance()
                val formatTime = SimpleDateFormat("HH:mm:ss")

                val getLastDate = formatTime.parse(lastTimerCountDonw)
                calendar.time = getLastDate
                calendar.add(Calendar.MINUTE, dataCooldown!!)

                //result lastblast + cooldown
                val resultLastBlastTime = formatTime.format(calendar.time)

                val currentTime: String =
                    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                val lastBlastParse = formatTime.parse(resultLastBlastTime)
                val currentParse = formatTime.parse(currentTime)

                val testTotal = lastBlastParse.time - currentParse.time
                val formatTotal = formatTime.format(testTotal)
                println("respon Time manual $testTotal and $formatTotal and current time $currentParse")

                val calendarTimeCoolDown = Calendar.getInstance()
                calendarTimeCoolDown.set(Calendar.MINUTE, dataCooldown!!)
                calendarTimeCoolDown.set(Calendar.SECOND, 0)


                val getCalendarCoolDown = calendarTimeCoolDown.time
                val formatCalendar = formatTime.format(getCalendarCoolDown)
                val parseGetCoolDown = formatTime.parse(formatCalendar)
                val timeFormatTotal = formatTime.parse(formatTotal)
                println(
                    "respon Calendar manual $getCalendarCoolDown and $parseGetCoolDown" +
                            " and current time $timeFormatTotal"
                )

                if (!isClickSwitch) {
                    countDownTimerAutoBlast?.cancel()
                    timeCountInMilliSeconds = 0
                    println("respon puaseee ")
                }

                if (timeFormatTotal.time > parseGetCoolDown.time) {
                    tvTimerBlast.text = "Blast Now"
                } else {
                    showView(rlMultipleLoader)
                    showView(multipleLoader)
                    hideView(imgRippleLoader)
                    timeCountInMilliSeconds = testTotal
                    startCountDownTimer()
                }
            } else {
                timerCountDown = dataItem.cooldown!!
                tvTimerBlast.text = "Blast Now"
            }
        }
    }


    fun initSwitchAutoBlast() {
        switchAutoBlast.setOnCheckedChangeListener { buttonView, isChecked ->
            isClickSwitch = isChecked
        }

        switchAutoBlast.setOnClickListener {
            if (isClickSwitch) {
                isClickAutoBlast = true
                Hawk.put("isClickSwitch", isClickAutoBlast)
                hitApiAutoBlastNotif()
                println("respon Switch if true")
            } else {
                isClickAutoBlast = false
                Hawk.put("isClickSwitch", isClickAutoBlast)
                hitApiAutoBlastNotif()
                countDownTimerAutoBlast?.cancel()
                println("respon Switch else true")
            }
        }
    }


    private fun setTabViewPager() {
        adapterViewPagerBlast = AdapterViewPagerBlast(titles, this, instanceFragment)
        viewPager2Blast.adapter = adapterViewPagerBlast
        TabLayoutMediator(
            tabLayoutBlast, viewPager2Blast
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = titles[position]
        }.attach()
    }


    private fun startCountDownTimer() {
//        showView(multipleLoader)
//        hideView(imgRippleLoader)
        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRunning = millisUntilFinished
                tvTimerBlast?.text = hmsTimeFormatter(millisUntilFinished)
                progressBarCircle?.progress = (millisUntilFinished / 1000).toInt()
                println("respon Test countd")
            }

            override fun onFinish() {
                timerCountDown = dataCooldown!!
                tvTimerBlast?.text = "Blast Now"
                if (multipleLoader != null) {
                    multipleLoader.visibility = View.INVISIBLE
                }
                showView(rlMultipleLoader)
                showView(imgRippleLoader)
            }
        }
        countDownTimer?.start()
    }

    private fun startCountDownTimerAutoBlast() {
        countDownTimerAutoBlast = object : CountDownTimer(timeCountInMilliSecondsAuto, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (dataRuleBlast?.isAutoBlast!!) {
                    timeRunningAuto = millisUntilFinished
                    tvTimerAutoBlast?.text = hmsTimeFormatter(millisUntilFinished)
                    if (progressBarCircle != null) {
                        progressBarCircle.progress = (millisUntilFinished / 1000).toInt()
                    }
                }
                println("respon Test countd auto")
            }

            override fun onFinish() {
                println("respon Finish auto")
                if (dataRuleBlast != null) {
                    startCountDownAuto(dataRuleBlast!!)
                } else {
                    isClickAutoBlast = false
                    Hawk.put("isClickSwitch", isClickAutoBlast)
                    switchAutoBlast.isChecked = isClickAutoBlast
                    tvTimerAutoBlast?.text = hmsTimeFormatter(timeRunningAuto)
                }
            }


        }
        countDownTimerAutoBlast?.start()
    }

    private fun hmsTimeFormatter(milliSeconds: Long): String? {
        return java.lang.String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(milliSeconds),
            TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    milliSeconds
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    milliSeconds
                )
            )
        )
    }


    private fun hitApiBlastNotif() {
        showLoading()
        viewModel.callNotifBlastManual()
    }

    private fun hitApiAutoBlastNotif() {
        showLoading()
        viewModel.callReqAutoBlastToggle()
    }

}
