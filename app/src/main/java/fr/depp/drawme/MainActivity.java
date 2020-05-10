package fr.depp.drawme;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import fr.depp.drawme.ui.fragments.AuthFragment;
import fr.depp.drawme.ui.fragments.MainFragment;
import fr.depp.drawme.utils.FragmentHelper;
import fr.depp.drawme.utils.OnBackPressed;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_account) {
            FragmentHelper.displayFragment(getSupportFragmentManager(), new AuthFragment(), true);
            return true;
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment instanceof OnBackPressed) {
            ((OnBackPressed) fragment).onBackPressed(goBack -> {
                if (goBack) super.onBackPressed();
            });
        }
        else {
            super.onBackPressed();
        }
    }
}
