package ca.printf.dndb.view;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.PrintWriter;
import java.io.StringWriter;
import ca.printf.dndb.R;

public class ErrorPage extends Fragment {
    public static final String ERROR_HEADER = "header";
    public static final String ERROR_MSG = "msg";

    public void onCreate(Bundle b) {super.onCreate(b);}

    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        View v = li.inflate(R.layout.error_page, vg, false);
        b = getArguments();
        if(b == null)
            return v;
        String header = b.getString(ERROR_HEADER, null);
        String msg = b.getString(ERROR_MSG, null);
        if(header != null)
            ((TextView)v.findViewById(R.id.error_header_txt)).setText(header);
        if(msg != null)
            ((TextView)v.findViewById(R.id.error_body_txt)).setText(msg);
        return v;
    }

    public static void errorScreen(FragmentManager fragManager, String header, String msg) {
        Bundle b = new Bundle();
        b.putString(ErrorPage.ERROR_HEADER, header);
        b.putString(ErrorPage.ERROR_MSG, msg);
        Fragment errfrag = new ErrorPage();
        errfrag.setArguments(b);
        fragManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.content_frame, errfrag)
                .commit();
    }

    public static void errorScreen(FragmentManager fragManager, String header, Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        errorScreen(fragManager, header, e.getMessage() + "\n" + sw.toString());
    }
}