package org.mavadvise.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.mavadvise.R;

public class FPSecurity extends AppCompatActivity {

    int secQuestion;
    String answer,
            quesText,
            ansFromUser,
            netId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpsecurity);
        Bundle b = getIntent().getExtras();
        secQuestion= b.getInt("securityQuestionValue");
        answer = b.getString("securityAnswer");
        netId=b.getString("netId");

        if(secQuestion==1)
            quesText="Where is your birth place?";
        else{
            if(secQuestion==2)
                quesText="What is your Mother's Maiden name?";
            else
                quesText="";
        }

        TextView fpSecQue = (TextView) findViewById(R.id.fpSecQuesTV);
        fpSecQue.setText(quesText);

        Button resetPassword = (Button) findViewById(R.id.goToReset);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAns();
            }
        });
    }

    private void navigateToReset(){
        Intent i = new Intent(FPSecurity.this,FPNewPassword.class);
        i.putExtra("netId",netId);
        startActivity(i);
        finish();
    }

    private void checkAns(){
        EditText fpSecAns = (EditText) findViewById(R.id.fpSecAnsET);
        ansFromUser=fpSecAns.getText().toString();
        if(answer.equalsIgnoreCase(ansFromUser)){
            navigateToReset();
        }
        else{
            Toast.makeText(getApplicationContext(), "Sorry, Your answer doesnot match with our records. Please try again",
                    Toast.LENGTH_LONG).show();
            return;
        }
    }
}
