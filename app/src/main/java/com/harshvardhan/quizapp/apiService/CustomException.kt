package com.harshvardhan.harsh_vardhan.apiService

sealed class CustomException(message: String) : Exception(message) {
    class NotFoundException(message: String) : CustomException(message)
    class EmptyContentException(message: String) : CustomException(message)
    class ClientException(message: String) : CustomException(message)
    class ServerException(message: String) : CustomException(message)
    class UnknownException(message: String) : CustomException(message)
}