package dev.davidalamo.appdpa.presentation.supabase

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.davidalamo.appdpa.data.remote.SupabaseManager
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Nombre del bucket publico que debes crear en Supabase Storage
private const val BUCKET = "imagenes"

@Composable
fun SupabaseStorageScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var subiendo by remember { mutableStateOf(false) }
    var urls by remember { mutableStateOf(listOf<String>()) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun cargarImagenes() {
        try {
            val archivos = SupabaseManager.client.storage.from(BUCKET).list()
            urls = archivos.map { archivo ->
                SupabaseManager.client.storage.from(BUCKET).publicUrl(archivo.name)
            }
            error = null
        } catch (e: Exception) {
            error = e.message
        }
    }

    LaunchedEffect(Unit) { cargarImagenes() }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            subiendo = true
            error = null
            scope.launch {
                try {
                    val bytes = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    } ?: throw Exception("No se pudo leer la imagen seleccionada")

                    val nombreArchivo = "foto_${System.currentTimeMillis()}.jpg"
                    SupabaseManager.client.storage.from(BUCKET).upload(nombreArchivo, bytes)
                    cargarImagenes()
                } catch (e: Exception) {
                    error = e.message
                } finally {
                    subiendo = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Supabase Storage", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                pickImageLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            enabled = !subiendo,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (subiendo) "Subiendo..." else "Subir imagen")
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
        ) {
            items(urls) { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Imagen almacenada en Supabase",
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
        }
    }
}

/*
CREATE POLICY "Permitir subir imágenes"
ON storage.objects
FOR insert
TO anon
WITH CHECK(
	bucket_id='imagenes'
);
 */