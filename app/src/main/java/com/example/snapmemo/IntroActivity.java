package com.example.snapmemo;

// Nama : Wildan Yusup
// Nim : 10120048
// Kelas : IF2

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    private IntroViewPagerAdapter introViewPagerAdapter;
    private Button btnGetStarted;
    private Animation btnAnim;
    private LinearLayout layoutDots;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Jika sudah login, langsung ke MainActivity
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();
            return;
        }

        if (notePrefData()) {
            // Jika sudah pernah menampilkan IntroActivity sebelumnya, langsung ke LoginActivity
            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_intro);

        // Sembunyikan action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Inisialisasi tampilan
        btnGetStarted = findViewById(R.id.btn_get_started);
        layoutDots = findViewById(R.id.layoutDots);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);

        // Isi layar daftar
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("SnapMemo", "Teman catatan Anda yang cepat dan praktis. Tangkap, simpan, dan atur ide-ide brilian dalam sekejap.", R.drawable.img1));
        mList.add(new ScreenItem("Tangkap Setiap Momen", "Foto, suara, atau tulisan - SnapMemo memudahkan Anda mencatat inspirasi di mana saja, kapan saja.", R.drawable.img2));
        mList.add(new ScreenItem("Akses Mudah", "Nikmati akses instan ke catatan Anda melalui semua perangkat Anda. Tetap sinkron dan produktif di mana pun Anda berada.", R.drawable.img3));

        // Atur ViewPager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // Set indikator titik pertama menjadi aktif
        updateDots(0);

        // Tambahkan pendengar perubahan halaman pada ViewPager
        screenPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == mList.size() - 1) {
                    // Jika halaman terakhir ditampilkan, tampilkan tombol "Get Started" dan sembunyikan indikator titik
                    btnGetStarted.setVisibility(View.VISIBLE);
                    layoutDots.setVisibility(View.INVISIBLE);
                    btnGetStarted.setAnimation(btnAnim);
                } else {
                    // Jika bukan halaman terakhir, tampilkan indikator titik dan sembunyikan tombol "Get Started"
                    layoutDots.setVisibility(View.VISIBLE);
                    btnGetStarted.setVisibility(View.INVISIBLE);

                    // Perbarui indikator titik
                    updateDots(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Klik tombol "Get Started"
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    Intent loginIntent = new Intent(IntroActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    Intent mainIntent = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }

                // Simpan preferensi bahwa layar intro telah ditampilkan
                savePrefsData();

                // Selesaikan IntroActivity
                finish();
            }
        });
    }

    private void updateDots(int currentPage) {
        dots = new ImageView[introViewPagerAdapter.getCount()];

        layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            if (i == currentPage) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dot));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_dot));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);
            layoutDots.addView(dots[i], params);
        }
    }

    private boolean notePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        return pref.getBoolean("isIntroOpnend", false);
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend", true);
        editor.apply();
    }
}
