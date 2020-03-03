package ru.itschool.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView textView;
    SharedPreferences sharedPreferences;
    final String SAVED_TEXT = "saved_text";
    SQLiteDatabase db;
    Cursor query;
    DatabaseHelper databaseHelper;
    ListView listView;
    SimpleCursorAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       /* db = getBaseContext().openOrCreateDatabase("app.db",
                MODE_PRIVATE, null);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        listView = findViewById(R.id.list);
      /*  db.execSQL("CREATE TABLE IF NOT EXISTS users (name TEXT,age INTEGER)");
        db.execSQL("INSERT INTO users VALUES ('PAVEL',18);");
        db.execSQL("INSERT INTO users VALUES ('IVAN',35);");*/
      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              Intent i = new Intent(getApplicationContext(), UserActivity.class);
              i.putExtra("id", id);
              startActivity(i);
          }
      });


        loadText();
    }

    @Override
    protected void onResume() {
        super.onResume();

        db = databaseHelper.getReadableDatabase();
        query = db.rawQuery("select * from " + DatabaseHelper.TABLE, null);
        String[] headers = new String[]{DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_YEAR};
        userAdapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item, query, headers,
                new int[]{android.R.id.text1, android.R.id.text2}, 0);
        textView.setText("Найдено элементов " + query.getCount());
        listView.setAdapter(userAdapter);
    }

    void loadText() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        String savedText = sharedPreferences.getString(SAVED_TEXT, "");
        textView.setText(savedText);
        Toast.makeText(this, "Привет " + savedText, Toast.LENGTH_SHORT).show();
    }

    void saveText() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        int r = (int) (Math.random() * 1000000);
      /*  String s=  editText.getText().toString();
        db.execSQL("INSERT INTO users VALUES ('PAVEL',18);");
        db.execSQL("INSERT INTO users VALUES ( "+ s+" , "+100+");");*/
        ed.putString(SAVED_TEXT, editText.getText().toString() + " " + r);
        ed.commit();
        Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }

    public void Save(View view) {
        saveText();
        editText.setText("");
    }

    public void Load(View view) {
        loadText();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveText();
        query.close();
        db.close();
    }

    public void LoadDB(View view) {
        textView.setText("");
        databaseHelper.onUpgrade(db,1,2);
        query = db.rawQuery("SELECT * FROM users;", null);
        if (query.moveToFirst()) {
            textView.setText("Найдено элементов " + query.getCount()+"\n");
            do {
                textView.append("Name " + query.getString(1) +
                        " Year " + query.getInt(2) + "\n");

            } while (query.moveToNext());
        }


    }

    public void openUserActivity(View view) {
        Intent i = new Intent(this, UserActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
