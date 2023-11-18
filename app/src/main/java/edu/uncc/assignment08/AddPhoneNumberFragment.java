package edu.uncc.assignment08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.uncc.assignment08.databinding.ContactListItemBinding;
import edu.uncc.assignment08.databinding.FragmentAddPhoneNumberBinding;


public class AddPhoneNumberFragment extends Fragment {


    public AddPhoneNumberFragment() {
        // Required empty public constructor
    }

    FragmentAddPhoneNumberBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddPhoneNumberBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneNumber phoneNumber = new PhoneNumber();

                String number = binding.editTextPhone.getText().toString();
                String type = "";
                int checkedRadioButtonId = binding.radioGroup.getCheckedRadioButtonId();

                if (checkedRadioButtonId == binding.radioButtonCell.getId()){
                    type = "Cell";
                } else if (checkedRadioButtonId == binding.radioButtonHome.getId()){
                    type = "Home";
                } else if (checkedRadioButtonId == binding.radioButtonWork.getId()) {
                    type = "Work";
                }else if (checkedRadioButtonId == binding.radioButtonOther.getId()) {
                    type = "Other";
                }

                if (number.isEmpty()){
                    Toast.makeText(getActivity(), "input number", Toast.LENGTH_SHORT).show();
                }else {
                    Log.d("Demo", "onClick: " + phoneNumber);
                    phoneNumber.setNumber(number);
                    phoneNumber.setType(type);
                    mListener.addPhoneNumberToContact(phoneNumber);
                }
            }
        });



        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goBackToCreateContact();
            }
        });
    }



    AddPhoneNumberListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (AddPhoneNumberListener) context;
    }

    interface AddPhoneNumberListener{
        void addPhoneNumberToContact(PhoneNumber phoneNumber);
        void goBackToCreateContact();
    }

}