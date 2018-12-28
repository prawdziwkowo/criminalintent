package pl.it_developers.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "dialog_date";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    private Crime crime;
    private File photoFile;

    private EditText titleField;
    private Button dateButton;
    private CheckBox solvedCheckbox;
    private Button reportButton;
    private Button suspectButton;
    private ImageButton photoButton;
    private ImageView photoView;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        crime = CrimeLab.get(getActivity()).getCrime(crimeId);

        photoFile = CrimeLab.get(getActivity()).getPhotoFile(crime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(crime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(pl.it_developers.android.criminalintent.R.layout.fragment_crime, container, false);

        titleField = (EditText) view.findViewById(pl.it_developers.android.criminalintent.R.id.crime_title);
        titleField.setText(crime.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dateButton = (Button) view.findViewById(pl.it_developers.android.criminalintent.R.id.crime_date);
        updateDate();
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        solvedCheckbox = (CheckBox) view.findViewById(pl.it_developers.android.criminalintent.R.id.crime_solved);
        solvedCheckbox.setChecked(crime.isSolved());
        solvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
            }
        });

        reportButton = (Button) view.findViewById(pl.it_developers.android.criminalintent.R.id.crime_report);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent =   ShareCompat.IntentBuilder.from(getActivity())
                        .setChooserTitle(getString(pl.it_developers.android.criminalintent.R.string.send_report))
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(pl.it_developers.android.criminalintent.R.string.crime_report_subject))
                        .getIntent();
                if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(shareIntent);
                }
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        suspectButton = (Button) view.findViewById(pl.it_developers.android.criminalintent.R.id.crime_suspect);
        suspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (crime.getSuspect() != null) {
            suspectButton.setText(crime.getSuspect());
        }

        //sprawdzanie czy jest aplikacja do zarządzania kontaktami
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            suspectButton.setEnabled(false);
        }

        photoButton = (ImageButton) view.findViewById(pl.it_developers.android.criminalintent.R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean catTakePhoto = photoFile != null && captureImage.resolveActivity(packageManager) != null;
        photoButton.setEnabled(catTakePhoto);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(
                        getActivity(),
                        "pl.it_developers.android.criminalintent.fileprovider",
                        photoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivites = getActivity()
                        .getPackageManager()
                        .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity: cameraActivites) {
                    getActivity().grantUriPermission(
                            activity.activityInfo.packageName,
                            uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        photoView = (ImageView) view.findViewById(pl.it_developers.android.criminalintent.R.id.crime_photo);
        updatePhotoView();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME
            };

            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            try {
                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                crime.setSuspect(suspect);
                suspectButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(
                    getActivity(),
                    "pl.it_developers.android.criminalintent.fileprovider",
                    photoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    private void updateDate() {
        dateButton.setText(crime.getDate().toString());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (crime.isSolved()) {
            solvedString = getString(pl.it_developers.android.criminalintent.R.string.crime_report_solved);
        } else {
            solvedString = getString(pl.it_developers.android.criminalintent.R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,
                crime.getDate()).toString();
        String suspect = crime.getSuspect();
        if (suspect == null) {
            suspect = getString(pl.it_developers.android.criminalintent.R.string.crime_report_no_suspect);
        } else {
            suspect = getString(pl.it_developers.android.criminalintent.R.string.crime_report_suspect, suspect);
        }
        String report = getString(pl.it_developers.android.criminalintent.R.string.crime_report,
                crime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    private void updatePhotoView() {
        if (photoFile == null || !photoFile.exists()) {
            photoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
            photoView.setImageBitmap(bitmap);
        }
    }
}
