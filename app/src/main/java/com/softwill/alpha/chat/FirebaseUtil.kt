package com.softwill.alpha.chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.encodeUtf8
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale

object FirebaseUtil {
    fun currentUserId(userId: String): String {
        return userId
    }

    //    public static boolean isLoggedIn(){
    //        if(currentUserId()!=null){
    //            return true;
    //        }
    //        return false;
    //    }
    fun currentUserDetails(id: String?): DocumentReference {
        return FirebaseFirestore.getInstance().collection("users").document(id!!)
    }

    fun allUserCollectionReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("chats")
    }

    fun getChatroomReference(chatroomId: String?): DocumentReference {
        return FirebaseFirestore.getInstance().collection("chatRooms").document(chatroomId!!)
    }

    fun getChatroomMessageReference(chatroomId: String?): CollectionReference {
        return getChatroomReference(chatroomId).collection("chats")
    }

    fun getChatroomId(userId1: String, userId2: String): String {
        return if (userId1.hashCode() < userId2.hashCode()) {
            userId1 + "_" + userId2
        } else {
            userId2 + "_" + userId1
        }
    }

    fun allChatroomCollectionReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("chatRooms")
    }

    fun getOtherUserFromChatroom(userID: String, userIds: List<String>): DocumentReference {
        return if (userIds[0] == userID) {
            allUserCollectionReference().document(userIds[1])
        } else {
            allUserCollectionReference().document(userIds[0])
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun timestampToString(timestamp: Timestamp): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp.toDate())
    }

 @SuppressLint("SimpleDateFormat")
    fun timeFormatAM_PM(timestamp: String): String {
     // Parse the input time
     val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
     val date = inputFormat.parse(timestamp)

     // Format the time in AM/PM format
     val amPmFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
     val formattedTime = amPmFormat.format(date!!)

     return formattedTime
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    //    public static StorageReference  getCurrentProfilePicStorageRef(){
    //        return FirebaseStorage.getInstance().getReference().child("profile_pic")
    //                .child(FirebaseUtil.currentUserId());
    //    }
    fun getOtherProfilePicStorageRef(otherUserId: String?): StorageReference {
        return FirebaseStorage.getInstance().reference.child("avtarUrl")
            .child(otherUserId!!)
    }

    fun checkDateOrTime(inputText: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        try {
            // Try parsing as date
            val date = dateFormat.parse(inputText)
            if (date != null) {
                return "Input is a Date"
            }
        } catch (e: Exception) {
            // Parsing as date failed
        }

        try {
            // Try parsing as time
            val time = timeFormat.parse(inputText)
            if (time != null) {
                return "Input is a Time"
            }
        } catch (e: Exception) {
            // Parsing as time failed
        }

        return "Input is neither a Date nor a Time"
    }

    fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        val decodedBytes: ByteArray = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun openPdfFromBase64(context: Context, base64String: String, fileName: String) {
        val bytes = Base64.decode(base64String, Base64.DEFAULT)
        val file = File(context.cacheDir, "temp_pdf_file.pdf")
        FileOutputStream(file).use { fos ->
            fos.write(bytes)
            fos.flush()
        }

        // Now you can use a PDF viewer library to open the file
        // For example, you can use AndroidPdfViewer library
        // Add dependency in your build.gradle:
        // implementation 'com.github.barteksc:android-pdf-viewer:3.2.0-beta.1'
        // Then, you can open the PDF file like this:
        val intent = Intent(context, PDFViewerActivity::class.java)
        intent.putExtra("pdf_file_path", file.absolutePath)
        intent.putExtra("pdf_file_name", fileName)
        context.startActivity(intent)

    }

    // Function to convert URL to Base64
    fun urlToBase64(url: String): String? {
        var connection: HttpURLConnection? = null
        var reader: BufferedReader? = null
        try {
            // Open connection to the URL
            connection = URL(url).openConnection() as HttpURLConnection
            connection.connect()

            // Read the content from the URL
            reader = BufferedReader(InputStreamReader(connection.inputStream))
            val stringBuilder = StringBuilder()
            val line: String?=null
            while (reader.readLine()!= null) {
                stringBuilder.append(line).append('\n')
            }

            // Convert the content to Base64
            val data = stringBuilder.toString().encodeUtf8().toByteArray()
            return Base64.encodeToString(data, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Close resources
            reader?.close()
            connection?.disconnect()
        }
        return null
    }

    fun downloadPdfFromUrl(url: String): ByteArray {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        val inputStream = connection.inputStream
        val outputStream = ByteArrayOutputStream()
        inputStream.copyTo(outputStream)
        connection.disconnect()
        return outputStream.toByteArray()
    }

}
