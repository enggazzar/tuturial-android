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
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.measureTime

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
    //  launch {  } if call every function inside launch tow function will be executed at same time
    //https://www.youtube.com/watch?v=yc_WfBp-PdE&list=PLQkwcJG4YTCQcFEPuYGuv54nYai_lwil_&index=3
    GlobalScope.launch {

      val network1 = networkCall1()
      val network2 = networkCall2()
      Log.e("Tag", network1)
      Log.e("Tag", network2)
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
      withContext(Dispatchers.Main) {
        //here can update ui
      }
    }

    //endregion
    //region runBlocking
    /*
    https://www.youtube.com/watch?v=k9yisEEPC8g&list=PLQkwcJG4YTCQcFEPuYGuv54nYai_lwil_&index=5
    1- will block main thread if have ex delay useful for test
    2- like sleep but can call suspend function inside
     */

    runBlocking {
      delay(100)
    }
    Log.e("tag", "will executed after 100")
    //endregion
    //region join wait canceling
    /*
    https://www.youtube.com/watch?v=55W60o9uzVc&list=PLQkwcJG4YTCQcFEPuYGuv54nYai_lwil_&index=7
    1- join will block main thread till current coroutine is finished
     */
    val job = GlobalScope.launch {
      repeat(3) {
        Log.e("Tage", "is working")
      }
    }
    runBlocking {
      job.join()
      Log.e("Tage", "is finished")
    }

    /*
    2- cancel will cancel coroutine but need to use is active because sometimes coroutine will busy with
    calculation
     */

    val job1 = GlobalScope.launch {
      if (isActive) {
        repeat(3) {
          Log.e("Tage", "is working")
        }
      }

      /*
      3-withTimeout out will cancel coroutine automatic if working more than the time
       */
      GlobalScope.launch {
        withTimeout(1000) {
          if (isActive) {
            repeat(3) {
              Log.e("Tage", "is working")
            }
          }
        }
      }

      //endregion
      //region Async and Await - Kotlin Coroutines
      /*
      1-launch will return job where can use join to wait coroutine but async will return deferred
      2- async will use with if there are  result( Deferred) but launch not
      3- measureTime to calculate time till coroutine is finish
       */
      GlobalScope.launch {
        var network1: String? = null
        var network2: String? = null
        network1 = networkCall1()
        network2 = networkCall2()
        //will print null
        Log.e("Tag", network1)
        Log.e("Tag", network2)
      }
      GlobalScope.launch {
        val time = measureTime {
          var network1: String? = null
          var network2: String? = null
          val job1 = launch { network1 = networkCall1() }
          val job2 = launch { network1 = networkCall2() }
          //will print null
          job1.join()
          job2.join()
          //will wait until job1 and job 2 is finished thin print job1 and job 2 will executed in same time
          Log.e("Tag", network1.toString())
          Log.e("Tag", network2.toString())
        }
        Log.e("Tag", "time is $time")
      }
      //async is better way more than above
      GlobalScope.launch {
        val time = measureTime {
          val network1 = async { networkCall1() }
          val network2 = async { networkCall2() }

          //will wait until network1 and network2 2 is finished thin print job1 and job 2 will executed in same time
          Log.e("Tag", network1.await())
          Log.e("Tag", network2.await())
        }
        Log.e("Tag", "time is $time")
      }

      //endregion
      //region lifecycle
      /*
     https://www.youtube.com/watch?v=uiPYcSSjNTI&list=PLQkwcJG4YTCQcFEPuYGuv54nYai_lwil_&index=8
     will cancel when open new activity if you make calculation you need to check if is active
      */
      lifecycleScope.launch {

      }
      //endregion
      //region Coroutine Cancellation & Exception Handling
      /*
       https://www.youtube.com/watch?v=VWlwkqmTLHc&list=PLQkwcJG4YTCQcFEPuYGuv54nYai_lwil_&index=11
       1- try catch not recommended with coroutine but use CoroutineExceptionHandler
       2- Coroutine scope if there are exception all sub coroutine will cancelled even we handle exception
       3-supervisorScope if there are exception all sub coroutine will not cancelled
       4- can use +  lifecycleScope.launch (handler+Dispatchers.Main)
       5-viewModelScope if there are exception all sub coroutine will cancelled even we handle exception
       6- if cancel job there are exception handle will eat cancel and if there are print after try catch
       will executed to stop this throw new exception

       */
      val handler= CoroutineExceptionHandler { coroutineContext, throwable ->
         println("Exception is ${throwable.message}")
      }
      lifecycleScope.launch (handler+Dispatchers.Main){
        launch {
          //app will not crash
          throw Exception("Errore")
        }
        launch {
          //will not printed all sub coroutine wil be cancelled
         println("coroutine 2")
        }

      }
      supervisorScope {
        launch {
          //app will not crash
          throw Exception("Errore")
        }
        launch {
          //will  printed
          println("coroutine 2")
        }

      }
      //cancel job
      lifecycleScope.launch {
        val job = launch {
          try {
            delay(timeMillis = 500L)
          } catch (e: Exception) {
            //to stop println  because e: Exception eat CancellationException
            if (e is CancellationException) {
              throw e
            }
            e.printStackTrace()
          }
          //will not print
          println("Coroutine 1 finished")
        }
        delay(timeMillis = 300L)
        job.cancel()
      }


      //endregion


      setContent {
        TutorialAndroidTheme {
          //Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          MyApp()
          // }
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
}