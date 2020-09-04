package ca.printf.dndb.view;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import ca.printf.dndb.R;
import com.google.android.material.navigation.NavigationView;

public class RootActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String FRAG_DEFAULT = "FRAG_DEFAULT";
    private Fragment content_frag;
    private DrawerLayout drw;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_activity);

        Toolbar tb = findViewById(R.id.nav_toolbar);
        setSupportActionBar(tb);

        drw = findViewById(R.id.nav_root);
        ActionBarDrawerToggle drw_tog =
                new ActionBarDrawerToggle(this, drw, tb, R.string.app_name, R.string.app_name);
        drw.addDrawerListener(drw_tog);
        drw.openDrawer(GravityCompat.START);
        drw_tog.syncState();

        ((NavigationView)findViewById(R.id.nav_sidebar)).setNavigationItemSelectedListener(this);

        content_frag = new DefaultFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_frame, content_frag, FRAG_DEFAULT)
                .commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        menu.findItem(R.id.menu_spells).setVisible(false);
        return true;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        return menuAction(item);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return menuAction(item);
    }

    private boolean menuAction(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_spells :
                openContentFragment(new SpellsListFragment());
                break;
            case R.id.menu_settings :
                openContentFragment(new SettingsFragment());
                break;
            case R.id.menu_about :
                createAboutDialog().show();
                break;
            case R.id.menu_exit :
                this.finish();
                break;
            default : break;
        }
        return true;
    }

    private AlertDialog createAboutDialog() {
        AlertDialog.Builder about = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.about_dialog, null, false);
        ((TextView)v.findViewById(R.id.about_text)).setMovementMethod(LinkMovementMethod.getInstance());
        about.setView(v);
        return about.create();
    }

    private void openContentFragment(Fragment contentFrag) {
        ((FrameLayout)findViewById(R.id.content_frame)).removeAllViewsInLayout();
        content_frag = contentFrag;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, content_frag)
                .addToBackStack(null)
                .commit();
        if(drw.isDrawerOpen(GravityCompat.START))
            drw.closeDrawer(GravityCompat.START, true);
    }
}