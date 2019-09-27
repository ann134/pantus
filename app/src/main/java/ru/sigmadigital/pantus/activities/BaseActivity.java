package ru.sigmadigital.pantus.activities;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    protected int getFragmentContainer() {
        return 0;
    }

    protected void loadFragment(Fragment fragment, String title, boolean stack) {

        Fragment currFragment = null;
        if (getSupportFragmentManager() != null && getSupportFragmentManager().getFragments().size() > 0) {
            currFragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size() - 1);
        } else {
            loadFragment(fragment, title, stack, getFragmentContainer());
        }

        if (currFragment != null && !currFragment.getClass().equals(fragment.getClass())) {
            loadFragment(fragment, title, stack, getFragmentContainer());
        }
    }

    protected void loadFragment(Fragment fragment, String title, boolean stack, int container) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(container, fragment);
        if (stack) fragmentTransaction.addToBackStack(title);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            this.finish();
        } else {
            //super.onBackPressed();
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().executePendingTransactions();
        }


    }

}