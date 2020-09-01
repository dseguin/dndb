package ca.printf.dndb.view;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ca.printf.dndb.R;

public class DefaultFragment extends Fragment {
    public DefaultFragment() {}
    public void onCreate(Bundle b) {super.onCreate(b);}
    public View onCreateView(LayoutInflater li, ViewGroup v, Bundle b) {
        return li.inflate(R.layout.fragment_default, v, false);
    }
}