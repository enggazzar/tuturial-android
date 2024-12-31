package com.tohamy.tutorial.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tohamy.tutorial.android.ui.theme.TutorialAndroidTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
references
https://www.youtube.com/watch?v=ShNhJ3wMpvQ&list=PLQkwcJG4YTCQcFEPuYGuv54nYai_lwil_
 */
/*
In Kotlin, coroutines are a lightweight way to perform asynchronous and concurrent programming.
==========Coroutine Scope===========
 1-GlobalScope
 2- CoroutineScope
 3- viewModelScope
 viewModelScope.launch {
            // Coroutine will cancel when ViewModel is cleared
        }
4. lifecycleScope
 A scope for coroutines tied to the lifecycle of a LifecycleOwner (e.g., Activity, Fragment).
  lifecycleScope.launch {
            // Coroutine will cancel when lifecycle is destroyed
        }
  5. repeatOnLifecycle
  Runs a block of code repeatedly when the LifecycleOwner reaches a specific lifecycle state.
  lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        // Runs only when the lifecycle is in STARTED state
    }
}
6. LifecycleOwner.lifecycleScope
Similar to lifecycleScope, but scoped specifically to a LifecycleOwner
myFragment.lifecycleScope.launch {
    // Coroutine scoped to Fragment lifecycle
}
8. SupervisorScope
Description: A scope that ensures child coroutines are independent of each other's failure.
Use Case: When you want sibling coroutines to continue running even if one fails.
supervisorScope {
    launch { task1() } // Will not affect the other coroutines
    launch { task2() }
}
What distinguishes Coroutines from Threads?
1-Executed within a thread
2-Coroutines are suspendable
3-They can switch their context
==================
sleep stop thread but delay will stop the current coroutine
if main thread finish his work main all coroutine will be canceled
======suspend function===========
1- executed with another suspend function or coroutine

 */

class MainActivity : ComponentActivity() {

  suspend fun networkCall1(): String {
    delay(3000)
    return "call 1"
  }

  suspend fun networkCall2(): String {
    delay(3000)
    return "call 2"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    //region loge will print after tow suspend function finish because executed in same coroutine
    //https://www.youtube.com/watch?v=yc_WfBp-PdE&list=PLQkwcJG4YTCQcFEPuYGuv54nYai_lwil_&index=3
    GlobalScope.launch {
      val network1 = networkCall1()
      val network2 = networkCall2()
      Log.e("Tag",network1)
      Log.e("Tag",network2)
    }
    //endregion
    //region Coroutine Contexts (Dispatcher)
    /*
    https://www.youtube.com/watch?v=71NrkkRNXG4&list=PLQkwcJG4YTCQcFEPuYGuv54nYai_lwil_&index=4
    Dispatchers
    1- Dispatchers.Main will be in main thread (we can update ui only from main thread)
    2-Dispatchers.IO data operation like (network call-db-read write files)
    3-Dispatchers.Default long running operation
    4-Dispatchers.Unconfined light used for simple test
    ==============
    switch between dispatcher
     withContext(Dispatchers.Main){
         //here can update ui
       }
       ===========
     */
     GlobalScope.launch(Dispatchers.IO) {
       val network1 = networkCall1()
       withContext(Dispatchers.Main){
         //here can update ui
       }
     }

    //endregion

    setContent {
      TutorialAndroidTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(
    text = "Hello $name!",
    modifier = modifier
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  TutorialAndroidTheme {
    Greeting("Android")
  }
}