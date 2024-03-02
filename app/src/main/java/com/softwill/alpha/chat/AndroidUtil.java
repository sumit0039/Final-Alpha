package com.softwill.alpha.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.softwill.alpha.chat.model.ChatUserModel;

public class AndroidUtil {

   public static  void showToast(Context context,String message){
       Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, ChatUserModel model){
        intent.putExtra("userId", model.userId);
       intent.putExtra("username", model.username);
       intent.putExtra("avtarUrl", model.avtarUrl);


    }

    public static ChatUserModel getUserModelFromIntent(Intent intent){
        ChatUserModel userModel= new ChatUserModel();
        userModel.username = intent.getStringExtra("username");
        userModel.avtarUrl = intent.getStringExtra("avtarUrl");
        userModel.userId = intent.getStringExtra("userId");
        return userModel;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
