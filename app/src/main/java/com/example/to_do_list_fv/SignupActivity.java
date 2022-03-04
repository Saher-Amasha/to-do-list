package com.example.to_do_list_fv;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class SignupActivity extends Activity {
    private GoogleSignInClient mGoogleSignInClient;
    private final static  int RC_SIGN_IN=123;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextInputLayout T_name,T_email,T_password, T_repeat_password;
    TextView ET_password, ET_repeat_password, ET_email;
    private CallbackManager mCallbackManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        createRequest();

        T_email = findViewById(R.id.signup_email);
        T_password = findViewById(R.id.signup_password);
        T_repeat_password = findViewById(R.id.signup_repeat_password);
        T_name = findViewById(R.id.signup_name);

        Button goToSignUp = findViewById(R.id.go_to_login);
        goToSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        Button signupButton = findViewById(R.id.containedButton);
        signupButton.setOnClickListener(v -> signUP());
        ET_password = findViewById(R.id.passwordStrength);
        ET_repeat_password = findViewById(R.id.passwordsMatch);
        ET_email= findViewById(R.id.isEmailValid);

        Objects.requireNonNull(T_password.getEditText()).addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(T_password.getEditText()).getText().toString().length() != 0) {
                    switch (passwordStrength(T_password.getEditText().getText().toString())) {
                        case 0:
                            ET_password.setText(R.string.passwordStrength_strong);
                            ET_password.setTextColor(Color.GREEN);
                            break;
                        case 1:
                            ET_password.setText(R.string.passwordStrength_intermidiate);
                            ET_password.setTextColor(Color.YELLOW);
                            break;
                        case 2:
                            ET_password.setText(R.string.passwordStrength_weak);
                            ET_password.setTextColor(Color.RED);
                            break;
                    }
                }
                else{
                    ET_password.setText("");

                }
            }
        });
        Objects.requireNonNull(T_repeat_password.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(T_repeat_password.getEditText()).getText().toString().length() != 0) {
                    if (T_password.getEditText().getText().toString().compareTo(Objects.requireNonNull(T_repeat_password.getEditText()).getText().toString()) == 0) {
                        ET_repeat_password.setText(R.string.passwords_match);
                        ET_repeat_password.setTextColor(Color.GREEN);
                    } else {
                        ET_repeat_password.setText(R.string.passwordsDontMatch);
                        ET_repeat_password.setTextColor(Color.RED);
                    }
                }
                else{
                    ET_repeat_password.setText("");
                }
            }
        });
        Objects.requireNonNull(T_email.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(T_email.getEditText()).getText().toString().length() != 0) {
                    if (isEmailValid(T_email.getEditText().getText().toString())) {
                        ET_email.setText(R.string.email_valid);
                        ET_email.setTextColor(Color.GREEN);
                    } else {
                        ET_email.setText(R.string.email_not_valid);
                        ET_email.setTextColor(Color.RED);
                    }
                }
                else{
                    ET_email.setText("");
                }
            }
        });
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button_f);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }
    private void signUP() {
        String name= Objects.requireNonNull(T_name.getEditText()).getText().toString();
        String email= Objects.requireNonNull(T_email.getEditText()).getText().toString();
        String password= Objects.requireNonNull(T_password.getEditText()).getText().toString();
        String Repeated_password= Objects.requireNonNull(T_repeat_password.getEditText()).getText().toString();
        if(name.length()!=0&&email.length()!=0&&password.length()!=0&&Repeated_password.length()!=0&&password.compareTo(Repeated_password)==0&& passwordStrength(T_password.getEditText().getText().toString())<2&& isEmailValid(email)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(SignupActivity.this, ProfileActivity.class);
                                startActivity(intent);
                                updateUI(user);
                            } else {
                                // If sign up fails, display a message to the user.

                                updateUI(null);
                            }
                        }
                    });
        }
    }


    private void updateUI(FirebaseUser account) {
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("29091666425-kkn0r4d0elf9j6oae3ke5vq0518ral56.apps.googleusercontent.com")
                .requestEmail()
                .build();
        //


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(v -> signIn());
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(SignupActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }
                    }
                });
    }

    public static int passwordStrength(String input)
    {
        // Checking lower alphabet in string
        int n = input.length();
        boolean hasLower = false, hasUpper = false,
                hasDigit = false, specialChar = false;
        Set<Character> set = new HashSet<>(
                Arrays.asList('!', '@', '#', '$', '%', '^', '&',
                        '*', '(', ')', '-', '+'));
        for (char i : input.toCharArray())
        {
            if (Character.isLowerCase(i))
                hasLower = true;
            if (Character.isUpperCase(i))
                hasUpper = true;
            if (Character.isDigit(i))
                hasDigit = true;
            if (set.contains(i))
                specialChar = true;
        }

        if (hasDigit && hasLower && hasUpper && specialChar
                && (n >= 8))
            return 0;
        else if ((hasLower || hasUpper || specialChar)
                && (n >= 6))
            return 1;
        else
            return 2;
    }
    public static boolean isEmailValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(SignupActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            updateUI(user);
                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
}
