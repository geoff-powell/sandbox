package sandbox.calendar.compose.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


interface Router {
    fun push(name: String)
    fun pop()
}

private class RouterImpl(
    start: String
) : Router {
    private val routeStack = mutableStateListOf(start)

    val currentRoute: String
        get() = routeStack.last()

    val isAtRoot: Boolean
        get() = routeStack.size == 1

    override fun push(name: String) {
        routeStack += name
    }

    override fun pop() {
        if (!isAtRoot) routeStack.removeLast()
    }
}

interface RouteBuilder {
    fun route(name: String, content: @Composable (Router) -> Unit)
}

private class Routes : RouteBuilder {
    val routeMap = mutableStateMapOf<String, @Composable (Router) -> Unit>()

    override fun route(name: String, content: @Composable (Router) -> Unit) {
        routeMap[name] = content
    }
}

@Composable
fun Router(
    start: () -> String,
    unavailableRoute: @Composable (String, Router) -> Unit = { route, _ ->
        BasicText(
            "Route not available: $route",
            Modifier.background(Color.Red),
            style = TextStyle(color = Color.Yellow)
        )
    },
    buildRoutes: RouteBuilder.() -> Unit
) {
    val currentBuilder by rememberUpdatedState(buildRoutes)
    val routes by remember { derivedStateOf { Routes().apply(currentBuilder) } }
    val router = remember { RouterImpl(start()) }
    val currentRoute = router.currentRoute

    Crossfade(currentRoute) {
        routes.routeMap[it]?.invoke(router) ?: unavailableRoute(it, router)
    }

    BackHandler(enabled = !router.isAtRoot) {
        router.pop()
    }
}

@Preview
@Composable
fun RouterPreview() {
    var counter by remember { mutableStateOf(0) }
    val items = remember { mutableStateListOf<String>() }
    Router({"home"}) {
        route("home") { router ->
            Column {
                Button(onClick = { router.push("details") }) {
                    Text("See details")
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = { router.push("bogus") }) {
                    Text("Bogus route")
                }
                Spacer(Modifier.height(16.dp))
                items.forEach { item ->
                    Button(onClick = { router.push(item) }) {
                        Text("See $item")
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = { items += "item ${++counter}" }) {
                    Text("Add another item")
                }
            }
        }
        route("details") {
            Button(onClick = it::pop) {
                Text("Go back")
            }
        }
        items.forEach { item ->
            route(item) { router ->
                Column {
                    Text("Looking at $item")
                    Button(onClick = router::pop) {
                        Text("Go back")
                    }
                }
            }
        }
    }
}