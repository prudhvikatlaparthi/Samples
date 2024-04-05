package cloud.mariapps.chatapp.ui.camera

import android.widget.MediaController
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import cloud.mariapps.chatapp.base.BaseDialogFragment
import cloud.mariapps.chatapp.databinding.DialogMediaPreviewBinding
import cloud.mariapps.chatapp.utils.Global.setLayout

class MediaPreviewDialog :
    BaseDialogFragment<DialogMediaPreviewBinding>(DialogMediaPreviewBinding::inflate) {
    private val args by navArgs<MediaPreviewDialogArgs>()
    override fun setup() {
        setLayout(width = 0.8f, 0.8f)
        when (args.mediaResult.media) {
            Media.GALLERY, Media.CAMERA -> {
                binding.imgView.apply {
                    isVisible = true
                    setImageURI(args.mediaResult.uri)
                }
            }
            Media.VIDEO -> {
                val mc = MediaController(requireContext())
                binding.videoViewer.apply {
                    isVisible = true
                    setOnPreparedListener {
                        it.start()
                        mc.show(0)
                        it.isLooping = true
                    }
                    setVideoURI(args.mediaResult.uri)
                    setMediaController(mc)
                    mc.setAnchorView(this)
                    mc.setMediaPlayer(this)
//                    requestFocus()
                }.start()
            }
        }
    }

    override suspend fun observers() {

    }

    override fun listeners() {
        binding.tvNo.setOnClickListener {
            dismiss()
        }
        binding.tvProceed.setOnClickListener {
            dismiss()
        }
    }

    override fun onBackPress() {
        dismiss()
    }

}