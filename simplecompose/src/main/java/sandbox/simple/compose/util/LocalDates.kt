package sandbox.simple.compose.util

import java.time.LocalDate

fun LocalDate.isToday(): Boolean = isEqual(LocalDate.now())