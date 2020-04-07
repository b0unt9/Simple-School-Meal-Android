package com.prigic.simpleschoolmeal

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.prigic.simpleschoolmeal.school.School
import com.prigic.simpleschoolmeal.school.SchoolMenu
import kotlinx.android.synthetic.main.navigation_meal.*
import java.text.SimpleDateFormat
import java.util.*


class FragmentMeal : Fragment() {

    var mealLoadDay: Int? = null
    var mealLoadYear: Int? = null
    var mealLoadMonth: Int? = null

    lateinit var next_day: ImageButton
    lateinit var pre_day: ImageButton
    lateinit var info: TextView

    var permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            Utils.toast(context, "급식 정보가 저장됩니다.")
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) { //거절
            Utils.toast(
                context,
                "급식 정보를 저장하기 위해서 권한이 필요합니다.\n설정애서 권한을 부여하십시오."
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.navigation_meal, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        info = (context as MainActivity).info!!
        pre_day = (context as MainActivity).pre_day!!
        next_day = (context as MainActivity).next_day!!

        var dateSetListener: DatePickerDialog.OnDateSetListener? = null
        dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            info.text = year.toString() + "-" + (month + 1).toString() + "-" + day.toString()
            mealLoadMonth = month + 1
            mealLoadDay = day
            mealLoadYear = year
            MealTask().execute()
        }

        pre_day.visibility = View.VISIBLE
        next_day.visibility = View.VISIBLE

        mealLoadMonth = getTime("MM").toInt()
        mealLoadDay = getTime("dd").toInt()
        mealLoadYear = getTime("yyyy").toInt()

        info.text = "$mealLoadYear-$mealLoadMonth-$mealLoadDay"

        MealTask().execute()

        info.setOnClickListener {
            val year = info.text.split("-")[0].toString().toInt()
            val month = info.text.split("-")[1].toString().toInt() - 1
            val day = info.text.split("-")[2].toString().toInt()

            val dialog = DatePickerDialog(context!!, dateSetListener, year, month, day)
            dialog.window.attributes.windowAnimations = R.style.DialogAnimation
            dialog.show()
        }

        next_day.setOnClickListener {
            var year = info.text.split("-")[0].toString().toInt()
            var month = info.text.split("-")[1].toString().toInt()
            var day = info.text.split("-")[2].toString().toInt()
            var lastday = (AllDay(year, month + 1, day) - AllDay(year, month, day)).toInt()

            if (day == lastday) {
                if (month == 12) {
                    year = year + 1
                    month = 1
                    day = 1
                } else {
                    month = month + 1
                    day = 1
                }
            } else {
                day = day + 1
            }

            mealLoadYear = year
            mealLoadMonth = month
            mealLoadDay = day

            info.text = "$mealLoadYear-$mealLoadMonth-$mealLoadDay"

            MealTask().execute()
        }

        pre_day.setOnClickListener {
            var year = info.text.split("-")[0].toString().toInt()
            var month = info.text.split("-")[1].toString().toInt()
            var day = info.text.split("-")[2].toString().toInt()

            if (day == 1) {
                if (month == 1) {
                    month = 12
                    year = year - 1
                } else {
                    month = month - 1
                }

                day = 1

                var lastday = (AllDay(year, month + 1, day) - AllDay(year, month, day)).toInt()

                mealLoadYear = year
                mealLoadMonth = month
                mealLoadDay = lastday

                info.text = "$mealLoadYear-$mealLoadMonth-$mealLoadDay"

                MealTask().execute()
            } else {
                day = day - 1

                mealLoadYear = year
                mealLoadMonth = month
                mealLoadDay = day

                info.text = "$mealLoadYear-$mealLoadMonth-$mealLoadDay"

                MealTask().execute()
            }
        }

        meal.movementMethod = ScrollingMovementMethod()
        meal.setOnTouchListener(object : OnSwipeTouchListener(context!!) {
            override fun onSwipeLeftToRight() {
                pre_day.performClick()
            }

            override fun onSwipeRightToLeft() {
                next_day.performClick()
            }
        })

        swipe.setColorSchemeResources(
            R.color.colorAccent,
            R.color.colorPrimary,
            R.color.colorPrimaryDark,
            R.color.colorPrimary
        )

        swipe.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            swipe.isRefreshing = false
            Utils.delete("Meal Data/$mealLoadYear-$mealLoadMonth.data")
            MealTask().execute()
        })

        var calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 7)
        calendar.set(Calendar.MINUTE, 30)
        calendar.set(Calendar.SECOND, 0)

        var alarmIntent = Intent("MealAlarmServiceListener")
            .putExtra("mealLoadYear", mealLoadYear!!)
            .putExtra("mealLoadMonth", mealLoadMonth!!)
            .putExtra("mealLoadDay", mealLoadDay!!)
        var pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        var alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun getTime(type: String): String {
        val sdf = SimpleDateFormat(type)
        val time = sdf.format(Date(System.currentTimeMillis()))
        return time
    }

    private inner class MealTask : AsyncTask<Void?, Void?, Void?>() {

        val permissionCheck = ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var textMenu: String? = null
        var menu: List<SchoolMenu>? = null
        var dialog: SweetAlertDialog? = null
        var content: String = "";
        var isText: Boolean = false

        override fun onPreExecute() {
            dialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
            dialog!!.progressHelper.barColor = Color.parseColor("#64b5f6")
            dialog!!.titleText = "\n\n급식 불러오는 중..."
            dialog!!.setCancelable(false)
            if (permissionCheck == PackageManager.PERMISSION_DENIED
                || (permissionCheck == PackageManager.PERMISSION_GRANTED &&
                        Utils.read("$mealLoadYear-$mealLoadMonth.data", "null").equals("null"))
            ) {
                dialog!!.show()
            }
        }

        override fun doInBackground(vararg params: Void?): Void? {
            if (permissionCheck == PackageManager.PERMISSION_GRANTED
                && !Utils.read("$mealLoadYear-$mealLoadMonth.data", "null").equals("null")
            ) {
                textMenu = Utils.read("$mealLoadYear-$mealLoadMonth.data", "파일 오류!")
                isText = true
                return null
            } else {
                menu = School(
                    School.Type.HIGH,
                    School.Region.SEOUL,
                    "B100000456"
                ).getMonthlyMenu(
                    mealLoadYear!!,
                    mealLoadMonth!!
                )
                return null
            }
        }

        override fun onPostExecute(result: Void?) {
            if (!isText) {
                for (i in 0 until menu!!.size) {
                    var str: String =
                        "<" + mealLoadYear.toString() + "-" + mealLoadMonth.toString() + "-" + (i + 1).toString() + ">" + menu!![i].toString()
                    if (!str.contains("중식")) str =
                        "<" + mealLoadYear.toString() + "-" + mealLoadMonth.toString() + "-" + (i + 1).toString() + ">" + "\n\n" + "급식이 없거나,\n본교에서 나이스에 급식 정보를 공시하지 않았습니다."
                    content += "\n\n" + str.replaceFirst("\n", "")
                }
            } else content = textMenu!!

            content.replaceFirst("\n\n", "")

            var show_content =
                content.split("<" + mealLoadYear.toString() + "-" + mealLoadMonth.toString() + "-" + mealLoadDay.toString() + ">")[1]

            if (show_content.contains("중식")) {
                if (getCharNumber(show_content, "<") > 1) {
                    show_content = show_content.split("<")[0]
                }
            } else show_content = show_content.split(".")[0]

            meal.text = show_content

            if (dialog!!.isShowing) dialog!!.cancel()

            when (permissionCheck) {
                PackageManager.PERMISSION_GRANTED -> {
                    if (Utils.read("$mealLoadYear-$mealLoadMonth.data", "null").equals("null")) {
                        Utils.createFolder("Meal Data");
                        Utils.save("$mealLoadYear-$mealLoadMonth.data", content)
                        Utils.toast(context, "저장되었습니다.");
                    }
                }
                else -> {
                    TedPermission.with(context)
                        .setPermissionListener(permissionlistener)
                        .setRationaleTitle("권한 필요")
                        .setRationaleMessage("급식 데이터를 저장하기 위해서는 권한이 필요합니다.")
                        .setDeniedMessage("권한이 없어 매번 급식정보를 인터넷으로 부터 불러옵니다. \n앱 설정을 통하여 권한을 부여하실 수 있습니다.")
                        .setPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        .check();
                }
            }

        }

    };

    fun AllDay(y: Int, m: Int, d: Int): Double {
        var month = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        if (y % 4 == 0) {
            month = arrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        }
        var amoon = 0
        for (i in 1 until m) {
            amoon += month.get(i - 1)
        }
        var result = (Math.floor(365.24253716252537 * y) + amoon + d) - 366;
        return result
    }


    fun getCharNumber(str: String, equ: String): Int {
        var count = 0
        for (i in 0 until str.length) {
            if (str[i].toString() == equ)
                count++
        }
        return count
    }

}