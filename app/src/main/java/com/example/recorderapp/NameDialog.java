package com.example.recorderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NameDialog extends Dialog {
    private Context context;
    private SetName setName;
    private boolean isChanged;

    public NameDialog(@NonNull Context context,SetName setName,boolean isChanged) {
        super(context);
        this.isChanged=isChanged;
        this.context=context;
        this.setName=setName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_dialog);

        EditText ed_name=findViewById(R.id.ed_name);
        Button b_save=findViewById(R.id.b_save_name);
        Button b_cancel=findViewById(R.id.b_cancel);
        b_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(ed_name.getText().toString().trim()))
                {
                    setName.setName(ed_name.getText().toString().trim(),isChanged);
                    ed_name.setText("");
                }
                else
                {
                    Toast.makeText(context, "Заполните поле", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}