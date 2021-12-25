package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import java.lang.IllegalArgumentException

class BlurWorker(ctx : Context , params : WorkerParameters) : Worker(ctx , params) {
    override fun doWork(): Result {
        //declaring context for the state of the app at a time
        val appContext = applicationContext
        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        //first notification before blurring stars
        makeStatusNotification("Blurring Image" , appContext)
        return try{
            if(TextUtils.isEmpty(resourceUri)){
                Log.i("BlurWork" , "Invalid Input Uri")
                throw IllegalArgumentException("Invalid Input Uri")
            }
            val resolver = appContext.contentResolver
            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri))
            )
            //applying blur effect on the picture
            val outputImage = blurBitmap(picture , appContext)
            //saving as file uri
            val outputUri = writeBitmapToFile(appContext , outputImage)
            makeStatusNotification("Output is $outputUri", appContext)

            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
            Result.success(outputData)
        } catch (throwable : Throwable){
            Log.i("BlurWork" , "Blurring failed")
            Result.failure()
        }
    }
}