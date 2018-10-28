package ru.sergeykamyshov.weekplanner.ui.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.ui.about.AboutFragment;
import ru.sergeykamyshov.weekplanner.ui.cardslist.archive.ArchiveWeekFragment;
import ru.sergeykamyshov.weekplanner.ui.cardslist.current.CurrentWeekFragment;
import ru.sergeykamyshov.weekplanner.ui.cardslist.next.NextWeekFragment;
import ru.sergeykamyshov.weekplanner.data.prefs.MainActivitySharedPreferencesUtils;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private MainActivitySharedPreferencesUtils prefsUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsUtils = new MainActivitySharedPreferencesUtils(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mNavigationView = findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(new NavigationItemClickListener());

        mDrawerLayout = findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        // При смене ориентации экрана, необходимо востанавливать текущий фрагмент
        if (getSupportFragmentManager().findFragmentById(R.id.frame_content) != null) {
            getSupportFragmentManager().popBackStack();
            setTitle(prefsUtils.getTitleFromPrefs());
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frame_content, CurrentWeekFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setAndSaveTitle(String title) {
        setTitle(title);
        prefsUtils.writeTitleToPrefs(title);
    }

    private class NavigationItemClickListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_item_current_week:
                    setAndSaveTitle(getString(R.string.app_name));
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_content, CurrentWeekFragment.newInstance())
                            .commit();
                    break;
                case R.id.nav_item_next_week:
                    setAndSaveTitle(getString(R.string.title_next_week));
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_content, NextWeekFragment.newInstance())
                            .commit();
                    break;
                case R.id.nav_item_archive:
                    setAndSaveTitle(getString(R.string.title_archive));
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_content, ArchiveWeekFragment.newInstance())
                            .commit();
                    break;
                case R.id.nav_item_about:
                    setAndSaveTitle(getString(R.string.title_about));
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_content, AboutFragment.newInstance())
                            .commit();
                    break;
            }
            mDrawerLayout.closeDrawers();
            return true;
        }
    }

}
