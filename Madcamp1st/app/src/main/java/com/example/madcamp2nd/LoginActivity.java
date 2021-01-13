package com.example.madcamp2nd;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends Activity {
//    private LoginButton btn_Facebook_Login;
    FirebaseAuth auth;
    String temp_name = null;
    List<List<String>> temp_number = null;

    private Button btn_custom_login;
    private CallbackManager callbackManager;
    private LoginCallback loginCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();
        loginCallback = new LoginCallback();

//        if (auth.getCurrentUser() != null) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }

        btn_custom_login = (Button) findViewById(R.id.btn_custom_login);
        btn_custom_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile","email"));
                loginManager.registerCallback(callbackManager, loginCallback);
            }
        });
    }
    public class LoginCallback implements FacebookCallback<LoginResult>
    {

        @Override
        public void onSuccess(LoginResult loginResult)
        {
            Log.e("Callback :: ", "onSuccess");
            requestMe(loginResult.getAccessToken());
        }

        @Override
        public void onCancel()
        {
            Log.e("Callback :: ", "onCancel");
        }

        @Override
        public void onError(FacebookException error)
        {
            Log.e("Callback :: ", "onError : " + error.getMessage());
        }

        // 사용자 정보 요청
        public void requestMe(AccessToken token)
        {
            Log.d("Token", "handleFacebookAccessToken : " + token.toString());
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            Log.d("Credential", "Credential is " + credential);
            auth.signInWithCredential(credential).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String displayName = auth.getCurrentUser().getDisplayName();
                        Log.e("Success", displayName);
                        Uri photoUrl = auth.getCurrentUser().getPhotoUrl();
                        Log.e("Success", photoUrl.toString());
                        String uid = auth.getCurrentUser().getUid();
                        Log.e("Success", uid);
//                        String method =  auth.getCurrentUser().getProviderData().get(1).getProviderId().toString().toLowerCase();
//                        Log.e("Success", method);
                        Users user = new Users(uid);
                        user.setName(displayName);
                        user.setProfile(photoUrl.toString());
                        //Retrofit use

                        Call<Users> call = RetrofitClient.getApiService().usersave(user);
                        call.enqueue(new Callback<Users>() {
                            @Override
                            public void onResponse(Call<Users> call, Response<Users> response) {
                                if (response.isSuccessful()){
//
                                    temp_name =  response.body().getUid();
                                    temp_number = response.body().getContacts();

                                    user.setContacts(temp_number);
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("user", user);
                                    startActivity(intent);
                                    finish();

                                }
                                else{
                                }

                            }
                            @Override
                            public void onFailure(Call<Users> call, Throwable t) {
                                Log.e("Login Activity", "dddd login fail");
                            }
                        });
                    }
                    else{
                        //Login Fail
                        Log.d("Failure", "signInWithCredential:Failure : " + task);
                        Toast.makeText(LoginActivity.this, "Login fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void logout(){
        auth.signOut();
    }

}
