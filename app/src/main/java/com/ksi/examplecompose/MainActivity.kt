package com.ksi.examplecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ksi.examplecompose.ui.theme.ExampleComposeTheme
import com.ksi.examplecompose.ui.theme.defaultPadding
import java.util.Collections.list

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExampleComposeTheme {
                // A surface container using the 'background' color from the theme
                //hoistable state remove dublicate state

                Surface(color = MaterialTheme.colors.background) {
                    MAnimateContentSize()
                }
            }
        }
    }
}

@Composable
fun AnimateColor() {
    var isGray by remember { mutableStateOf(false) }
    val animateColor = animateColorAsState(
        targetValue = if (isGray) Color.Gray else Color.Green,
        animationSpec = tween(durationMillis = 3000)
    )
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Button(onClick = { isGray = !isGray }) {
            Text(text = "Animate color")
        }
        Box(
            Modifier
                .size(400.dp)
                .background(
                    animateColor.value
                )
        ) {

        }

    }

}
@ExperimentalAnimationApi
@Composable
fun MAnimateVisibilty() {
    var isVisible by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Button(onClick = { isVisible = !isVisible }) {
            Text(text = "Animate color")
        }
        AnimatedVisibility(visible = isVisible) {
            Box(
                Modifier
                    .size(400.dp)
                    .background(
                        color = Color.Green
                    )
            ) {

            }
        }



    }

}

/*fun MAnimateVisibiltyMoreOption() {
    var isVisible by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Button(onClick = { isVisible = !isVisible }) {
            Text(text = "Animate color")
        }
        AnimatedVisibility(visible = isVisible
        , enter = expandIn(tween(durationMillis = 3000)),
            exit = shrinkOut(tween(durationMillis = 300))
        ) {
            Box(
                Modifier
                    .size(400.dp)
                    .background(
                        color = Color.Green
                    )
            ) {

            }
        }



    }

}*/
@Composable
fun MAnimateContentSize() {
    var isShort by remember { mutableStateOf(true) }
    val short="Iam hello Iam hello Iam hello Iam hello Iam hello Iam hello"
    val longtxt="Iam hello Iam hello Iam hello Iam hello Iam hello Iam hello Iam hello Iam hello Iam hello Iam hello Iam hello Iam hello Iam hello Iam hello Iam hello Iam hello Iam hello Iam hello"
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Button(onClick = { isShort = !isShort }) {
            Text(text = "Animate color")
        }

            Box(
                Modifier
                  //  .size(400.dp)
                    .padding(20.dp)
                    .animateContentSize (animationSpec = tween(durationMillis = 300) )
                    .background(
                        color = Color.Green
                    )
            ) {
                Text(text = if(isShort) short else longtxt)

        }



    }

}

@Composable
fun MyApp() {
    //remeberSeverabale save value even configer of app changed
    var onBoard by remember {
        mutableStateOf(true)
    }
    if (onBoard) {
        OnboardingScreen({ onBoard = false })
    } else {
        Greetings()
    }

}

@Composable
fun Greetings(names: List<String> = List(1000) { "$it" }) {
    Surface(color = MaterialTheme.colors.background) {
        Column(modifier = Modifier.padding(defaultPadding)) {
            LazyColumn {
                items(names) { name ->
                    Greeting(name = name)

                }

            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    //remember to save last value when recompose happened
    //mutablestate will recompose element depond on this value
    //by to delegate
    var expand by remember { mutableStateOf(false) }


    val expandValue = if (expand) 34.dp else 0.dp
    Surface(
        color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(defaultPadding)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.1f)
                    .padding(bottom = expandValue)
            ) {
                Text(text = "Hello ")
                Text(text = "$name!")
            }
            OutlinedButton(onClick = { expand = !expand }) {
                Text(if (expand) stringResource(R.string.cc) else "Show More")
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ExampleComposeTheme {
        // MyApp()
        OnboardingScreen({})
    }
}

@Composable
fun OnboardingScreen(onContenueClicked: () -> Unit) {
    // TODO: This state should be hoisted
    // var shouldShowOnboarding by remember { mutableStateOf(true) }

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("Welcome to the Basics Codelab!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContenueClicked
            ) {
                Text("Continue")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    ExampleComposeTheme {
        OnboardingScreen({})
    }
}