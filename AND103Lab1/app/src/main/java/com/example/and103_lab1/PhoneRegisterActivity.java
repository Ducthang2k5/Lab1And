package com.example.and103_lab1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneRegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String verificationID;

    EditText edPhoneNumber, edOTP;
    Button btnGetOTP, btnRegisterPhone;
    String phoneNumber, otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phone_register);

        edPhoneNumber = (EditText) findViewById(R.id.edPhoneNumber);
        edOTP = (EditText) findViewById(R.id.edOTP);
        btnGetOTP = (Button) findViewById(R.id.btnGetOTP);
        btnRegisterPhone = (Button) findViewById(R.id.btnRegisterPhone);

        auth = FirebaseAuth.getInstance();
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                edOTP.setText(phoneAuthCredential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
            }

            @Override
            public void onCodeSent(@NonNull String code, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                verificationID = code;
            }
        };

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOTP("+84"+edPhoneNumber.getText().toString());
            }
        });

        btnRegisterPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOTP(edOTP.getText().toString());
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Đăng nhập thành công !", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = task.getResult().getUser();
                } else {
                    Toast.makeText(getApplicationContext(), "Đăng nhập thất bại !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verifyOTP(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void getOTP(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+84"+phoneNumber)       // Số điện thoại để xác thực
                .setTimeout(60L, TimeUnit.SECONDS) // Thời gian chờ
                .setActivity(this)                 // Activity hiện tại
                .setCallbacks(callbacks)           // Callbacks xử lý kết quả
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
}