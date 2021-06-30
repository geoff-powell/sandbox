package sandbox.calendar.compose.ui.components

import org.junit.Test
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

class CalendarKtTest {

    private val DAYS_IN_WEEK = 7

    @Test
    fun test() {
        println("" + WeekFields.of(Locale.US).run {
            (0 until DAYS_IN_WEEK).map {
                DayOfWeek.of((firstDayOfWeek.ordinal + it) % DAYS_IN_WEEK + 1)
                    .getDisplayName(TextStyle.SHORT, Locale.US)
            }
        })
    }
}