package cloud.mariapps.chatapp.ui.chat

import android.Manifest.permission.CAMERA
import android.Manifest.permission.RECORD_AUDIO
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.navigation.fragment.findNavController
import cloud.mariapps.chatapp.R
import cloud.mariapps.chatapp.base.BaseFragment
import cloud.mariapps.chatapp.databinding.FragmentChatScreenBinding
import cloud.mariapps.chatapp.model.internal.ToolbarItem
import cloud.mariapps.chatapp.navigation.AppController
import cloud.mariapps.chatapp.ui.camera.CameraResultBack
import cloud.mariapps.chatapp.ui.camera.Media
import cloud.mariapps.chatapp.ui.composables.AlertItem
import cloud.mariapps.chatapp.utils.Constants
import cloud.mariapps.chatapp.utils.Global.createOptionsMenu
import cloud.mariapps.chatapp.utils.Global.setResultListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatScreenFragment :
    BaseFragment<FragmentChatScreenBinding>(FragmentChatScreenBinding::inflate) {
    @Inject
    lateinit var appController: AppController
    override fun setup() {
        setupToolBar(toolbarItem = ToolbarItem(title = getString(R.string.app_name)))
        createOptionsMenu(R.menu.chat_screen_menu) {
            if (it.itemId == R.id.addMembers) {
                val action =
                    ChatScreenFragmentDirections.actionChatScreenFragmentToUsersFragment(fromScreen = Constants.FromScreen.SelectUsers)
                findNavController().navigate(action)
            }
            true
        }
        setResultListener {
            if (it is CameraResultBack) {
                handleResult(it)
            }
        }
    }

    private fun handleResult(resultBack: CameraResultBack) {
        val action = ChatScreenFragmentDirections.actionChatScreenFragmentToMediaPreviewDialog(
            mediaResult = resultBack
        )
        findNavController().navigate(action)
    }

    override suspend fun observers() {

    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val action = ChatScreenFragmentDirections.actionChatScreenFragmentToCameraFragment()
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val videoPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (perms[CAMERA] == true && perms[RECORD_AUDIO] == true) {
            val action = ChatScreenFragmentDirections.actionChatScreenFragmentToVideoFragment()
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), "Camera/Mic permission denied", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private val selectImageIntent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                handleResult(resultBack = CameraResultBack(media = Media.GALLERY, uri = uri))
            }
        }

    override fun listeners() {
        binding.imgCaptureImage.setOnClickListener {
            appController.showAlertDialog(
                alertItem = AlertItem(
                    message = "Please Select an Option",
                    posBtnText = "Camera",
                    negBtnText = "Gallery",
                    posBtnListener = {
                        if (checkSelfPermission(
                                requireContext(), CAMERA
                            ) != PermissionChecker.PERMISSION_GRANTED
                        ) {
                            cameraPermissionLauncher.launch(CAMERA)
                        } else {
                            val action =
                                ChatScreenFragmentDirections.actionChatScreenFragmentToCameraFragment()
                            findNavController().navigate(action)
                        }
                    },
                    negBtnListener = {
                        selectImageIntent.launch("image/*")
                    },
                    dismissOnClickOutside = true,
                    dismissOnBackPress = true
                )
            )


        }
        binding.imgCaptureVideo.setOnClickListener {
            if (checkSelfPermission(
                    requireContext(), CAMERA
                ) != PermissionChecker.PERMISSION_GRANTED || checkSelfPermission(
                    requireContext(), RECORD_AUDIO
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                videoPermissionLauncher.launch(arrayOf(CAMERA, RECORD_AUDIO))
            } else {
                val action = ChatScreenFragmentDirections.actionChatScreenFragmentToVideoFragment()
                findNavController().navigate(action)
            }
        }
    }

}