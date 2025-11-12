package com.example.fitlife;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.fitlife.Fragment.HomeFragment;
import com.example.fitlife.Fragment.ProfileFragment;
import com.example.fitlife.Fragment.WorkoutsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment=null;
                int intId= item.getItemId();
                if (intId==R.id.home){
                    fragment= new HomeFragment();
                }else if (intId==R.id.workout){
                    fragment= new WorkoutsFragment();
                }else if (intId==R.id.profile){
                    fragment= new ProfileFragment();
                }
                if (fragment != null) {
                    loadFragment((HomeFragment) fragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(HomeFragment homeFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();
    }
}