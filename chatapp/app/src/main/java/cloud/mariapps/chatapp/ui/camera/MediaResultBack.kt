package cloud.mariapps.chatapp.ui.camera

import android.net.Uri
import cloud.mariapps.chatapp.base.ResultBack

data class CameraResultBack(
    val media: Media, var uri: Uri
) : ResultBack()

enum class Media {
    CAMERA, GALLERY, VIDEO
}