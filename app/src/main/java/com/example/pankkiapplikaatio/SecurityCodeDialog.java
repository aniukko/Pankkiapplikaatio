package com.example.pankkiapplikaatio;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Random;

public class SecurityCodeDialog extends AppCompatDialogFragment {
    String logUser;
    private TextInputLayout securityCode;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_security_code, null);

        securityCode = view.findViewById(R.id.securityCode);
        logUser = getArguments().getString("loguser");

        Random random = new Random();
        final int ranNumber = random.nextInt(999999 - 100000) + 100000;

        builder.setView(view)
                .setTitle("Give the following code\n" + ranNumber)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Integer.parseInt(securityCode.getEditText().getText().toString()) == ranNumber) {
                            securityCode.setError(null);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("loguser", logUser);
                            startActivity(intent);
                        } else {
                            securityCode.setError("Wrong code");
                            Toast.makeText(getActivity(), "Wrong security code", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(), LogInActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), LogInActivity.class);
                        startActivity(intent);
                    }
                });
        return builder.create();
    }
}
