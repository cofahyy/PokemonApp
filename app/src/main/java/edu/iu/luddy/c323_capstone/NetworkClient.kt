package edu.iu.luddy.c323_capstone

import android.util.Log
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.util.concurrent.Executor

// provides the get for the API
class NetworkClient(private val cronetEngine: CronetEngine, private val ex: Executor) {
    fun get(url: String, callback: (String?) -> Unit) {
        val requestBuilder = cronetEngine.newUrlRequestBuilder(url, object : UrlRequest.Callback() {

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
                byteBuffer?.clear()
                request?.read(byteBuffer)
                val byteArray = byteBuffer?.let { ByteArray(it.remaining()) }
                if (byteArray != null) {
                    byteBuffer.get(byteArray)
                }
                val response = byteArray?.toString(Charsets.UTF_8)
                callback(response)
            }

            override fun onSucceeded(
                request: UrlRequest?,
                info: UrlResponseInfo?
            ) {
                Log.i("TAG", "onSucceeded method called.")
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