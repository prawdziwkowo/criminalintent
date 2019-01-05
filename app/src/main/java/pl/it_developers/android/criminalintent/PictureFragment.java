package pl.it_developers.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.UUID;

public class PictureFragment extends DialogFragment {
    private static final String ARG_UUID = "UUID";
    private ImageView image;

    public static PictureFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_UUID, uuid);

        PictureFragment fragment = new PictureFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_UUID);
        Crime crime = CrimeLab.get(getActivity()).getCrime(crimeId);

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_picture, null);

        image = (ImageView) view.findViewById(R.id.crime_image_view);
        Bitmap bitmap = PictureUtils.getBitmap(CrimeLab.get(getActivity()).getPhotoFile(crime).getPath());
        image.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.picture_title)
                .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
    }
}
