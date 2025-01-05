package com.tohamy.tutorial.android.ui

/**
 *
 *
 * 31/12/2024
 */
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tohamy.tutorial.android.R

data class ImageItem(
  val id: Int,
  val category: String,
  val imageRes: Int,
  val name: String,
  val summary: String,
  val description: String
)

val sampleData = listOf(
  ImageItem(1, "Nature", R.drawable.image1, "Sunset", "A beautiful sunset", "A detailed description of a beautiful sunset."),
  ImageItem(9, "Nature", R.drawable.image9, "Dolphin", "The intelligent swimmer", "A detailed description " +
      "of a dolphin."),
  ImageItem(6, "Nature", R.drawable.image6, "Tokyo", "The bustling capital", "A detailed description of Tokyo."),

  ImageItem(2, "Nature", R.drawable.image2, "Forest", "A serene forest", "A detailed description of a serene forest."),
  ImageItem(3, "Nature", R.drawable.image3, "Mountain", "A majestic mountain", "A detailed description of a majestic mountain."),
  ImageItem(4, "Cities", R.drawable.image4, "New York", "The city that never sleeps", "A detailed description of New York."),
  ImageItem(5, "Cities", R.drawable.image5, "Paris", "The city of lights", "A detailed description of Paris."),
  ImageItem(6, "Cities", R.drawable.image6, "Tokyo", "The bustling capital", "A detailed description of Tokyo."),
  ImageItem(7, "Animals", R.drawable.image7, "Lion", "The king of the jungle", "A detailed description of a lion."),
  ImageItem(8, "Animals", R.drawable.image8, "Eagle", "The majestic bird", "A detailed description of an eagle."),
  ImageItem(9, "Animals", R.drawable.image9, "Dolphin", "The intelligent swimmer", "A detailed description of a dolphin.")
)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MyApp()
    }
  }
}

@Composable
fun MyApp() {
  var selectedImage by remember { mutableStateOf<ImageItem?>(null) }

  if (selectedImage == null) {
    ImageListScreen(onImageClick = { selectedImage = it })
  } else {
    ImageDetailScreen(image = selectedImage!!, onBackClick = { selectedImage = null })
  }
}

@Composable
fun ImageListScreen(onImageClick: (ImageItem) -> Unit) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(top = 120.dp)
      .padding(16.dp)
  ) {
    val groupedImages = sampleData.groupBy { it.category }
    groupedImages.forEach { (category, images) ->
      item {
        Text(
          text = category,
          style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
          modifier = Modifier.padding(vertical = 8.dp)
        )
        LazyRow {
          items(images.size) { index ->
            val image = images[index]
            ImageItemView(image = image, onClick = { onImageClick(image) })
          }
        }
      }
    }
  }
}

@Composable
fun ImageItemView(image: ImageItem, onClick: () -> Unit) {
  Column(
    modifier = Modifier
      .width(120.dp)
      .padding(8.dp)
      .clickable { onClick() },
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      painter = painterResource(id = image.imageRes),
      contentDescription = image.name,
      modifier = Modifier
        .size(100.dp)
        .padding(8.dp)
    )
    Text(
      text = image.name,
      style = MaterialTheme.typography.bodyMedium,
      maxLines = 1,
      modifier = Modifier.padding(top = 4.dp)
    )
  }
}

@Composable
fun ImageDetailScreen(image: ImageItem, onBackClick: () -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(top = 90.dp)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Button(onClick = onBackClick) {
      Text(text = "Back")
    }
    Image(
      painter = painterResource(id = image.imageRes),
      contentDescription = image.name,
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
    )
    Text(
      text = image.name,
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
    )
    Text(
      text = image.summary,
      style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
    )
    Text(
      text = image.description,
      style = MaterialTheme.typography.bodyLarge
    )
  }
}
