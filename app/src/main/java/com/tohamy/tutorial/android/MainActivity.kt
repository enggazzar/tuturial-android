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
import androidx.compose.ui.tooling.preview.Preview
import com.tohamy.tutorial.android.ui.theme.TutorialAndroidTheme

class MainActivity : ComponentActivity() {
  /*
  reference
  https://www.youtube.com/watch?v=bbMsuI2p1DQ
   */
  /*
  Dependency Injection as a Design Pattern
Dependency Injection focuses on:
1-Providing pre-built objects (dependencies) to other objects.
2-Promoting loose coupling by making a class independent of how its dependencies are instantiated.
3-Delegating the responsibility of dependency creation and management to an external framework or component.

   */
  /*
  Dagger Hilt
  1-is a dependency injection (DI) library for Android built on top of Dagger, designed to simplify and
  optimize dependency injection in Android applications. It provides a standard way to handle DI in Android projects, making your code easier to manage, test, and maintain.
  2- Lifetime Management
       @Singleton	The dependency is alive as long as the application is running. example(db)
       @ActivityScoped	The dependency is alive as long as the activity is alive.
       @FragmentScoped	The dependency is alive as long as the fragment is alive.
       @ViewModelScoped	The dependency is alive as long as the ViewModel is alive.
       @ServiceScoped	The dependency is alive as long as the service is alive.
    3-Summary of Advantages
         Simple
         Built-in Scoping: Predefined scopes like @Singleton, @ActivityScoped, and more.
         Lifecycle Awareness: Dependencies live as long as needed and are cleaned up when not.
         Performance Optimization: Reuses objects instead of creating new ones unnecessarily.
         Developer Productivity: Automates lifecycle and scope management, reducing boilerplate.
       4- instead of define var we inject instance in constructor
       5- constructor injection in dependency injection (e.g., using Dagger Hilt) helps determine outside the class which instance to provide to a class. The class itself doesn't need to know how to create its dependencies
       6- when using Dagger Hilt in a Kotlin project, you need the kotlin-kapt plugin to enable annotation processing for Hilt. This is because Hilt (and Dagger) generate the necessary code for dependency injection at compile-time using annotations. The kapt (Kotlin Annotation Processing Tool) plugin processes these annotations and generates the necessary classes.
   */
  //from di
  /*
  /*
  provide function
  1- A provider function (annotated with @Provides or @Binds) is a way to tell Hilt how to create or provide a particular type of dependency.
  2- Instead of calling a provider function directly, you can pass its dependency into the constructor of a class. Hilt will resolve and inject the necessary objects automatically.
   */
  @InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMyApi(): MyApi {
        return Retrofit.Builder()
            .baseUrl("https://test.com")
            .build()
            .create(MyApi::class.java)
    }
    @Provides
    @Singleton
    fun provideMyRepository(api:MyApi): MyRepository {
        return MyRepositoryImpl(api)
    }
}
1-@InstallIn  It determines the scope
     a-SingletonComponent::class (lifetime of the application)
     b-ActivityComponent::class (Lifetime: Tied to the lifetime of an activity.)
     c-ViewModelComponent::class (Tied to the lifetime of a ViewModel.)
     d-ServiceComponent::class(Tied to the lifecycle of a service)
     e-ActivityRetainedComponent::class(Lifetime: Tied to the lifetime of a retained activity. It survives configuration changes like screen rotations.)
2-@Provides when you need to define how a dependency should be instantiated (e.g., setting up Retrofit, database, or other configurations
3- @Singleton  Ensures that the MyApi instance is created only once and reused across the app(if I have multi
 imp will provide one instance)
 //==========
1-The @HiltAndroidApp annotation is applied to your custom Application class in an Android app
 2- if have tow provider function return same data type we can use @Named example:
  @Provides
    @Singleton
    fun provideMyApi( @Named("firstName"):name:String): MyApi {

    }
  @Provides
    @Named("firstName")
    fun provideFirstName(): String {
        return "John"
    }

    @Provides
    @Named("lastName")
    fun provideLastName(): String {
        return "Doe"
    }
3- When Field Injection is Used (When the class is managed by Android (e.g., Activity, Fragment, or Service), and you don't control the constructor.)
 @Inject lateinit var myRepository: MyRepository
 4- When you inject a dependency using @Inject without Lazy, the dependency is initialized immediately when the parent object (e.g., Activity, Fragment, or ViewModel) is created
  5-With Lazy Injection
When you use Lazy (from Dagger), the dependency is not initialized immediately. It will only be initialized the first time you call myDependency.get().





   */

  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {

  }
}


}