package ru.itschool.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {
    EditText name, year;
    Button btnSave, btnDelete;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    long userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        name = findViewById(R.id.userName);
        year = findViewById(R.id.userYear);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveUser();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteUser();
            }
        });

        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("id");
        }
        if(userId>0){
            userCursor = db.rawQuery("select * from "+DatabaseHelper.TABLE+ " where "
                    +DatabaseHelper.COLUMN_ID + "=?",new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();
            name.setText(userCursor.getString(1));
            year.setText(String.valueOf(userCursor.getInt(2)));
            userCursor.close();
        } else {
            btnDelete.setVisibility(View.GONE);
        }
    }

    private void DeleteUser() {
        db.delete(DatabaseHelper.TABLE, "_id = ?", new String[]{String.valueOf(userId)} );
        goHome();
    }

    private void SaveUser() {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, name.getText().toString());
        cv.put(DatabaseHelper.COLUMN_YEAR, Integer.parseInt(year.getText().toString()));
        if(userId > 0)
        {
            db.update(DatabaseHelper.TABLE, cv,
                    DatabaseHelper.COLUMN_ID + "=" + String.valueOf(userId),
                    null);
        } else {
            db.insert(DatabaseHelper.TABLE,
                    null, cv);
        }

        goHome();
    }

    private void goHome() {
        db.close();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);   //    позволяет оставит только одну активность (нельзя возвращать системной кнопкой назад)
        startActivity(i);
        finish();
    }
}
