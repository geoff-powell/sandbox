package sandbox.simple.compose.ui.components

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sandbox.simple.compose.ui.CalendarTheme
import sandbox.simple.compose.ui.SandboxTheme
import sandbox.simple.compose.util.isToday
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*

private const val DAYS_IN_WEEK = 7
private const val MONTHS_TO_RENDER = 2L

@SuppressLint("NewApi")
@ExperimentalFoundationApi
@Composable
fun Calendar(
    locale: Locale,
    modifier: Modifier = Modifier,
    startDate: LocalDate = LocalDate.now()
) {
    val months = remember {
        (0..MONTHS_TO_RENDER).map { months ->
            startDate.plusMonths(months)
        }
    }
    LazyColumn(modifier = modifier) {
        items(months) { month ->
            Month(month, locale)
        }
    }
}

@SuppressLint("NewApi")
@ExperimentalFoundationApi
@Composable
fun Month(date: LocalDate, locale: Locale) {
    val dayRange = remember {
        (date.with(TemporalAdjusters.firstDayOfMonth()).dayOfMonth..date.with(TemporalAdjusters.lastDayOfMonth()).dayOfMonth).toList()
    }
    val weekField = remember { WeekFields.of(locale) }
    val weekMap = remember {
        mutableMapOf<Int, Array<LocalDate?>>().apply {
            dayRange.asSequence()
                .map { date.withDayOfMonth(it) }
                .forEach { dayDate ->
                    val weekOfMonth = dayDate.get(weekField.weekOfMonth()) - 1
                    val dayOfWeek = dayDate.get(weekField.dayOfWeek()) - 1
                    getOrPut(weekOfMonth) {
                        arrayOfNulls(7)
                    }[dayOfWeek] = dayDate
                }
        }
    }
    val random = Random()
    Column {
        MonthHeader(date, locale)
        DaysOfWeekHeader(locale)
        weekMap.forEach { (_, date) ->
            Column {
                Row(horizontalArrangement = Arrangement.SpaceAround) {
                    val modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                    (date).forEach { date ->
                        date?.let {
                            Day(date = date, hasEvents = random.nextBoolean(), modifier = modifier)
                        } ?: EmptyDay(modifier)
                    }
                }
            }
        }
    }
}

@Composable
fun MonthHeader(date: LocalDate, locale: Locale) {
    val rememberMonthTitle = remember { date.month.getDisplayName(TextStyle.FULL, locale) }
    Text(
        modifier = Modifier.absoluteOffset(x = 16.dp).padding(bottom = 16.dp),
        text = rememberMonthTitle,
        color = CalendarTheme.palette.label,
        style = MaterialTheme.typography.h4
    )
}

@SuppressLint("NewApi")
@Composable
fun DaysOfWeekHeader(locale: Locale) {
    Row(horizontalArrangement = Arrangement.SpaceAround) {
        val headers = remember {
            WeekFields.of(locale).run {
                (0 until DAYS_IN_WEEK).map {
                    DayOfWeek.of((firstDayOfWeek.ordinal + it) % DAYS_IN_WEEK + 1)
                        .getDisplayName(TextStyle.SHORT, locale).toUpperCase(locale)
                }
            }
        }
        headers.forEach {
            Text(
                modifier = Modifier.weight(1f),
                text = it,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.secondary
            )
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun Day(date: LocalDate, hasEvents: Boolean, modifier: Modifier = Modifier) {
    val dayOfMonth = remember { date.dayOfMonth }
    val hide = date.dayOfWeek == DayOfWeek.SUNDAY
    val hasEvents = remember { hasEvents }
    Column(
        modifier = modifier.selectedDateBackground(date, MaterialTheme.colors.primaryVariant, CalendarTheme.palette.icon),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayOfMonth.toString(),
            color = if (hide) MaterialTheme.colors.secondary else MaterialTheme.colors.primary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1,
        )
        if (hasEvents) {
            Box(
                modifier = Modifier
                    .size(2.dp)
                    .background(CalendarTheme.palette.icon, CircleShape)
            )
        }
    }
}

private fun Modifier.selectedDateBackground(date: LocalDate, color: Color, stroke: Color): Modifier {
    return if (date.isToday()) {
        this.padding(8.dp)
            .background(color, CircleShape)
            .border(0.5.dp, stroke, CircleShape)
    } else {
        this
    }
}

@Composable
fun EmptyDay(modifier: Modifier = Modifier) {
    Box(modifier)
}

@ExperimentalFoundationApi
@Preview
@Composable
fun CalendarPreview() {
    SandboxTheme {
        Calendar(locale = Locale.getDefault())
    }
}

@ExperimentalFoundationApi
@Preview(widthDp = 200)
@Composable
fun CalendarSmallPreview() {
    SandboxTheme {
        Calendar(locale = Locale.getDefault())
    }
}
