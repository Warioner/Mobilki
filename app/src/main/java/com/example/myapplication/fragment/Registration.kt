package com.example.myapplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.user

class Registration : Fragment() {

    private var selectedDay = 0
    private var selectedMonth = 0
    private var selectedYear = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val baton = view.findViewById<Button>(R.id.buttonRegistration)
        baton.setOnClickListener {
            pressButton(view)
        }

        val calendarViewCv = view.findViewById<CalendarView>(R.id.calendarView)

        val initialCal = java.util.Calendar.getInstance()
        selectedYear = initialCal.get(java.util.Calendar.YEAR)
        selectedMonth = initialCal.get(java.util.Calendar.MONTH) + 1
        selectedDay = initialCal.get(java.util.Calendar.DAY_OF_MONTH)

        calendarViewCv.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedYear = year
            selectedMonth = month + 1
            selectedDay = dayOfMonth
        }
    }

    private fun createUser(view: View): user? {
        var flag = false
        val nameEt = view.findViewById<EditText>(R.id.InputNickname)
        val genderRg = view.findViewById<RadioGroup>(R.id.inputGender)
        val courseSp = view.findViewById<Spinner>(R.id.inputCourse)
        val seekBarSb = view.findViewById<SeekBar>(R.id.inputDifficulte)

        val regex = Regex("^[a-zA-Zа-яА-ЯеЁ]+$")
        if (!regex.matches(nameEt.text.toString())) {
            nameEt.error = "Только буквы, без пробелов и цифр!"
            flag = true
        }

        val genderText = when (genderRg.checkedRadioButtonId) {
            R.id.radioFemale -> "Female"
            R.id.radioMale -> "Male"
            R.id.radioOther -> "Another"
            else -> "Не выбран"
        }

//        if(genderText == "Another" || genderText == "Не выбран")
//        {
//            val genderText = view.findViewById<TextView>(R.id.gender)
//            genderText.error = "Нельзя!"
//            flag = true
//        }

        if(flag == true)
        {
            val Linear = view.findViewById<LinearLayout>(R.id.Linear)
            Linear.visibility = View.INVISIBLE;
            return null
        }

            return user(
                name = nameEt.text.toString(),
                course = courseSp.selectedItemPosition + 1,
                difficulty = seekBarSb.progress,
                birthDay = selectedDay,
                birthMonth = selectedMonth,
                birthYear = selectedYear,
                gender = genderText,
                zodiac = Whos_That_Zodiak(selectedDay, selectedMonth)
            )

    }

    private fun outUser(view: View, user: user) {
        val tvResult = view.findViewById<TextView>(R.id.userInfo)
        val ivZodiac = view.findViewById<ImageView>(R.id.image)

        tvResult.text = "Имя: ${user.name}\n" +
                "Курс: ${user.course}\n" +
                "Сложность: ${user.difficulty}\n" +
                "Дата: ${user.birthDay}.${user.birthMonth}.${user.birthYear}\n" +
                "Пол: ${user.gender}\n" +
                "Зодиак: ${user.zodiac}"

        val imageResId = when (user.zodiac) {
            "Овен" -> R.drawable.oven
            "Телец" -> R.drawable.telec
            "Близнецы" -> R.drawable.blizneci
            "Рак" -> R.drawable.rak
            "Лев" -> R.drawable.lev
            "Дева" -> R.drawable.deva
            "Весы" -> R.drawable.vesi
            "Скорпион" -> R.drawable.scorpion
            "Стрелец" -> R.drawable.strelec
            "Козерог" -> R.drawable.kozerog
            "Водолей" -> R.drawable.vodoley
            "Рыбы" -> R.drawable.ribi
            else -> R.drawable.ic_launcher_foreground
        }
        ivZodiac.setImageResource(imageResId)
    }

    private fun pressButton(view: View) {
        val createdUser = createUser(view)
        if(createdUser != null)
        {
            outUser(view, createdUser)
            val Linear = view.findViewById<LinearLayout>(R.id.Linear)
            Linear.visibility = View.VISIBLE;
        }
    }

    private fun Whos_That_Zodiak(day: Int, month: Int): String {
        return when (month) {
            1 -> if (day < 20) "Козерог" else "Водолей"
            2 -> if (day < 19) "Водолей" else "Рыбы"
            3 -> if (day < 21) "Рыбы" else "Овен"
            4 -> if (day < 20) "Овен" else "Телец"
            5 -> if (day < 21) "Телец" else "Близнецы"
            6 -> if (day < 21) "Близнецы" else "Рак"
            7 -> if (day < 23) "Рак" else "Лев"
            8 -> if (day < 23) "Лев" else "Дева"
            9 -> if (day < 23) "Дева" else "Весы"
            10 -> if (day < 23) "Весы" else "Скорпион"
            11 -> if (day < 22) "Скорпион" else "Стрелец"
            12 -> if (day < 22) "Стрелец" else "Козерог"
            else -> ""
        }
    }
}