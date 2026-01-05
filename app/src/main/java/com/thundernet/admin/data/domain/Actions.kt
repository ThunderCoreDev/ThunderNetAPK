package com.thundenet.admin.domain

sealed class ActionResult {
    data class Success(val message: String) : ActionResult()
    data class Failure(val error: String) : ActionResult()
}

fun offlineFailure(): ActionResult.Failure =
    ActionResult.Failure("No hay conexión al servidor. Inténtalo más tarde.")