package edu.uncc.assignment08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.uncc.assignment08.databinding.FragmentLoginBinding;


public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }
    FragmentLoginBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.editTextEmailAddress.getText().toString();
                String password = binding.editTextPassword.getText().toString();
                if(email.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid email!", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid password!", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getActivity(), "Sign-Up Successful",
                                        Toast.LENGTH_SHORT).show();
                                mListener.authSuccessful();
                            }else {
                                Toast.makeText(getActivity(), task.getException().getLocalizedMessage()
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        binding.buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.register();
            }
        });

        getActivity().setTitle("Login");
    }

    LoginListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (LoginListener) context;
    }

    interface LoginListener {
        void authSuccessful();
        void register();
    }
}