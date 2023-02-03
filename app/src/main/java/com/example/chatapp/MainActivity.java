package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;


import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseAuth fireAuth;
    private FirebaseUser firebaseUser;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        fireAuth = FirebaseAuth.getInstance();
        firebaseUser = fireAuth.getCurrentUser();

        logout = findViewById(R.id.logoutbtn);
        logout.setOnClickListener(v -> {
            fireAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        if (firebaseUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    private void setupViewPager(ViewPager viewPager) {

        firebaseUser = fireAuth.getCurrentUser();
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatFragment(), "Chat");
        adapter.addFragment(new CallsFragment(), "Calls");
        adapter.addFragment(new StatusFragment(), "Status");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        userRef.child("userType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userType = dataSnapshot.getValue(String.class);
                if (userType != null && userType.equals("premium")) {
                    adapter.addFragment(new MapsFragment(), "Map");
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // handle the error
            }
        });
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}