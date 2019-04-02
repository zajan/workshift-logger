package com.ajna.workshiftlogger.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ajna.workshiftlogger.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InvoicesListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InvoicesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvoicesListFragment extends Fragment {
    // == constants ==
    private static final String TAG = "InvoicesListFragment";
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 111;

    // == fields ==
    private File[] files;
    private List<String> fileNames = new ArrayList<>();
    private ListView lvFiles;
    private ArrayAdapter<String> arrayAdapter;
    private OnFragmentInteractionListener mListener;


    // == constructors and newInstance() ==
    public InvoicesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InvoicesListFragment.
     */
    public static InvoicesListFragment newInstance() {
        return new InvoicesListFragment();
    }


    // == callback methods ==
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getContext();
        if(context == null) return;
        arrayAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                fileNames);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_invoices_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvFiles = view.findViewById(R.id.lv_invoices);
        lvFiles.setAdapter(arrayAdapter);

        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String fileName = (String) adapterView.getItemAtPosition(position);
                previewPdf(fileName);
            }
        });
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onNewInvoiceClicked();
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Activity activity = getActivity();
        if(activity == null) return;
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            listFiles();
            arrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    listFiles();
                    arrayAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getContext(), "WRITE_EXTERNAL Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // == private methods ==
    private void listFiles() {
        String path = Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.folder_name);
        File directory = new File(path);
        files = directory.listFiles();
        fileNames.clear();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
            }
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {

                Context context = getContext();
                if(context == null) return;
                new AlertDialog.Builder(context)
                        .setMessage(R.string.allow_storage_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();
            }
        }
    }

    private void previewPdf(String fileName) {

        String path = Environment.getExternalStorageDirectory() + "/" + getString(R.string.folder_name) + "/" + fileName;
        File file = new File(path);
        Context context = getContext();
        if(context == null) return;
        Uri uri = FileProvider.getUriForFile(context, getString(R.string.file_provider_authority), file);

        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            intent.setData(uri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {

            intent.setDataAndType(uri, "application/pdf");
            intent = Intent.createChooser(intent, "Open File");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    // == interface ==

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onNewInvoiceClicked();
    }
}
