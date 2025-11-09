package Auth;

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

import com.example.fitlife.MainActivity;
import com.example.fitlife.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class SignupPage extends AppCompatActivity {

    EditText email,password,Cpassword;
    Button signup_btn;
    Text txt_login;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_page);

        email=findViewById(R.id.signup_email);
        password=findViewById(R.id.signup_password);
        Cpassword=findViewById(R.id.signup_Cpassword);
        signup_btn=findViewById(R.id.signup_btn);
//        txt_login=findViewById(R.id.signup_txt_login);

        auth=FirebaseAuth.getInstance();

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get Method
                String Email=email.getText().toString().trim();
                String Password=password.getText().toString().trim();
                String cPassword=Cpassword.getText().toString().trim();

                //Validation Method
                if (Email.isEmpty() || Password.isEmpty() || Password.length() < 6)  {
                    Toast.makeText(SignupPage.this, "Please enter a valid email and a password of at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
               if (!Password.equals(cPassword)){
                   Toast.makeText(SignupPage.this, "Password do not match", Toast.LENGTH_SHORT).show();
               }
                if (cPassword.equals(Password)){
                    auth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SignupPage.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                                Intent signupIntent=new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(signupIntent);
                                finish();
                            }else {
                                Toast.makeText(SignupPage.this, "Registration unsuccessful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    public void SignupToLogin(View view) {
        Intent intent=new Intent(getApplicationContext(),LoginPage.class);
        startActivity(intent);
    }
}