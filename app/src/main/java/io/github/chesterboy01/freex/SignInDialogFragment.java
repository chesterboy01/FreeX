package io.github.chesterboy01.freex;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 10/10/2016.
 * 登陆的弹窗
 */

public class SignInDialogFragment extends DialogFragment {
    /* public static SignInDialogFragment newInstance() {
             return new SignInDialogFragment();
         }*/
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.login_fragment, null);
        //获取用户名的textview
        final EditText tv1 = (EditText) view.findViewById(R.id.id_txt_username);
        final EditText tv2 = (EditText) view.findViewById(R.id.id_txt_password);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        Button login_signup = (Button) view.findViewById(R.id.sign_up_button);
        Button login_login = (Button) view.findViewById(R.id.sign_in_button);

        final TextView errorPrompt_login = (TextView) view.findViewById(R.id.error_prompt_login_text);
        errorPrompt_login.setVisibility(View.INVISIBLE);

        tv1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorPrompt_login.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tv2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorPrompt_login.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        login_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            //the action of launching the login page will be executed here.
            public void onClick(View v) {
                Toast.makeText(getActivity(), "注册", Toast.LENGTH_LONG).show();
                SignUpDialogFragment dialog_signup = new SignUpDialogFragment();
                dialog_signup.show(getFragmentManager(), "signupDialog");

            }
        });


        login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            //the action of launching the login page will be executed here.
            public void onClick(View v) {
                //获取输入的信息
                String username = tv1.getText().toString();
                String password = tv2.getText().toString();

                Toast.makeText(getActivity(),
                        "用户名是" + username + " 密码是" + password,
                        Toast.LENGTH_LONG).show();
                Intent intent_toMain = new Intent(getActivity(),MainActivity.class);
                ((FullscreenActivityBeforeLogin)getActivity()).startActivity(intent_toMain);
              /*  if (IsValidLogin(username,password)) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                    dismiss();
                }
                else{
                    errorPrompt_login.setText("Wrong username or password.");
                    errorPrompt_login.setVisibility(View.VISIBLE);
                }*/


            }
        });
        return builder.setView(view).create();

            //在程序里面添加按键不太容易操控
            /*builder.setView(view)
                    // Add action buttons
                    .setPositiveButton("Sign In",

                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    //获取输入的信息
                                    String username = tv1.getText().toString();
                                    String password = tv2.getText().toString();

                                    Toast.makeText(getActivity(),
                                            "用户名是" + username + " 密码是" + password,
                                            Toast.LENGTH_LONG).show();
                                }
                            })
                    .setNegativeButton("Sign Up & Start Trading", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getActivity(), "注册", Toast.LENGTH_LONG).show();
                            SignUpDialogFragment dialog_signup = new SignUpDialogFragment();
                            dialog_signup.show(getFragmentManager(), "signupDialog");
                        }
                    });
            return builder.create();*/
    }
}
