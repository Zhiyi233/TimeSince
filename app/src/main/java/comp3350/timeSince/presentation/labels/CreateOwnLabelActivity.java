package comp3350.timeSince.presentation.labels;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import comp3350.timeSince.R;
import comp3350.timeSince.business.EventLabelManager;
import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.business.UserEventManager;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.presentation.HomeActivity;

public class CreateOwnLabelActivity extends AppCompatActivity {
    private Bundle extras;
    private TextView labelName;
    private CheckBox checkBox;

    private EventManager eventManager;
    private EventLabelManager eventLabelManager;
    private UserEventManager userEventManager;
    private String userID;
    private int eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_own_label);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        labelName = findViewById(R.id.label_name);
        checkBox = findViewById(R.id.checkBox);

        extras = getIntent().getExtras();
        userID = extras.get("email").toString();
        eventID = extras.getInt("eventID");
        eventManager = new EventManager(true);
        eventLabelManager = new EventLabelManager(true);

        try {
            userEventManager = new UserEventManager(userID, true);
        } catch (UserNotFoundException e) {
            String message = "Something went wrong with your account, sorry! Please try logging " +
                    "in again.";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            moveBackToHome();
        }

        findViewById(R.id.save_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveContents();
                moveToLabelList();
            }
        });
    }

    private void moveToLabelList(){
        Intent nextIntent = new Intent(getApplicationContext(), LabelListActivity.class);
        nextIntent.putExtra("email", userID);
        nextIntent.putExtra("eventID", eventID);
        finish();
        startActivity(nextIntent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        moveToLabelList();
        return true;
    }

    private void saveContents() {
        extras = getIntent().getExtras();
        EventLabelDSO newLabel;
        String message = "Creation successful! ";

        //if the label is successfully created, save information to the database
        try {
            newLabel = eventLabelManager.createLabel(labelName.getText().toString());
            if (newLabel != null && newLabel.validate()) {
                userEventManager.addUserLabel(newLabel);
                if (checkBox.isChecked()) {
                    EventDSO event = eventManager.getEventByID(eventID);
                    if (event != null && event.validate()) {
                        eventManager.addLabelToEvent(event, newLabel);
                    }
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "The new label is not successfully created.",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (EventNotFoundException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void moveBackToHome() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        finish(); // end this activity before starting the next
        startActivity(intent);
    }

}
