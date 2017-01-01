package ir.mtajik.firebasedatabasedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ir.mtajik.firebasedatabasedemo.models.CarModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView txtDetails;
    private EditText inputName, inputPrice;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private String carId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initilizeUi();
    }

    private void initilizeUi() {
        // Displaying toolbar icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        txtDetails = (TextView) findViewById(R.id.txt_information);
        inputName = (EditText) findViewById(R.id.name);
        inputPrice = (EditText) findViewById(R.id.email);
        btnSave = (Button) findViewById(R.id.btn_save);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'Cars' node
        mFirebaseDatabase = mFirebaseInstance.getReference("cars");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Realtime Database");

        // app_title change listener
        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);

                // update toolbar title
                getSupportActionBar().setTitle(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        // Save / update the Car
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = inputName.getText().toString();
                String price = inputPrice.getText().toString();

                // Check for already existed carId
                if (TextUtils.isEmpty(carId)) {
                    createCar(name, price);
                } else {
                    updateCar(name, price);
                }
            }
        });

        toggleButton();
    }

    // Changing button text
    private void toggleButton() {
        if (TextUtils.isEmpty(carId)) {
            btnSave.setText("Save");
        } else {
            btnSave.setText("Update");
        }
    }

    private void createCar(String name, String price) {

        if (TextUtils.isEmpty(carId)) {
            carId = mFirebaseDatabase.push().getKey();
        }

        CarModel car = new CarModel(carId, name, price);

        mFirebaseDatabase.child(carId).setValue(car);

        addCarChangeListener();
    }

    /**
     * Car data change listener
     */
    private void addCarChangeListener() {
        // Car data change listener
        mFirebaseDatabase.child(carId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CarModel car = dataSnapshot.getValue(CarModel.class);

                // Check for null
                if (car == null) {
                    Log.e(TAG, "Car data is null!");
                    return;
                }

                Log.e(TAG, "Car data is changed!" + "Id=" + car.getId() + " | " + car.getName() +
                        " | price: " + car.getPrice());

                // Display newly updated name and email
                txtDetails.setText("Id=" + car.getId() + " | name: " + car.getName() +
                        " | price: " + car.getPrice());

                // clear edit text
                inputPrice.setText("");
                inputName.setText("");

                toggleButton();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read car data", error.toException());
            }
        });
    }

    private void updateCar(String name, String email) {
        // updating the car via child nodes
        if (!TextUtils.isEmpty(name))
            mFirebaseDatabase.child(carId).child("name").setValue(name);

        if (!TextUtils.isEmpty(email))
            mFirebaseDatabase.child(carId).child("price").setValue(email);
    }
}
