package com.imastudio.userojol.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imastudio.userojol.MainActivity;
import com.imastudio.userojol.R;
import com.imastudio.userojol.helper.HeroHelper;
import com.imastudio.userojol.helper.SessionManager;
import com.imastudio.userojol.model.ResponseLogin;
import com.imastudio.userojol.model.ResponseRegister;
import com.imastudio.userojol.network.InitRetrofit;
import com.imastudio.userojol.network.RestApi;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRegisterActivity extends AppCompatActivity {

    @BindView(R.id.txt_rider_app)
    TextView txtRiderApp;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.rootlayout)
    RelativeLayout rootlayout;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginregister);
        ButterKnife.bind(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED
                    ) {
                requestPermissions(
                        new String[]{android.Manifest.permission.READ_PHONE_STATE},
                        110);


            }
            return;
        }
    }

    @OnClick({R.id.btnSignIn, R.id.btnRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                login();
                break;
            case R.id.btnRegister:
                register();
                break;
        }
    }

    private void login() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login");
        builder.setMessage(R.string.messagelogin);
        //layanan menjadikan view dalam bentuk popup
        LayoutInflater inflater = getLayoutInflater();
        //set view
        View tampilanlogin = inflater.inflate(R.layout.layout_login, null);
        //deklarasi n inisialisasi widget
        final ViewHolderLogin login = new ViewHolderLogin(tampilanlogin);
        builder.setView(tampilanlogin);
        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cek validasi
                if (TextUtils.isEmpty(login.edtEmail.getText().toString().trim())) {
                    login.edtEmail.setError(getString(R.string.requireemail));
                } else if (TextUtils.isEmpty(login.edtPassword.getText().toString().trim())) {
                    login.edtPassword.setError(getString(R.string.requirepassword));
                } else {
                    proseslogin(login, dialog);
                }
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        //untuk menampilkan dialog
        builder.show();
    }

    private void proseslogin(ViewHolderLogin login, final DialogInterface dialog) {
        final ProgressDialog loading = ProgressDialog.show(this, "Proses register", "Loading . . .");
        //call retrofit
        RestApi api = InitRetrofit.getInstance();
        String deviceid = HeroHelper.getDeviceUUID(LoginRegisterActivity.this);
        Call<ResponseLogin> loginCall = api.loginuser(
                deviceid,
                login.edtPassword.getText().toString(),
                login.edtEmail.getText().toString()
        );
        loginCall.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                if (response.isSuccessful()) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        Toast.makeText(LoginRegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        startActivity(new Intent(LoginRegisterActivity.this, MainActivity.class));
                        finish();
                        manager = new SessionManager(LoginRegisterActivity.this);
                        String token = response.body().getToken();
                        String iduser = response.body().getIdUser();
                        manager.createLoginSession(token);
                        manager.setIduser(iduser);
                    } else {
                        Toast.makeText(LoginRegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {

            }
        });
    }

    private void register() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Register");
        builder.setMessage(R.string.messageregister);
        //layanan menjadikan view dalam bentuk popup
        LayoutInflater inflater = getLayoutInflater();
        //set view
        View tampilanregister = inflater.inflate(R.layout.layout_register, null);
        //deklarasi n inisialisasi widget
        final ViewHolderRegister register = new ViewHolderRegister(tampilanregister);
        builder.setView(tampilanregister);
        builder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cek validasi
                if (TextUtils.isEmpty(register.edtEmail.getText().toString().trim())) {
                    register.edtEmail.setError(getString(R.string.requireemail));
                } else if (TextUtils.isEmpty(register.edtPassword.getText().toString().trim())) {
                    register.edtPassword.setError(getString(R.string.requirepassword));
                } else if (TextUtils.isEmpty(register.edtName.getText().toString().trim())) {
                    register.edtPassword.setError(getString(R.string.requirename));
                } else if (TextUtils.isEmpty(register.edtPhone.getText().toString().trim())) {
                    register.edtPassword.setError(getString(R.string.requirephone));
                } else {
                    prosesregister(register, dialog);
                }
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        //untuk menampilkan dialog
        builder.show();
    }

    private void prosesregister(ViewHolderRegister register, final DialogInterface dialog) {
        final ProgressDialog loading = ProgressDialog.show(this, "Proses register", "Loading . . .");
        //call retrofit
        RestApi api = InitRetrofit.getInstance();

        Call<ResponseRegister> registerCall = api.registeruser(
                register.edtName.getText().toString(),
                register.edtPassword.getText().toString(),
                register.edtPhone.getText().toString(),
                register.edtEmail.getText().toString()
        );
        //untuk menangkap callback
        registerCall.enqueue(new Callback<ResponseRegister>() {

            @Override
            public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
                if (response.isSuccessful()) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        Toast.makeText(LoginRegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(LoginRegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseRegister> call, Throwable t) {
                Toast.makeText(LoginRegisterActivity.this, "cek koneksi anda", Toast.LENGTH_SHORT).show();
                loading.dismiss();
                dialog.dismiss();
            }
        });
    }

    static class ViewHolderRegister {
        @BindView(R.id.edtEmail)
        MaterialEditText edtEmail;
        @BindView(R.id.edtPassword)
        MaterialEditText edtPassword;
        @BindView(R.id.edtName)
        MaterialEditText edtName;
        @BindView(R.id.edtPhone)
        MaterialEditText edtPhone;

        ViewHolderRegister(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ViewHolderLogin {
        @BindView(R.id.edtEmail)
        MaterialEditText edtEmail;
        @BindView(R.id.edtPassword)
        MaterialEditText edtPassword;

        ViewHolderLogin(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
