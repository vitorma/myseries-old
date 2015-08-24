package mobi.myseries.gui.shared;

import mobi.myseries.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastBuilder {
    private Context context;
    private String message;

    public ToastBuilder(Context context) {
        this.context = context;
    }

    public ToastBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public ToastBuilder setMessage(int messageResourceId) {
        this.message = this.context.getString(messageResourceId);
        return this;
    }

    public Toast build() {
        Toast toast = new Toast(this.context);

        toast.setView(this.toastView());
        toast.setDuration(Toast.LENGTH_SHORT);

        return toast;
    }

    private View toastView() {
        View toastView = this.layoutInflater().inflate(R.layout.toast, null);

        TextView messageView = (TextView) toastView.findViewById(R.id.message);
        messageView.setText(this.message);

        return toastView;
    }

    private LayoutInflater layoutInflater() {
        return (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
