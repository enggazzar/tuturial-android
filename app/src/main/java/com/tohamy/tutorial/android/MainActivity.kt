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
import androidx.lifecycle.lifecycleScope
import com.tohamy.tutorial.android.ui.MyApp
import com.tohamy.tutorial.android.ui.theme.TutorialAndroidTheme
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlin.time.measureTime



class MainActivity : ComponentActivity() {

/*
reference
https://www.youtube.com/watch?v=cr5xLjPC4-0&t=932s
 */

  //region 1-Sequential Execution
  /*
   1-Sequential Execution: The getUserFirstNames function processes each userId one by one. Since getFirstName involves a delay(1000L), the total time to fetch all names grows linearly with the number of userIds.
    */

  suspend fun getFirstName(userId: String): String {
    delay(1000L)
    return "first name"
  }
  //mistake
  suspend fun getUserFirstNames(userIds: List<String>): List<String> {
    val firstNames = mutableListOf<String>()
    for (id in userIds) {
      firstNames.add(getFirstName(id))
    }
    return firstNames
  }
  //fix Each getFirstName call runs concurrently using async
  suspend fun getUserFirstNamesAsync(userIds: List<String>): List<String> = coroutineScope {
    userIds.map { userId ->
      async { getFirstName(userId) }
    }.map { it.await() }
  }
  //endregion
  //region 2-Inefficient Cancellation
  /*
  The job.cancel() will stop the coroutine, but the loop does not have explicit checks to handle
 cancellation, which could lead to unexpected behavior
   */
  //mistake
  suspend fun doSomething() {
    val job = CoroutineScope(Dispatchers.Default).launch {
      var random = Random.nextInt(until = 100_000)
      while (random != 50_000) {
        random = Random.nextInt(until = 100_000)
      }
    }
    delay(500L)
    job.cancel()
  }
  //fix The isActive check ensures the coroutine can stop execution gracefully when canceled
  suspend fun doSomethingFix() {
    val job = CoroutineScope(Dispatchers.Default).launch {
      var random: Int
      //check isActive
      while (isActive) {
        random = Random.nextInt(until = 100_000)
        if (random == 50_000) break
      }
    }

    delay(500L) // Wait for 500 ms
    job.cancelAndJoin() // Cancel the job and wait for its completion
  }

  //endregion
  //region 3- a coroutine is not main-safe, it typically means that the coroutine is executing code that might block or delay the main thread
  /*
  1-Dispatchers.Main is the default for suspend function so will run in main ui thread
  2- Popular libraries like Room and Retrofit are designed to handle threading efficiently, meaning you don’t need to use withContext(Dispatchers.IO) manually for their operations. These libraries are main-safe by default
   */
  suspend fun doNetworkCall(): Result<String> {
    val result = networkCall()
    return if (result == "Success") {
      Result.success(result)
    } else {
      Result.failure(Exception("Network call failed"))
    }
  }
 //will execute in main dispatcher
  suspend fun networkCall(): String {
    delay(3000L) // Simulates network latency
    return if (Random.nextBoolean()) "Success" else "Error"
  }
  suspend fun networkCallFix(): String {
    return  withContext(Dispatchers.IO){
      delay(3000L) // Simulates network latency
      if (Random.nextBoolean()) "Success" else "Error"
    }

  }
  //endregion
  //region 4-Parent-Child  CancellationException in Coroutines
  /*
  In a coroutine context, when a child coroutine fails with an unhandled exception, it cancels the parent coroutine.
    However, if the exception is caught locally, the parent coroutine remains unaware of the error.
   */
  suspend fun riskyTask() {
    try {
      delay(3000L)
      println("The answer is ${10 / 0}")
    } catch (e: Exception) {
      println("Oops, that didn't work")
    }
  }
  //here parent coroutine will notify
  suspend fun riskyTaskFix() {
    try {
      delay(3000L)
      println("The answer is ${10 / 0}")
    } catch (e: Exception) {
      if(e is CancellationException){
        throw  e
      }
      println("Oops, that didn't work")
    }
  }
 //endregion
  //region  5-Calling APIs from an Activity's coroutine scope
  /*
  Calling APIs from an Activity's coroutine scope (e.g., using lifecycleScope or directly tied to the Activity) can lead to cancellations if the Activity is destroyed, such as during a configuration change (e.g., rotation or theme change).
   */
  //endregion

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()



      setContent {
        TutorialAndroidTheme {
          //Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          MyApp()
          // }
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
}