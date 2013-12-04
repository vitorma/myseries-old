package mobi.myseries.gui.shared;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mobi.myseries.R;
import mobi.myseries.shared.FilesUtil;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class FileChooserDialogBuilder {
    private static final int DEFAULT_TITLE_RESOURCE = R.string.restore_choose_backup_to_restore;

    private final Context context;
    private int titleResource = DEFAULT_TITLE_RESOURCE;
    private String[] fileList;
    private File currentPath;

    private TextView titleView;
    private final ArrayList<RadioButton> optionViews;

    private String fileEndsWith;

    private OnChooseListener onChooseListener;
    protected String selectedFile;

    private RadioGroup radioGroup;

    public FileChooserDialogBuilder(Context context) {
        this.context = context;
        this.optionViews = new ArrayList<RadioButton>();
    }

    public FileChooserDialogBuilder setTitle(int stringResource) {
        this.titleResource = stringResource;
        return this;
    }

    public FileChooserDialogBuilder fileEndsWith(String endsWith) {
        this.fileEndsWith = endsWith;
        return this;
    }

    public FileChooserDialogBuilder setOnChooseListener(
            OnChooseListener listener) {
        this.onChooseListener = listener;
        return this;
    }

    public Dialog build() {
        Dialog dialog = new Dialog(this.context, R.style.MySeriesTheme_Dialog);

        dialog.setContentView(R.layout.dialog_file_chooser);

        this.setUpTitleFor(dialog);
        this.setUpOptionViewsFor(dialog);
        this.setUpCancelButtonFor(dialog);
        this.setUpOkButtonFor(dialog);

        return dialog;
    }

    private void setUpTitleFor(Dialog dialog) {
        this.titleView = (TextView) dialog.findViewById(R.id.title);

        this.titleView.setText(this.context.getText(this.titleResource));
    }

    private void setUpOptionViewsFor(Dialog dialog) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        this.radioGroup = (RadioGroup) dialog
                .findViewById(R.id.files);

        fileList = FilesUtil.listFilesOfDirectory(currentPath, fileEndsWith);
        List<String> list = Arrays.asList(fileList);
        Collections.reverse(list);
        fileList = (String[]) list.toArray();
            for (int i = 0; i < fileList.length; i++) {
            View v = inflater
                    .inflate(R.layout.dialog_file_chooser_option, null);
            final RadioButton fileName = (RadioButton) v
                    .findViewById(R.id.radioButton);

            fileName.setText(fileList[i]);
            fileName.setId(i);

            fileName.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    radioGroup.check(fileName.getId());
                }
            });

            radioGroup.addView(v);

            this.optionViews.add(fileName);
        }
            radioGroup.check(0);
    }

    private void setUpCancelButtonFor(final Dialog dialog) {
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setUpOkButtonFor(final Dialog dialog) {
        Button okButton = (Button) dialog.findViewById(R.id.okButton);

        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectedFile = fileList[radioGroup.getCheckedRadioButtonId()];
                File chosenFile = getChosenFile(selectedFile);
                FileChooserDialogBuilder.this.onChooseListener
                        .onChoose(chosenFile);
            }
        });
    }

    private File getChosenFile(String fileChosen) {
        if(fileChosen != null)
            return new File(currentPath, fileChosen);
        return null;

    }

    public static interface OnChooseListener {
        public void onChoose(File chosenFile);
    }

    public FileChooserDialogBuilder setDefaultPath(File path) {
        if (!path.exists())
            path = Environment.getExternalStorageDirectory();
        fileList = FilesUtil.listFilesOfDirectory(path, fileEndsWith);
        this.currentPath = path;
        return this;
    }

    public FileChooserDialogBuilder setEmptyMessage(int stringResource) {
        return this;
    }
}