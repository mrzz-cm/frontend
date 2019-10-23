package com.example.codematchfrontend;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

import static com.example.codematchfrontend.Global.createID;

public class ProfileView extends AppCompatActivity implements CoursesListAdapter.CoursesListClickListener{

    private LinkedList<String> courses;
    private RecyclerView coursesView;
    private RecyclerView.LayoutManager layoutManager;
    private CoursesListAdapter adapter;


    /** Called when the user taps the notificationviewbutton button */
    public void switchTabToNotifyView() {
        // Do something in response to button
        Intent intent = new Intent(this, NotifyView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
    public void switchTabToPostingView() {
        // Do something in response to button
        Intent intent = new Intent(this, PostingView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
    public void switchTabToProfileView() {
        Intent intent = new Intent (this, ProfileView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        coursesView = (RecyclerView) findViewById(R.id.courseList);

        layoutManager = new LinearLayoutManager(this);
        coursesView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(coursesView.getContext(),
                DividerItemDecoration.VERTICAL);
        coursesView.addItemDecoration(dividerItemDecoration);

        // debug: initialize courses
        courses = new LinkedList<String>();
        courses.add("test1");
        courses.add("test2");
        courses.add("test2");
        courses.add("test2");
        courses.add("test2");
        courses.add("test2");

        adapter = new CoursesListAdapter(this, this.courses);
        coursesView.setAdapter(adapter);

        Button addCourseButton = findViewById(R.id.addcoursebutton);
        final EditText course_input_text = findViewById(R.id.addCourseText);
        addCourseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addcourse(course_input_text.getText().toString());
                course_input_text.getText().clear();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        System.out.println("clicked at " + position);
        final int pressed_position = position;

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.course_delete_popup);
        dialog.setCanceledOnTouchOutside(true);

        Button yes = (Button) dialog.findViewById(R.id.delete_course_yes);
        Button no = (Button) dialog.findViewById(R.id.delete_course_no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCourseAtPosition(pressed_position);
                dialog.cancel();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void removeCourseAtPosition(int position) {
        this.courses.remove(position);
        adapter.notifyItemRemoved(position);
    }

    private void addcourse(String course) {
        courses.add(course);

        // send course information to the backend
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", Global.EMAIL);
            jsonObject.put("courseId", course);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        Request post_course_request = new Request.Builder()
                .url(Global.BASE_URL + "/user/add-course")
                .addHeader("Authorization", "Bearer " + Global.API_KEY)
                .post(body)
                .build();

        Global.HTTP_CLIENT.newCall(post_course_request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error: "+ e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("Post course request returned code " + response.code());
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
     public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.postingViewButton:
                Toast.makeText(this, "Posting View selected!!", Toast.LENGTH_SHORT).show();
                switchTabToPostingView();
                return true;
            case R.id.notifyViewButton:
                Toast.makeText(this, "Notification View selected!!", Toast.LENGTH_SHORT).show();
                switchTabToNotifyView();

                return true;
            case R.id.profileViewButton:
                Toast.makeText(this, "Profile View selected!!", Toast.LENGTH_SHORT).show();
                switchTabToProfileView();
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void deleteProfile() {
        /* This method is to enable the user to delete their profile from codematch and all associated data

         */
    }



    public void pushNotificationTest (View view) {
        NotificationsModule nModule = new NotificationsModule();

        nModule.newNotification("Test", "Wow, this is a test of the fact That it's a long ass 'string!!!");
    }

    }
