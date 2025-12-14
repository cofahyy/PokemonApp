package edu.iu.luddy.c323_capstone

import android.util.Log
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.util.concurrent.Executor

class NetworkClient(private val cronetEngine: CronetEngine, private val ex: Executor) {
    fun get(url: String, callback: (String?) -> Unit) {
        val requestBuilder = cronetEngine.newUrlRequestBuilder(url, object : UrlRequest.Callback() {
            private val bytesReceived = ByteArrayOutputStream()
            private val receiveChannel = Channels.newChannel(bytesReceived)

            override fun onRedirectReceived(
                request: UrlRequest?,
                info: UrlResponseInfo?,
                newLocationUrl: String?
            ) {
                request?.followRedirect()
            }

            override fun onResponseStarted(
                request: UrlRequest?,
                info: UrlResponseInfo?
            ) {
                request?.read(ByteBuffer.allocateDirect(102400))
            }

            override fun onReadCompleted(
                request: UrlRequest?,
                info: UrlResponseInfo?,
                byteBuffer: ByteBuffer?
            ) {
                byteBuffer?.flip()

                try {
                    receiveChannel.write(byteBuffer)
                } catch (e: Exception) {
                    Log.e("NetworkClient", "Error writing to stream", e)
                }
                byteBuffer?.clear()
                request?.read(byteBuffer)
            }

            override fun onSucceeded(
                request: UrlRequest?,
                info: UrlResponseInfo?
            ) {
                val response = bytesReceived.toString(Charsets.UTF_8.name())
                callback(response)
            }

            override fun onFailed(
                request: UrlRequest?,
                info: UrlResponseInfo?,
                error: CronetException?
            ) {
                Log.e("NetworkClient", "Request failed: ", error)
                callback(null)
            }
        }, ex)

        requestBuilder.build().start()
    }
}