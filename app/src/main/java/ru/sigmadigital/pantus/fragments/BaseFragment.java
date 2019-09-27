package ru.sigmadigital.pantus.fragments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public abstract class BaseFragment extends Fragment {

    void loadFragment(Fragment fragment, String title, boolean stack) {
        Fragment currFragment = null;
        if (getFragmentManager() != null && getFragmentManager().getFragments().size() > 0) {
            currFragment = getFragmentManager().getFragments().get(getFragmentManager().getFragments().size() - 1);
        } else {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction;
            if (fragmentManager != null) {
                fragmentTransaction = fragmentManager
                        .beginTransaction();
                fragmentTransaction.replace(getFragmentContainer(),
                        fragment);
                if (stack) fragmentTransaction.addToBackStack(title);
                fragmentTransaction.commit();
            }
        }

        if (currFragment != null && !currFragment.getClass().equals(fragment.getClass())) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction;
            if (fragmentManager != null) {
                fragmentTransaction = fragmentManager
                        .beginTransaction();
                fragmentTransaction.replace(getFragmentContainer(),
                        fragment);
                if (stack) fragmentTransaction.addToBackStack(title);
                fragmentTransaction.commit();
            }
        }
    }

    protected void loadFragment(Fragment fragment, String title, boolean stack, int fragmentContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction;
        if (fragmentManager != null) {
            fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(fragmentContainer,
                    fragment);
            if (stack) fragmentTransaction.addToBackStack(title);
            fragmentTransaction.commit();
        }
    }

    protected abstract int getFragmentContainer();
}