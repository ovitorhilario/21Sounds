package com.hyper.twentyonesounds.ui.main.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.databinding.FragmentMainProfileBinding
import com.hyper.twentyonesounds.ui.main.MainActivity
import com.hyper.twentyonesounds.ui.main.model.home.UserPreferences
import com.hyper.twentyonesounds.utils.extension.isValid
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class ProfileFragment : Fragment() {

    private var _binding : FragmentMainProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, group: ViewGroup?, saved: Bundle?): View {
        _binding = FragmentMainProfileBinding.inflate(inflater, group, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
    }

    fun setupUI() {
        lifecycleScope.launch {
            (requireActivity() as MainActivity).userPrefFlow.collect { userPref ->
                binding.inputProfileName.editText?.setText(userPref.name)
                binding.inputProfileId.editText?.setText(userPref.id)
                binding.inputProfileEmail.editText?.setText(userPref.email)
            }
        }
    }

    fun setupListeners() {
        binding.btnProfileBack.onClick = {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnProfileSave.setOnClickListener {
            val name = binding.inputProfileName.editText?.text.toString()
            val id = binding.inputProfileId.editText?.text.toString()
            val email = binding.inputProfileEmail.editText?.text.toString()

            if (name.isValid() && id.isValid() && email.isValid()) {
                try {
                    saveUserPref(UserPreferences(name, id, email))

                    MotionToast.darkColorToast(requireActivity(),
                        title = "Success",
                        message = "Configurações salvadas!",
                        style = MotionToastStyle.SUCCESS,
                        position = MotionToast.GRAVITY_BOTTOM,
                        duration = MotionToast.SHORT_DURATION,
                        font = ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
                    )

                } catch (e: Exception) {

                    MotionToast.darkColorToast(requireActivity(),
                        title = "Error",
                        message = "Erro ao salvar: ${e.message}",
                        style = MotionToastStyle.ERROR,
                        position = MotionToast.GRAVITY_BOTTOM,
                        duration = MotionToast.SHORT_DURATION,
                        font = ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
                    )
                }
             } else {
                MotionToast.darkColorToast(requireActivity(),
                    title = "Warning",
                    message = "Preencha todos os campos para salvar!",
                    style = MotionToastStyle.WARNING,
                    position = MotionToast.GRAVITY_BOTTOM,
                    duration = MotionToast.SHORT_DURATION,
                    font = ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
                )
             }
        }
    }

    fun saveUserPref(userPref: UserPreferences) {
        lifecycleScope.launch {
            (requireActivity() as MainActivity).dataStore.edit { settings ->
                settings[MainActivity.PREF_PROFILE_NAME] = userPref.name
                settings[MainActivity.PREF_PROFILE_ID] = userPref.id
                settings[MainActivity.PREF_PROFILE_EMAIL] = userPref.email
            }
        }
    }
}