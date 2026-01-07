package com.thundernet.admin.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.thundernet.admin.data.repo.ServerRepository
import com.thundernet.admin.domain.ActionResult
import com.thundernet.admin.domain.offlineFailure
import com.thundernet.admin.util.showSnack

abstract class BaseModuleFragment(layoutId: Int) : Fragment(layoutId) {
    protected lateinit var repo: ServerRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repo = ServerRepository(requireContext())
    }

    protected fun guardOnline(action: () -> Boolean, view: View, successMsg: String) {
        val result: ActionResult = if (repo.testConnection() && action()) {
            ActionResult.Success(successMsg)
        } else {
            offlineFailure()
        }
        when (result) {
            is ActionResult.Success -> showSnack(view, result.message)
            is ActionResult.Failure -> showSnack(view, result.error)
        }
    }
}