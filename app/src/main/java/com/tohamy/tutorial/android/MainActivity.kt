package com.tohamy.tutorial.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap.Companion.Square
import androidx.compose.ui.tooling.preview.Preview
import com.tohamy.tutorial.android.ui.theme.TutorialAndroidTheme
/*
Reference https://www.youtube.com/watch?v=Itb5fu4UVt4
 */
/* notes
1- inline function like include copy past code when called from any where (Decompile)
2- if function simple Compiler will complain example function square not have lambda  as parameters
3- if function with simple body can use inline function only if frequency used like kotlin.math.square
3- example kotlin inline function is list.map
4- have context example if you call this function from coroutine and used function delay not need to be
suspend because it copy past code
5-The reified keyword allows the type parameter T to be accessed at runtime, enabling features like T::class.simpleName
6- can inline val also class
7-In Kotlin, value classes are a feature introduced to optimize memory usage and improve performance by wrapping a single value. They are similar to inline classes but are more efficient. Value classes are used to represent simple types that should behave like a regular class but without the overhead of an extra object allocation.

 */
class MainActivity : ComponentActivity() {
  //Expected performance impact from inlining is insignificant. I
  inline fun square(numb:Int):Int {
    return  numb*numb
  }
  inline fun <reified T> printClassName(instance: T) {
    println("The class name is: ${T::class.simpleName}")
  }
  /*
  return@findNumberInline true exits just the lambda and tells the inline function that the condition for the current number is satisfied.
  It doesn’t stop the inline function itself—only the lambda execution.
  When the condition in the findNumberInline function is true, return number immediately exits the entire function
   */
//region return
  inline fun findNumberInline(numbers: List<Int>, condition: (Int) -> Boolean): Int? {
    for (number in numbers) {
      if (condition(number)) {
        return number // Exits the whole function if condition is true
      }
    }
    return null // Returns null if no number satisfies the condition
  }

  fun main() {
    val result = findNumberInline(listOf(1, 2, 3, 4, 5)) { num ->
      if (num > 3) {
        return@findNumberInline true // Exits only this lambda
      }
      false // Continues checking the next number
    }

    println("Result: $result") // Output: Result: 4
  }
//endregion
  /*
You need to inline a lambda but want to prevent it from exiting the enclosing function early using return
  example if lambda inside coroutine
   */
  //region crossinline
inline fun doSomethingWithCrossinline(crossinline action: () -> Unit) {
  println("Before action")

  // We can't return from this function using `action` directly
  action() // Lambda will be executed here

  println("After action")
}

  fun mainz() {
    doSomethingWithCrossinline {
      println("Executing action")
      // `return` is not allowed here; it will cause a compile error
      // return // Uncommenting this will cause a compile-time error
    }
  }

  //endregion inside coroutine
  //region value class
  @JvmInline
  value class UserId(val id: Int)

  fun mainA() {
    val userId = UserId(123)
    println("User ID: ${userId.id}") // Accessing the value inside the value class
  }
  //endregion


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    square(20)
    enableEdgeToEdge()
    setContent {
      TutorialAndroidTheme {

        }
      }

  }
}


