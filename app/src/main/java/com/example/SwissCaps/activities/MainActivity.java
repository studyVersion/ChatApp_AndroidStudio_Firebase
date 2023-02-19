package com.example.SwissCaps.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;


import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.SwissCaps.R;
import com.example.SwissCaps.fragments.CallsFragment;
import com.example.SwissCaps.fragments.ChatFragment;
import com.example.SwissCaps.fragments.MapsFragment;
import com.example.SwissCaps.fragments.QrCodeFragment;
import com.example.SwissCaps.fragments.ProfileFragement;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private FirebaseAuth fireAuth;
    private FirebaseUser firebaseUser;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fireAuth = FirebaseAuth.getInstance();
        firebaseUser = fireAuth.getCurrentUser();

        if (firebaseUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        fab = findViewById(R.id.floatingBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager2.setCurrentItem(0);
            }
        });


        viewPager2 = findViewById(R.id.viewpager);
        setupViewPager(viewPager2);
        viewPager2.setUserInputEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupViewPager(ViewPager2 viewPager) {

        tabLayout = findViewById(R.id.tabs);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(this);

        setFragements(adapter);

        viewPager2.setAdapter(adapter);
        viewPager2.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {

            tab.setIcon(adapter.mFragmentIconList.get(position));

        }).attach();
    }


    public void setFragements(ViewPagerAdapter adapter) {
        if (firebaseUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
            userRef.child("userType").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userType = dataSnapshot.getValue(String.class);
                    if (userType != null && userType.equals("premium")) {
                        createPremiumView(adapter);

                    } else {
                        createNormalView(adapter);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // handle the error
                }
            });
        }
    }

    public void setMapsFragement(ViewPagerAdapter adapter) {

        adapter.addFragment(new MapsFragment(), getResources().getDrawable(R.drawable.ic_arking));

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (adapter.createFragment(position) instanceof MapsFragment) {
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                    params.gravity = Gravity.START | Gravity.BOTTOM;
                    fab.setLayoutParams(params);
                } else {
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                    params.gravity = Gravity.END | Gravity.BOTTOM;
                    fab.setLayoutParams(params);
                }
            }
        });
        adapter.notifyDataSetChanged();

    }

    public void createPremiumView(ViewPagerAdapter adapter) {
        adapter.addFragment(new ChatFragment(), getResources().getDrawable(R.drawable.ic_chat));
        adapter.addFragment(new CallsFragment(), getResources().getDrawable(R.drawable.ic_phone));
        adapter.addFragment(new QrCodeFragment(), getResources().getDrawable(R.drawable.qr_scanner));
        adapter.addFragment(new ProfileFragement(), getResources().getDrawable(R.drawable.ic_profile));
        setMapsFragement(adapter);


    }

    public void createNormalView(ViewPagerAdapter adapter) {
        adapter.addFragment(new ChatFragment(), getResources().getDrawable(R.drawable.ic_chat));
        adapter.addFragment(new CallsFragment(), getResources().getDrawable(R.drawable.ic_phone));
        adapter.addFragment(new QrCodeFragment(), getResources().getDrawable(R.drawable.qr_scanner));
        adapter.addFragment(new ProfileFragement(), getResources().getDrawable(R.drawable.ic_profile));

    }


    class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<Drawable> mFragmentIconList = new ArrayList<>();

        public ViewPagerAdapter(FragmentActivity fragment) {
            super(fragment);
        }

        public void addFragment(Fragment fragment, Drawable icon) {
            mFragmentList.add(fragment);
            mFragmentIconList.add(icon);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return mFragmentList.size();
        }
    }

    public void disableSwipe() {
        viewPager2.setUserInputEnabled(false);
    }

    public void enableSwipe() {
        viewPager2.setUserInputEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem logoutItem = menu.findItem(R.id.logout);
        logoutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                fireAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;
            }
        });


        return true;
    }


}