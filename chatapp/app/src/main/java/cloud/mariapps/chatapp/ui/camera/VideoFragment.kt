package cloud.mariapps.chatapp.ui.camera

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.view.View
import android.widget.*
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.navigation.fragment.findNavController
import cloud.mariapps.chatapp.R
import cloud.mariapps.chatapp.base.BaseFragment
import cloud.mariapps.chatapp.databinding.FragmentVideoBinding
import cloud.mariapps.chatapp.model.internal.ToolbarItem
import cloud.mariapps.chatapp.utils.Constants.DEFAULT_QUALITY_IDX
import cloud.mariapps.chatapp.utils.Constants.FILENAME_FORMAT
import cloud.mariapps.chatapp.utils.Global.setResult
import com.example.android.camerax.video.extensions.getAspectRatio
import com.example.android.camerax.video.extensions.getNameString
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VideoFragment : BaseFragment<FragmentVideoBinding>(FragmentVideoBinding::inflate) {

    private val captureLiveStatus = MutableLiveData<String>()

    private val cameraCapabilities = mutableListOf<CameraCapability>()

    private lateinit var videoCapture: VideoCapture<Recorder>
    private var currentRecording: Recording? = null
    private lateinit var recordingState: VideoRecordEvent
    private val maxLength = 10L

    enum class UiState {
        IDLE,       // Not recording, all UI controls are active.
        RECORDING,  // Camera is recording, only display Pause/Resume & Stop button.
        FINALIZED,  // Recording just completes, disable all RECORDING UI controls.
    }

    private var cameraIndex = 0
    private var qualityIndex = DEFAULT_QUALITY_IDX
    private var audioEnabled = true

    private val mainThreadExecutor by lazy { ContextCompat.getMainExecutor(requireContext()) }
    private var enumerationDeferred: Deferred<Unit>? = null
    private lateinit var outputDirectory: File

    private suspend fun bindCaptureUseCase() {
        val cameraProvider = ProcessCameraProvider.getInstance(requireContext()).await()

        val cameraSelector = getCameraSelector(cameraIndex)

        val quality = cameraCapabilities[cameraIndex].qualities[qualityIndex]
        val qualitySelector = QualitySelector.from(quality)

        val preview =
            Preview.Builder().setTargetAspectRatio(quality.getAspectRatio(quality)).build().apply {
                setSurfaceProvider(binding.previewView.surfaceProvider)
            }

        val recorder = Recorder.Builder().setQualitySelector(qualitySelector).build()
        videoCapture = VideoCapture.withOutput(recorder)

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner, cameraSelector, videoCapture, preview
            )
        } catch (exc: Exception) {
            exc.printStackTrace()
            resetUIandState("bindToLifecycle failed: $exc")
        }
        enableUI(true)
    }

    @SuppressLint("MissingPermission")
    private fun startRecording() {
        // create MediaStoreOutputOptions for our recorder: resulting our recording!
        val photoFile = File(
            outputDirectory, SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".mp4"
        )
        currentRecording = videoCapture.output.prepareRecording(
            requireActivity(), FileOutputOptions.Builder(photoFile).build()
        ).apply { if (audioEnabled) withAudioEnabled() }.start(mainThreadExecutor, captureListener)
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else requireContext().filesDir
    }

    /**
     * CaptureEvent listener.
     */
    private val captureListener = Consumer<VideoRecordEvent> { event ->
        // cache the recording state
        if (event !is VideoRecordEvent.Status) recordingState = event

        updateUI(event)

        if (event is VideoRecordEvent.Finalize) {
            // display the captured video
            setResult(
                result = CameraResultBack(
                    media = Media.VIDEO, uri = event.outputResults.outputUri
                )
            )
            findNavController().popBackStack()
        }
    }

    /**
     * Retrieve the asked camera's type(lens facing type). In this sample, only 2 types:
     *   idx is even number:  CameraSelector.LENS_FACING_BACK
     *          odd number:   CameraSelector.LENS_FACING_FRONT
     */
    private fun getCameraSelector(idx: Int): CameraSelector {
        if (cameraCapabilities.size == 0) {
            requireActivity().finish()
        }
        return (cameraCapabilities[idx % cameraCapabilities.size].camSelector)
    }

    data class CameraCapability(val camSelector: CameraSelector, val qualities: List<Quality>)

    /**
     * Query and cache this platform's camera capabilities, run only once.
     */
    init {
        enumerationDeferred = lifecycleScope.async {
            whenCreated {
                val provider = ProcessCameraProvider.getInstance(requireContext()).await()

                provider.unbindAll()
                for (camSelector in arrayOf(
                    CameraSelector.DEFAULT_BACK_CAMERA, CameraSelector.DEFAULT_FRONT_CAMERA
                )) {
                    try {
                        // just get the camera.cameraInfo to query capabilities
                        // we are not binding anything here.
                        if (provider.hasCamera(camSelector)) {
                            val camera = provider.bindToLifecycle(requireActivity(), camSelector)
                            QualitySelector.getSupportedQualities(camera.cameraInfo)
                                .filter { quality ->
                                    listOf(
                                        Quality.UHD, Quality.FHD, Quality.HD, Quality.SD
                                    ).contains(quality)
                                }.also {
                                    cameraCapabilities.add(CameraCapability(camSelector, it))
                                }
                        }
                    } catch (exc: java.lang.Exception) {
                        exc.printStackTrace()
                    }
                }
            }
        }
    }

    /**
     * One time initialize for CameraFragment (as a part of fragment layout's creation process).
     * This function performs the following:
     *   - initialize but disable all UI controls except the Quality selection.
     *   - set up the Quality selection recycler view.
     *   - bind use cases to a lifecycle camera, enable UI controls.
     */
    private fun initCameraFragment() {
        outputDirectory = getOutputDirectory()
        initializeUI()
        viewLifecycleOwner.lifecycleScope.launch {
            if (enumerationDeferred != null) {
                enumerationDeferred!!.await()
                enumerationDeferred = null
            }

            bindCaptureUseCase()
        }
    }

    /**
     * Initialize UI. Preview and Capture actions are configured in this function.
     * Note that preview and capture are both initialized either by UI or CameraX callbacks
     * (except the very 1st time upon entering to this fragment in onCreateView()
     */
    @SuppressLint("ClickableViewAccessibility", "MissingPermission")
    private fun initializeUI() {
        binding.cameraButton.apply {
            setOnClickListener {
                cameraIndex = (cameraIndex + 1) % cameraCapabilities.size
                // camera device change is in effect instantly:
                //   - reset quality selection
                //   - restart preview
                qualityIndex = DEFAULT_QUALITY_IDX
                enableUI(false)
                viewLifecycleOwner.lifecycleScope.launch {
                    bindCaptureUseCase()
                }
            }
            isEnabled = false
        }

        // audioEnabled by default is disabled.
        binding.audioSelection.isChecked = audioEnabled
        binding.audioSelection.setOnClickListener {
            audioEnabled = binding.audioSelection.isChecked
        }

        // React to user touching the capture button
        binding.startButton.apply {
            setOnClickListener {
                if (!this@VideoFragment::recordingState.isInitialized || recordingState is VideoRecordEvent.Finalize) {
                    enableUI(false)  // Our eventListener will turn on the Recording UI.
                    startRecording()
                    /*viewLifecycleOwner.lifecycleScope.launch{
                        delay(10 * 1000)
                        binding.stopButton.performClick()
                    }*/
                } else {
                    when (recordingState) {
                        is VideoRecordEvent.Start -> {
                            currentRecording?.pause()
                            binding.stopButton.visibility = View.VISIBLE
                        }
                        is VideoRecordEvent.Pause -> currentRecording?.resume()
                        is VideoRecordEvent.Resume -> currentRecording?.pause()
                        else -> throw IllegalStateException("recordingState in unknown state")
                    }
                }
            }
            isEnabled = false
        }

        binding.stopButton.apply {
            setOnClickListener {
                // stopping: hide it after getting a click before we go to viewing fragment
                binding.stopButton.visibility = View.INVISIBLE
                if (currentRecording == null || recordingState is VideoRecordEvent.Finalize) {
                    return@setOnClickListener
                }

                val recording = currentRecording
                if (recording != null) {
                    recording.stop()
                    currentRecording = null
                }
                binding.startButton.setImageResource(R.drawable.ic_start)
            }
            // ensure the stop button is initialized disabled & invisible
            visibility = View.INVISIBLE
            isEnabled = false
        }

        captureLiveStatus.observe(viewLifecycleOwner) {
            binding.captureStatus.apply {
                post { text = it }
            }
        }
        captureLiveStatus.value = ""
    }

    /**
     * UpdateUI according to CameraX VideoRecordEvent type:
     *   - user starts capture.
     *   - this app disables all UI selections.
     *   - this app enables capture run-time UI (pause/resume/stop).
     *   - user controls recording with run-time UI, eventually tap "stop" to end.
     *   - this app informs CameraX recording to stop with recording.stop() (or recording.close()).
     *   - CameraX notify this app that the recording is indeed stopped, with the Finalize event.
     *   - this app starts VideoViewer fragment to view the captured result.
     */
    private fun updateUI(event: VideoRecordEvent) {
        if (event is VideoRecordEvent.Status) recordingState.getNameString()
        else event.getNameString()
        when (event) {
            is VideoRecordEvent.Status -> {
                // placeholder: we update the UI with new status after this when() block,
                // nothing needs to do here.
            }
            is VideoRecordEvent.Start -> {
                showUI(UiState.RECORDING, event.getNameString())
            }
            is VideoRecordEvent.Finalize -> {
                showUI(UiState.FINALIZED, event.getNameString())
            }
            is VideoRecordEvent.Pause -> {
                binding.startButton.setImageResource(R.drawable.ic_resume)
            }
            is VideoRecordEvent.Resume -> {
                binding.startButton.setImageResource(R.drawable.ic_pause)
            }
        }

        val stats = event.recordingStats
//        val size = stats.numBytesRecorded / 1000
        val time = java.util.concurrent.TimeUnit.NANOSECONDS.toSeconds(stats.recordedDurationNanos)
//        var text = "${state}: recorded ${size}KB, in ${time}second"
        val text = "$time second(s)"
        captureLiveStatus.value = text
        if (time == maxLength) {
            binding.stopButton.performClick()
        }
    }

    /**
     * Enable/disable UI:
     *    User could select the capture parameters when recording is not in session
     *    Once recording is started, need to disable able UI to avoid conflict.
     */
    private fun enableUI(enable: Boolean) {
        arrayOf(
            binding.cameraButton,
            binding.startButton,
            binding.stopButton,
            binding.audioSelection,
        ).forEach {
            it.isEnabled = enable
        }
        // disable the camera button if no device to switch
        if (cameraCapabilities.size <= 1) {
            binding.cameraButton.isEnabled = false
        }
    }

    /**
     * initialize UI for recording:
     *  - at recording: hide audio, qualitySelection,change camera UI; enable stop button
     *  - otherwise: show all except the stop button
     */
    private fun showUI(state: UiState, status: String = "") {
        try {
            binding.let {
                when (state) {
                    UiState.IDLE -> {
                        it.startButton.setImageResource(R.drawable.ic_start)
                        it.stopButton.visibility = View.INVISIBLE

                        it.cameraButton.visibility = View.VISIBLE
                        it.audioSelection.visibility = View.VISIBLE
                    }
                    UiState.RECORDING -> {
                        it.cameraButton.visibility = View.INVISIBLE
                        it.audioSelection.visibility = View.INVISIBLE

                        it.startButton.setImageResource(R.drawable.ic_pause)
                        it.startButton.isEnabled = true
                        it.stopButton.visibility = View.VISIBLE
                        it.stopButton.isEnabled = true
                    }
                    UiState.FINALIZED -> {
                        it.startButton.setImageResource(R.drawable.ic_start)
                        it.stopButton.visibility = View.INVISIBLE
                    }
                }
                it.captureStatus.text = status
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    /**
     * ResetUI (restart):
     *    in case binding failed, let's give it another change for re-try. In future cases
     *    we might fail and user get notified on the status
     */
    private fun resetUIandState(reason: String) {
        enableUI(true)
        showUI(UiState.IDLE, reason)

        cameraIndex = 0
        qualityIndex = DEFAULT_QUALITY_IDX
        audioEnabled = false
        binding.audioSelection.isChecked = audioEnabled
    }

    override fun setup() {
        setupToolBar(toolbarItem = ToolbarItem(hideActionBar = true), isOnlyPortrait = true)
        initCameraFragment()
    }

    override suspend fun observers() {

    }

    override fun listeners() {

    }
}