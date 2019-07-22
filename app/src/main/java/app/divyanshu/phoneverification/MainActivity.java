package app.divyanshu.phoneverification;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText number,otp;
    Button register;
    private String mVerificationId;

    FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
         progressDialog = new ProgressDialog(MainActivity.this);
        number = findViewById(R.id.number);
        register = findViewById(R.id.register);
        otp = findViewById(R.id.otp);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerMethod();
            }
        });
    }

    private void registerMethod()
    {
        String number1 = "+91"+number.getText().toString();
        progressDialog.setTitle("OTP SENT");
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number1,60, TimeUnit.SECONDS, this,mCallbacks);
    }



    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks  = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
        {

            String code = phoneAuthCredential.getSmsCode();

            if (code!=null)
            {
                        otp.setText(code);
                        veryfyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e)
        {
            Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
         //   mResendToken = forceResendingToken;
        }
    };

    private void veryfyCode(String otp) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            String message = "Somthing is wrong, we will fix it soon...";
                            Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                                Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }}

